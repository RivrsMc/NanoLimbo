package ua.nanit.limbo.model;

public enum InteractionHand {
    MAIN_HAND,
    OFF_HAND;

    private static final InteractionHand[] VALUES = values();

    public int getId() {
        return ordinal();
    }

    public static InteractionHand getById(int id) {
        return VALUES[id];
    }
}