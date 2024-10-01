package pl.kaitou_dev.clone2048.utils.platform_specific;

/**
 * Describes the interface of a class that will be used for displaying errors.
 */
public interface ErrorDisplayer {
    /**
     * Displays an error provided via title and message.
     * @param title The title of the error.
     * @param message The message (details) of the error.
     */
    public void displayError(String title, String message);

    /**
     * Displays an error provided via an exception.
     * @param ex The exception representing the error.
     * @see #displayError(String, String)
     */
    public default void displayError(Exception ex) {
        displayError(ex.getClass().getSimpleName(), ex.getMessage());
    }
}
