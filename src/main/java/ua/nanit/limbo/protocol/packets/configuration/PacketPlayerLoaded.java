package ua.nanit.limbo.protocol.packets.configuration;

import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketIn;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

public class PacketPlayerLoaded implements PacketIn {

    private String locale;

    public String getLocale() {
        return locale;
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        locale = msg.readString();
    }

    @Override
    public void handle(ClientConnection conn, LimboServer server) {
        server.getPacketHandler().handle(conn, this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
