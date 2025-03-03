package ua.nanit.limbo.protocol.packets.play;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nanit.limbo.model.metadata.EntityData;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.Log;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketMetadata implements PacketOut {

    private int id;
    private List<EntityData> entityData;

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(this.id);
        for (EntityData data : entityData) {
            msg.writeByte(data.getIndex());
            msg.writeVarInt(data.getType().getId());


            data.getType().getDataSerializer().accept(msg, data.getValue());
        }
        msg.writeByte(255);
    }
}
