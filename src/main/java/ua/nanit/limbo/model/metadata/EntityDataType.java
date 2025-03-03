package ua.nanit.limbo.model.metadata;

import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.Data;
import ua.nanit.limbo.protocol.ByteMessage;

public record EntityDataType<T>(String name, int id, Function<ByteMessage, T> dataDeserializer,
                                BiConsumer<ByteMessage, Object> dataSerializer) {

}