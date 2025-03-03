package ua.nanit.limbo.model.metadata;

import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.Data;
import ua.nanit.limbo.protocol.ByteMessage;

@Data
public class EntityDataType<T> {

    private final String name;
    private final int id;
    private final Function<ByteMessage, T> dataDeserializer;
    private final BiConsumer<ByteMessage, Object> dataSerializer;

}