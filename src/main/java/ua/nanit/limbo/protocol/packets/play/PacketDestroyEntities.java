package ua.nanit.limbo.protocol.packets.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketDestroyEntities implements PacketOut {

    private int[] ids;

    public PacketDestroyEntities(int id) {
        this.ids = new int[]{id};
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(this.ids.length);
        for (int id : this.ids) {
            msg.writeVarInt(id);
        }
    }
}
