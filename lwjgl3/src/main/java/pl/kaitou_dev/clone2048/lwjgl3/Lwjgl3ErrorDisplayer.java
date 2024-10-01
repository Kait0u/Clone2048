package pl.kaitou_dev.clone2048.lwjgl3;

import pl.kaitou_dev.clone2048.utils.platform_specific.ErrorDisplayer;

import javax.swing.JOptionPane;

/**
 * A desktop implementation of the {@link ErrorDisplayer}. It uses Swing.
 */
public class Lwjgl3ErrorDisplayer implements ErrorDisplayer {

    @Override
    public void displayError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
