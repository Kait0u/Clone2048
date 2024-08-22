package pl.kaitou_dev.clone2048;

public enum Directions {
    UP, DOWN, LEFT, RIGHT;

    public Directions opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
