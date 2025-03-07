/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.connection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jetbrains.annotations.NotNull;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import ua.nanit.limbo.connection.pipeline.PacketDecoder;
import ua.nanit.limbo.connection.pipeline.PacketEncoder;
import ua.nanit.limbo.model.Location;
import ua.nanit.limbo.model.Vector;
import ua.nanit.limbo.model.player.GameMode;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.Packet;
import ua.nanit.limbo.protocol.PacketSnapshot;
import ua.nanit.limbo.protocol.packets.login.PacketDisconnect;
import ua.nanit.limbo.protocol.packets.play.PacketKeepAlive;
import ua.nanit.limbo.protocol.packets.play.PacketPlayerInfo;
import ua.nanit.limbo.protocol.packets.play.PacketSpawnEntity;
import ua.nanit.limbo.protocol.registry.EntityType;
import ua.nanit.limbo.protocol.registry.State;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;
import ua.nanit.limbo.util.UuidUtil;

public class ClientConnection extends ChannelInboundHandlerAdapter {

    private final LimboServer server;
    private final Channel channel;
    @Getter
    private final GameProfile gameProfile;

    private final PacketDecoder decoder;
    private final PacketEncoder encoder;

    private State state;
    @Getter
    private Version clientVersion;
    @Getter
    private SocketAddress address;

    private int velocityLoginMessageId = -1;

    public ClientConnection(Channel channel, LimboServer server, PacketDecoder decoder, PacketEncoder encoder) {
        this.server = server;
        this.channel = channel;
        this.decoder = decoder;
        this.encoder = encoder;
        this.address = channel.remoteAddress();
        this.gameProfile = new GameProfile();
    }

    public UUID getUuid() {
        return gameProfile.getUuid();
    }

