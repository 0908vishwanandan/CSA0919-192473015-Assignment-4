import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class DrawPad extends Frame {

    private final List<Point> points = new ArrayList<>();

    private final Label status = new Label("Clicks: 0");

    private int count = 0;

    public DrawPad() {

        super("Draw Pad");

        setLayout(new BorderLayout());

        add(status, BorderLayout.SOUTH);

        // TODO 1: Handle mouse clicks
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                // Store the clicked position
                points.add(e.getPoint());

                // Increase click count
                count++;

                // Update the label
                status.setText("Clicks: " + count);

                // Request repainting
                repaint();
            }
        });

        // Handle window closing
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                dispose();
            }
        });

        setSize(400, 300);

        setLocationRelativeTo(null);
    }

    // TODO 2: Redraw all stored points
    @Override
    public void paint(Graphics g) {

        super.paint(g);

        for (Point p : points) {

            // Draw a 6 x 6 dot centred on the clicked position
            g.fillOval(
                p.x - 3,
                p.y - 3,
                6,
                6
            );
        }
    }

    // Main method
    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            DrawPad pad = new DrawPad();

            pad.setVisible(true);
        });
    }
}
