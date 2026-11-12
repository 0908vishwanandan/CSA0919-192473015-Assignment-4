import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

public class ConverterNamedInner extends JFrame {

    private final JTextField amount = new JTextField(10);

    private final JComboBox<String> target =
        new JComboBox<>(new String[]{"USD", "EUR"});

    private final JLabel result = new JLabel(" ");

    private final JButton convertButton =
        new JButton("Convert");

    public ConverterNamedInner() {

        super("Currency Converter");

        setLayout(new FlowLayout());

        add(new JLabel("INR"));

        add(amount);

        add(target);

        add(convertButton);

        add(result);

        // Create named inner class object
        ConvertHandler handler = new ConvertHandler();

        // Register the same handler for both events
        convertButton.addActionListener(handler);

        amount.addActionListener(handler);

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

    // Named inner class
    private class ConvertHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            convert();
        }
    }

    private void convert() {

        try {

            double inr =
                Double.parseDouble(amount.getText().trim());

            if (!Double.isFinite(inr) || inr < 0) {
                throw new NumberFormatException();
            }

            String currency =
                (String) target.getSelectedItem();

            double converted = inr * rate(currency);

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

    private static double rate(String currency) {

        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new ConverterNamedInner().setVisible(true);

        });
    }
}
