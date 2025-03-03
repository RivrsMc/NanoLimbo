package ua.nanit.limbo.protocol;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.nbt.BinaryTag;

@Setter
@Getter
public class NbtMessage {

    private String json;
    private BinaryTag tag;

    public NbtMessage(String json, BinaryTag tag) {
        this.json = json;
        this.tag = tag;
    }

}
