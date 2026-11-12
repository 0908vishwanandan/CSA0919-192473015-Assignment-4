import java.awt.*;
import java.awt.event.*;

public class FeeWindow extends Frame {

    private final TextField regNo = new TextField(20);

    private final TextField amount = new TextField(20);

    private final Button pay = new Button("Pay");

    private final Button clear = new Button("Clear");

    private final Button receipt = new Button("Receipt");

    private final Label status = new Label("Enter payment details.");

    private final FeeService service = new FeeService();

    private FeeService.PaymentReceipt lastReceipt;

    private boolean paymentInProgress = false;

    public FeeWindow() {

        super("Student Fee Payment");

        setLayout(new GridLayout(5, 2, 10, 10));

        add(new Label("Registration Number:"));
        add(regNo);

        add(new Label("Payment Amount:"));
        add(amount);

        add(pay);
        add(clear);

        add(receipt);
        add(status);

        // Separate button listeners
        pay.addActionListener(e -> processPayment());

        clear.addActionListener(e -> clearFields());

        receipt.addActionListener(e -> showReceipt());

        // Window closing event
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                if (paymentInProgress) {

                    status.setText(
                        "Payment in progress. Please wait."
                    );

                } else {

                    dispose();
                }
            }
        });

        setSize(450, 250);

        setLocationRelativeTo(null);
    }

    // Starts payment processing
    private void processPayment() {

        if (paymentInProgress) {
            return;
        }

        String reg = regNo.getText();

        String amt = amount.getText();

        paymentInProgress = true;

        pay.setEnabled(false);

        clear.setEnabled(false);

        status.setText("Processing payment...");

        // Run database work outside the event thread
        Thread worker = new Thread(() -> {

            try {

                FeeService.PaymentReceipt result =
                    service.pay(reg, amt);

                EventQueue.invokeLater(() -> {

                    lastReceipt = result;

                    status.setText("Payment successful.");

                });

            } catch (FeeService.FeeServiceException e) {

                EventQueue.invokeLater(() -> {

                    status.setText(e.getMessage());

                });

            } finally {

                EventQueue.invokeLater(() -> {

                    paymentInProgress = false;

                    pay.setEnabled(true);

                    clear.setEnabled(true);

                });
            }

        }, "Fee-Payment-Worker");

        worker.start();
    }

    // Clears input fields
    private void clearFields() {

        if (paymentInProgress) {
            return;
        }

        regNo.setText("");

        amount.setText("");

        status.setText("Fields cleared.");
    }

    // Displays the last successful receipt
    private void showReceipt() {

        if (lastReceipt == null) {

            status.setText("No payment receipt available.");

        } else {

            status.setText(lastReceipt.toString());
        }
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            FeeWindow window = new FeeWindow();

            window.setVisible(true);

        });
    }
}
