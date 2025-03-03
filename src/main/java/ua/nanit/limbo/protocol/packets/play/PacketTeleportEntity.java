package ua.nanit.limbo.protocol.packets.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nanit.limbo.model.Location;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketTeleportEntity implements PacketOut {


    private int id;
    private Location location;
    private boolean onGround;

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(this.id);
        msg.writeDouble(this.location.getX());
        msg.writeDouble(this.location.getY());
        msg.writeDouble(this.location.getZ());
        msg.writeFloat(this.location.getYaw());
        msg.writeFloat(this.location.getPitch());
        msg.writeBoolean(this.onGround);
    }

}
