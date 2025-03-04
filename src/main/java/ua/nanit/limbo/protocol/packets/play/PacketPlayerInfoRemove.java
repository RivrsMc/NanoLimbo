package ua.nanit.limbo.protocol.packets.play;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

@RequiredArgsConstructor
public class PacketPlayerInfoRemove implements PacketOut {

    private final List<UUID> uniqueIds;

    public PacketPlayerInfoRemove(UUID... uniqueIds) {
        this.uniqueIds = new ArrayList<>();
        Collections.addAll(this.uniqueIds, uniqueIds);
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeList(this.uniqueIds, ByteMessage::writeUuid);
    }

}
