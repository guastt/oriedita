package origami_editor.editor;

import javax.inject.Inject;
import javax.inject.Named;
import origami_editor.editor.databinding.ApplicationModel;

import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;

@Singleton
public class HelpDialog extends JDialog {
    private final ResourceBundle helpBundle;
    private final Point point = new Point();
    private JPanel contentPane;
    private JTextPane helpLabel;

    @Inject
    public HelpDialog(@Named("mainFrame") JFrame frame, ApplicationModel applicationModel) {
        super(frame, "Help");
        $$$setupUI$$$();
        setContentPane(contentPane);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                applicationModel.setHelpVisible(false);
            }
        });

        applicationModel.addPropertyChangeListener(e -> {
            if (e.getPropertyName() == null || e.getPropertyName().equals("helpVisible")) {
                setVisible(applicationModel.getHelpVisible());
            }
            frame.requestFocus();
        });

        setUndecorated(true);

        JPopupMenu popup = new JPopupMenu();
        JMenuItem dismissMenuItem = new JMenuItem("Dismiss");
        dismissMenuItem.addActionListener(e -> applicationModel.setHelpVisible(false));

        popup.add(dismissMenuItem);

        // Code to move the dialog by dragging the label.
        helpLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                point.x = e.getX();
                point.y = e.getY();

                maybeShowPopup(e);

                frame.requestFocus();
            }

            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(),
                            e.getX(), e.getY());
                }
            }
        });
        helpLabel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - point.x,
                        p.y + e.getY() - point.y);
            }
        });

        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> applicationModel.setHelpVisible(false), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        helpBundle = ResourceBundle.getBundle("help");

        helpLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public void setExplanation(String key) {
        helpLabel.setText(helpBundle.getString(key));
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setAutoscrolls(false);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(31);
        scrollPane1.setMinimumSize(new Dimension(300, 400));
        scrollPane1.setPreferredSize(new Dimension(300, 400));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(scrollPane1, gbc);
        helpLabel = new JTextPane();
        helpLabel.setContentType("text/html");
        helpLabel.setFocusable(false);
        helpLabel.setOpaque(true);
        scrollPane1.setViewportView(helpLabel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    public void start(Point canvasLocation, Dimension canvasSize) {
        pack();

        setLocation(canvasLocation.x + canvasSize.width - getSize().width - 10, canvasLocation.y + 10);

        helpLabel.setText(helpBundle.getString("a__hajimeni"));
    }
}
