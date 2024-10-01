package pl.kaitou_dev.clone2048.utils.platform_specific;

/**
 * Describes the interface of a class that will be used for asking for confirmation.
 */
public interface Confirmer {
    /**
     * Prompts the user for confirmation.
     * @param message The message to explain to the user what matter requires their confirmation.
     * @return {@code true} if the user confirmed, {@code false} if the user declined.
     */
    public boolean askConfirm(String message);
}
