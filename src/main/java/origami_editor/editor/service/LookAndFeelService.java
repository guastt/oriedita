package origami_editor.editor.service;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import origami_editor.editor.App;
import origami_editor.editor.databinding.ApplicationModel;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.net.URL;

@Singleton
public class LookAndFeelService {
    private final JFrame frame;

    @Inject
    public LookAndFeelService(@Named("mainFrame") JFrame frame, ApplicationModel applicationModel) {
        this.frame = frame;

        applicationModel.addPropertyChangeListener(e -> {
            if (e.getPropertyName() == null || e.getPropertyName().equals("laf")) {
                applyLookAndFeel(applicationModel.getLaf());
            }
        });
    }

    private void applyLookAndFeel(String lafClassName) {
        EventQueue.invokeLater(() -> {
            try {
                // clear custom default font before switching to other LaF
                Font defaultFont = null;
                if (UIManager.getLookAndFeel() instanceof FlatLaf) {
                    Font font = UIManager.getFont("defaultFont");
                    if (font != UIManager.getLookAndFeelDefaults().getFont("defaultFont"))
                        defaultFont = font;
                }
                UIManager.put("defaultFont", null);

                // change look and feel
                UIManager.setLookAndFeel(lafClassName);

                // restore custom default font when switched to other FlatLaf LaF
                if (defaultFont != null && UIManager.getLookAndFeel() instanceof FlatLaf)
                    UIManager.put("defaultFont", defaultFont);

                // update all components
                updateUI2();

                updateButtonIcons(frame);

                if (frame.getExtendedState() == Frame.NORMAL) {
                    // increase size of frame if necessary
                    int width = frame.getWidth();
                    int height = frame.getHeight();
                    Dimension prefSize = frame.getPreferredSize();
                    if (prefSize.width > width || prefSize.height > height)
                        frame.setSize(Math.max(prefSize.width, width), Math.max(prefSize.height, height));

                    // limit frame size to screen size
                    Rectangle screenBounds = frame.getGraphicsConfiguration().getBounds();
                    screenBounds = FlatUIUtils.subtractInsets(screenBounds, frame.getToolkit().getScreenInsets(frame.getGraphicsConfiguration()));
                    Dimension frameSize = frame.getSize();
                    if (frameSize.width > screenBounds.width || frameSize.height > screenBounds.height)
                        frame.setSize(Math.min(frameSize.width, screenBounds.width), Math.min(frameSize.height, screenBounds.height));

                    // move frame to left/top if necessary
                    if (frame.getX() + frame.getWidth() > screenBounds.x + screenBounds.width ||
                            frame.getY() + frame.getHeight() > screenBounds.y + screenBounds.height) {
                        frame.setLocation(Math.min(frame.getX(), screenBounds.x + screenBounds.width - frame.getWidth()),
                                Math.min(frame.getY(), screenBounds.y + screenBounds.height - frame.getHeight()));
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }


    private static void updateUI2() {
        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        java.awt.Component permanentFocusOwner = keyboardFocusManager.getPermanentFocusOwner();
        JSpinner spinner = (permanentFocusOwner != null)
                ? (JSpinner) SwingUtilities.getAncestorOfClass(JSpinner.class, permanentFocusOwner)
                : null;

        FlatLaf.updateUI();

        if (spinner != null && keyboardFocusManager.getPermanentFocusOwner() == null) {
            JComponent editor = spinner.getEditor();
            JTextField textField = (editor instanceof JSpinner.DefaultEditor)
                    ? ((JSpinner.DefaultEditor) editor).getTextField()
                    : null;
            if (textField != null)
                textField.requestFocusInWindow();
        }
    }

    public void updateButtonIcons() {
        updateButtonIcons(frame);
    }

    private void updateButtonIcons(Container container) {
        boolean isDark = FlatLaf.isLafDark();
        for (java.awt.Component c : container.getComponents()) {
            if (c instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) c;
                if (button.getIcon() instanceof ImageIcon) {
                    button.setIcon(determineIcon(isDark, (ImageIcon) button.getIcon()));
                }
            } else if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if (label.getIcon() instanceof ImageIcon) {
                    label.setIcon(determineIcon(isDark, (ImageIcon) label.getIcon()));
                }
            } else if (c instanceof Container) {
                updateButtonIcons((Container) c);
            }
        }
    }

    private ImageIcon determineIcon(boolean isDark, ImageIcon icon) {
        // TODO this works because the description is the filename of the image, this should be based on the name of the action.
        String uri = icon.getDescription();

        if (isDark) {
            uri = uri.replaceAll(".*ppp", "ppp_dark");
        } else {
            uri = uri.replaceAll(".*ppp_dark", "ppp");
        }

        URL resource = App.class.getClassLoader().getResource(uri);

        if (resource != null) {
            return new ImageIcon(resource);
        }

        return icon;
    }
}
