import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class BufferedDrawPad extends Frame {

    private final Label status = new Label("Clicks: 0");

    private final DrawingCanvas canvas = new DrawingCanvas();

    public BufferedDrawPad() {

        super("Buffered Draw Pad");

        setLayout(new BorderLayout());

        add(canvas, BorderLayout.CENTER);

        add(status, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                dispose();
            }
        });

        setSize(400, 300);

        setLocationRelativeTo(null);
    }

    class DrawingCanvas extends Canvas {

        private BufferedImage image;

        private int count = 0;

        DrawingCanvas() {

            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    ensureImage();

                    if (image == null) {
                        return;
                    }

                    // Draw dot directly onto the image
                    Graphics2D g2 = image.createGraphics();

                    try {

                        g2.setColor(Color.BLACK);

                        g2.fillOval(
                            e.getX() - 3,
                            e.getY() - 3,
                            6,
                            6
                        );

                    } finally {

                        g2.dispose();
                    }

                    count++;

                    status.setText("Clicks: " + count);

                    repaint();
                }
            });
        }

        // Create or resize the BufferedImage
        private void ensureImage() {

            int width = getWidth();

            int height = getHeight();

            if (width <= 0 || height <= 0) {
                return;
            }

            if (image == null
                    || image.getWidth() != width
                    || image.getHeight() != height) {

                BufferedImage oldImage = image;

                image = new BufferedImage(
                    width,
                    height,
                    BufferedImage.TYPE_INT_ARGB
                );

                Graphics2D g2 = image.createGraphics();

                try {

                    g2.setColor(Color.WHITE);

                    g2.fillRect(0, 0, width, height);

                    // Preserve the old drawing after resizing
                    if (oldImage != null) {

                        g2.drawImage(oldImage, 0, 0, null);
                    }

                } finally {

                    g2.dispose();
                }
            }
        }

        @Override
        public void paint(Graphics g) {

            ensureImage();

            if (image != null) {

                g.drawImage(image, 0, 0, this);
            }
        }
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            BufferedDrawPad pad = new BufferedDrawPad();

            pad.setVisible(true);
        });
    }
}
