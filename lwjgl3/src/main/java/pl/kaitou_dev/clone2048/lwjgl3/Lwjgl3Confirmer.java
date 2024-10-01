package pl.kaitou_dev.clone2048.lwjgl3;

import pl.kaitou_dev.clone2048.utils.platform_specific.Confirmer;

import javax.swing.*;

/**
 * A desktop implementation of the {@link Confirmer}. It uses Swing.
 */
public class Lwjgl3Confirmer implements Confirmer {
    @Override
    public boolean askConfirm(String message) {
        int dialogResult = JOptionPane.showConfirmDialog(
            null,
            message,
            "Are you sure?",
            JOptionPane.YES_NO_OPTION
        );

        return dialogResult == JOptionPane.YES_OPTION;
    }
}
