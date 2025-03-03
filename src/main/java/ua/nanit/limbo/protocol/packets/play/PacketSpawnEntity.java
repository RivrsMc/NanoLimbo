package ua.nanit.limbo.protocol.packets.play;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.nanit.limbo.model.Location;
import ua.nanit.limbo.model.Vector;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.EntityType;
import ua.nanit.limbo.protocol.registry.Version;

@Data
@AllArgsConstructor
public class PacketSpawnEntity implements PacketOut {

    private int id;
    private UUID uniqueId;
    private Location location;
    private EntityType type;
    private float headYaw;
    private int data;
    private Vector velocity;

    public PacketSpawnEntity(int id, UUID uniqueId, Location location, EntityType type, float headYaw) {
        this(id, uniqueId, location, type, headYaw, 0, Vector.ZERO);
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(this.id);
        msg.writeUuid(this.uniqueId);
        msg.writeVarInt(this.type.getId());
        msg.writeDouble(this.location.getX());
        msg.writeDouble(this.location.getY());
        msg.writeDouble(this.location.getZ());
        msg.writeFloat(this.location.getPitch());
        msg.writeFloat(this.location.getYaw());
        msg.writeInt(this.data);
        msg.writeShort((int) this.velocity.getX());
        msg.writeShort((int) this.velocity.getY());
        msg.writeShort((int) this.velocity.getZ());
    }
}
