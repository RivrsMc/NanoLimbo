package ua.nanit.limbo.model.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityData {

    private int index;
    private EntityDataType<?> type;
    private Object value;

}
