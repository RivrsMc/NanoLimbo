package ua.nanit.limbo.protocol.packets.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import ua.nanit.limbo.model.InventoryType;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketOpenContainer implements PacketOut {

    private int id;
    private InventoryType type;
    private Component title;

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(id);
        msg.writeVarInt(type.ordinal());
        msg.writeComponent(title);
    }
}