    public String getUsername() {
        return gameProfile.getUsername();
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        if (state.equals(State.PLAY) || state.equals(State.CONFIGURATION)) {
            server.getConnections().removeConnection(this);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (channel.isActive()) {
            Log.error("Unhandled exception: ", cause);
        }
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
        handlePacket(msg);
    }

    public void handlePacket(Object packet) {
        if (packet instanceof Packet) {
            ((Packet) packet).handle(this, server);
        }
    }

    public void fireLoginSuccess() {
        if (server.getConfig().getInfoForwarding().isModern() && velocityLoginMessageId == -1) {
            disconnectLogin("You need to connect with Velocity");
            return;
        }

        sendPacket(PacketSnapshots.PACKET_LOGIN_SUCCESS);

        server.getConnections().addConnection(this);

        // Preparing for configuration mode
        if (clientVersion.moreOrEqual(Version.V1_20_2)) {
            updateEncoderState(State.CONFIGURATION);
            return;
        }

        spawnPlayer();
    }

    public void spawnPlayer() {
        updateState(State.PLAY);

        Runnable sendPlayPackets = () -> {
            writePacket(PacketSnapshots.PACKET_JOIN_GAME);
            writePacket(PacketSnapshots.PACKET_PLAYER_ABILITIES);

            if (clientVersion.less(Version.V1_9)) {
                writePacket(PacketSnapshots.PACKET_PLAYER_POS_AND_LOOK_LEGACY);
            } else {
                writePacket(PacketSnapshots.PACKET_PLAYER_POS_AND_LOOK);
            }

            if (clientVersion.moreOrEqual(Version.V1_19_3))
                writePacket(PacketSnapshots.PACKET_SPAWN_POSITION);

            if (server.getConfig().isUsePlayerList() || clientVersion.equals(Version.V1_16_4))
                writePacket(PacketSnapshots.PACKET_PLAYER_INFO);

            if (clientVersion.moreOrEqual(Version.V1_13)) {
                writePacket(PacketSnapshots.PACKET_DECLARE_COMMANDS);

                if (PacketSnapshots.PACKET_PLUGIN_MESSAGE != null)
                    writePacket(PacketSnapshots.PACKET_PLUGIN_MESSAGE);
            }

            if (PacketSnapshots.PACKET_BOSS_BAR != null && clientVersion.moreOrEqual(Version.V1_9))
                writePacket(PacketSnapshots.PACKET_BOSS_BAR);

            if (PacketSnapshots.PACKET_JOIN_MESSAGE != null)
                writePacket(PacketSnapshots.PACKET_JOIN_MESSAGE);

            if (PacketSnapshots.PACKET_TITLE_TITLE != null && clientVersion.moreOrEqual(Version.V1_8))
                writeTitle();

            if (PacketSnapshots.PACKET_HEADER_AND_FOOTER != null && clientVersion.moreOrEqual(Version.V1_8))
                writePacket(PacketSnapshots.PACKET_HEADER_AND_FOOTER);

            if (clientVersion.moreOrEqual(Version.V1_20_3)) {
                writePacket(PacketSnapshots.PACKET_START_WAITING_CHUNKS);

                for (PacketSnapshot chunk : PacketSnapshots.PACKETS_EMPTY_CHUNKS) {
                    writePacket(chunk);
                }
            }

            sendKeepAlive();
        };

        if (clientVersion.lessOrEqual(Version.V1_7_6)) {
            this.channel.eventLoop().schedule(sendPlayPackets, 100, TimeUnit.MILLISECONDS);
        } else {
            sendPlayPackets.run();
        }
        this.channel.eventLoop().schedule(() -> {
            UUID uniqueId = UUID.randomUUID();
            PacketPlayerInfo packetPlayerInfo = new PacketPlayerInfo(
                    uniqueId,
                    "jeb_",
                    GameMode.SURVIVAL
            );
            sendPacket(packetPlayerInfo);

            PacketSpawnEntity packetSpawnEntity = new PacketSpawnEntity(
                    10,
                    uniqueId,
                    new Location(2, 400, 0),
                    EntityType.PLAYER,
                    0,
                    0,
                    Vector.ZERO
            );
            sendPacket(packetSpawnEntity);
        }, 1, TimeUnit.SECONDS);

    }

    public void onLoginAcknowledgedReceived() {
        updateState(State.CONFIGURATION);

        if (PacketSnapshots.PACKET_PLUGIN_MESSAGE != null)
            writePacket(PacketSnapshots.PACKET_PLUGIN_MESSAGE);

        if (clientVersion.moreOrEqual(Version.V1_20_5)) {
            for (PacketSnapshot packet : PacketSnapshots.PACKETS_REGISTRY_DATA) {
                writePacket(packet);
            }
        } else {
            writePacket(PacketSnapshots.PACKET_REGISTRY_DATA);
        }

        sendPacket(PacketSnapshots.PACKET_FINISH_CONFIGURATION);
    }

    public void disconnectLogin(String reason) {
        if (isConnected() && state == State.LOGIN) {
            PacketDisconnect disconnect = new PacketDisconnect();
            disconnect.setReason(reason);
            sendPacketAndClose(disconnect);
        }
    }

    public void writeTitle() {
        if (clientVersion.moreOrEqual(Version.V1_17)) {
            writePacket(PacketSnapshots.PACKET_TITLE_TITLE);
            writePacket(PacketSnapshots.PACKET_TITLE_SUBTITLE);
            writePacket(PacketSnapshots.PACKET_TITLE_TIMES);
        } else {
            writePacket(PacketSnapshots.PACKET_TITLE_LEGACY_TITLE);
            writePacket(PacketSnapshots.PACKET_TITLE_LEGACY_SUBTITLE);
            writePacket(PacketSnapshots.PACKET_TITLE_LEGACY_TIMES);
        }
    }

    public void sendKeepAlive() {
        if (state.equals(State.PLAY)) {
            PacketKeepAlive keepAlive = new PacketKeepAlive();
            keepAlive.setId(ThreadLocalRandom.current().nextLong());
            sendPacket(keepAlive);
        }
    }

    public void sendPacket(Object packet) {
        if (isConnected())
            channel.writeAndFlush(packet, channel.voidPromise());
    }

    public void sendPacketAndClose(Object packet) {
        if (isConnected())
            channel.writeAndFlush(packet).addListener(ChannelFutureListener.CLOSE);
    }

    public void writePacket(Object packet) {
        if (isConnected())
            channel.write(packet, channel.voidPromise());
    }

    public boolean isConnected() {
        return channel.isActive();
    }

    public void updateState(State state) {
        this.state = state;
        decoder.updateState(state);
        encoder.updateState(state);
    }

    public void updateEncoderState(State state) {
        encoder.updateState(state);
    }

    public void updateVersion(Version version) {
        clientVersion = version;
        decoder.updateVersion(version);
        encoder.updateVersion(version);
    }

    public void setAddress(String host) {
        this.address = new InetSocketAddress(host, ((InetSocketAddress) this.address).getPort());
    }

    boolean checkBungeeGuardHandshake(String handshake) {
        String[] split = handshake.split("\00");

        if (split.length != 4)
            return false;

        String socketAddressHostname = split[1];
        UUID uuid = UuidUtil.fromString(split[2]);
        JsonArray arr;

        try {
            arr = JsonParser.array().from(split[3]);
        } catch (JsonParserException e) {
            return false;
        }

        String token = null;

        for (Object obj : arr) {
            if (obj instanceof JsonObject) {
                JsonObject prop = (JsonObject) obj;
                if (prop.getString("name").equals("bungeeguard-token")) {
                    token = prop.getString("value");
                    break;
                }
            }
        }

        if (!server.getConfig().getInfoForwarding().hasToken(token))
            return false;

        setAddress(socketAddressHostname);
        gameProfile.setUuid(uuid);

        Log.debug("Successfully verified BungeeGuard token");

        return true;
    }

    int getVelocityLoginMessageId() {
        return velocityLoginMessageId;
    }

    void setVelocityLoginMessageId(int velocityLoginMessageId) {
        this.velocityLoginMessageId = velocityLoginMessageId;
    }

    boolean checkVelocityKeyIntegrity(ByteMessage buf) {
        byte[] signature = new byte[32];
        buf.readBytes(signature);
        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), data);

        List<byte[]> secretKeys = server.getConfig().getInfoForwarding().getSecretKeys();
        boolean validKey = false;

        for (byte[] secretKey : secretKeys) {
            try {
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
                byte[] mySignature = mac.doFinal(data);
                if (MessageDigest.isEqual(signature, mySignature)) {
                    validKey = true;
                    break;
                }
            } catch (InvalidKeyException | java.security.NoSuchAlgorithmException e) {
                throw new AssertionError(e);
            }
        }

        int version = buf.readVarInt();
        if (version != 1)
            throw new IllegalStateException("Unsupported forwarding version " + version + ", wanted " + '\001');
        return validKey;
    }
}
