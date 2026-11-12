import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

public class ConverterFrameListener extends JFrame
        implements ActionListener {

    private final JTextField amount = new JTextField(10);

    private final JComboBox<String> target =
        new JComboBox<>(new String[]{"USD", "EUR"});

    private final JLabel result = new JLabel(" ");

    private final JButton convertButton =
        new JButton("Convert");

    public ConverterFrameListener() {

        super("Currency Converter");

        setLayout(new FlowLayout());

        add(new JLabel("INR"));

        add(amount);

        add(target);

        add(convertButton);

        add(result);

        // Register the frame as listener
        convertButton.addActionListener(this);

        amount.addActionListener(this);

        // Handle window closing
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                dispose();
            }
        });

        pack();

        setLocationRelativeTo(null);
    }

    // Handle button click and Enter key
    @Override
    public void actionPerformed(ActionEvent e) {

        convert();
    }

    // Perform conversion
    private void convert() {

        try {

            double inr =
                Double.parseDouble(amount.getText().trim());

            if (!Double.isFinite(inr) || inr < 0) {
                throw new NumberFormatException();
            }

            String currency =
                (String) target.getSelectedItem();

            double converted =
                inr * rate(currency);

            String message = String.format(
                java.util.Locale.US,
                "%.2f INR = %.2f %s",
                inr, converted, currency
            );

            result.setText(message);

            JOptionPane.showMessageDialog(
                this,
                message,
                "Conversion Result",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException e) {

            result.setText("Invalid amount");

            JOptionPane.showMessageDialog(
                this,
                "Invalid amount",
                "Input Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Sample conversion rates
    private static double rate(String currency) {

        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new ConverterFrameListener().setVisible(true);

        });
    }
}
