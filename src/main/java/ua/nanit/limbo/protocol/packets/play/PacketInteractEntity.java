package ua.nanit.limbo.protocol.packets.play;

import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ua.nanit.limbo.model.InteractionHand;
import ua.nanit.limbo.model.Vector;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketIn;
import ua.nanit.limbo.protocol.registry.Version;

@Getter
@NoArgsConstructor
public class PacketInteractEntity implements PacketIn {

    private int id;
    private InteractAction action;
    private Optional<Vector> target;
    private InteractionHand hand;
    private boolean sneaking;

    @Override
    public void decode(ByteMessage msg, Version version) {
        this.id = msg.readVarInt();

        int typeIndex = msg.readVarInt();
        this.action = InteractAction.VALUES[typeIndex];
        if (action == InteractAction.INTERACT_AT) {
            float x = msg.readFloat();
            float y = msg.readFloat();
            float z = msg.readFloat();
            this.target = Optional.of(new Vector(x, y, z));
        } else
            this.target = Optional.empty();

        if (action == InteractAction.INTERACT || action == InteractAction.INTERACT_AT) {
            int handID = msg.readVarInt();
            this.hand = InteractionHand.getById(handID);
        } else
            this.hand = InteractionHand.MAIN_HAND;

        this.sneaking = msg.readBoolean();
    }

    public enum InteractAction {
        INTERACT, ATTACK, INTERACT_AT;
        public static final InteractAction[] VALUES = values();
    }
}
