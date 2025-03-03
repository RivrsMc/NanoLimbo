package ua.nanit.limbo.protocol.packets.play;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nanit.limbo.model.Location;
import ua.nanit.limbo.model.Vector;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.EntityType;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.util.MathUtil;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketSpawnEntity implements PacketOut {

    private static final float ROTATION_FACTOR = 256.0F / 360.0F;
    private static final double VELOCITY_FACTOR = 8000.0;

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

        // Location
        msg.writeDouble(this.location.getX());
        msg.writeDouble(this.location.getY());
        msg.writeDouble(this.location.getZ());
        msg.writeByte(MathUtil.floor(this.location.getPitch() * ROTATION_FACTOR));
        msg.writeByte(MathUtil.floor(this.location.getYaw() * ROTATION_FACTOR));
        msg.writeByte(MathUtil.floor(this.headYaw * ROTATION_FACTOR));

        // Data
        msg.writeVarInt(this.data);

        // Velocity
        msg.writeShort((int) (this.velocity.getX() * VELOCITY_FACTOR));
        msg.writeShort((int) (this.velocity.getY() * VELOCITY_FACTOR));
        msg.writeShort((int) (this.velocity.getZ() * VELOCITY_FACTOR));
    }
}
