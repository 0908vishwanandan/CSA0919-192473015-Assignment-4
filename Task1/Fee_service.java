import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.math.BigDecimal;
import java.sql.SQLException;

public class FeeService {

    private static final String URL =
        "jdbc:mysql://localhost:3306/college_fees";

    private static final String USER = "root";

    private static final String PASSWORD = "your_password";

    // Performs validation and payment
    public PaymentReceipt pay(String regNoText, String amountText)
            throws FeeServiceException {

        String regNo = regNoText.trim();

        // Validate registration number
        if (regNo.isEmpty()) {
            throw new FeeServiceException(
                "Registration number cannot be empty."
            );
        }

        double amount;

        // Validate amount format
        try {
            amount = Double.parseDouble(amountText.trim());

        } catch (NumberFormatException e) {

            throw new FeeServiceException(
                "Please enter a valid payment amount.", e
            );
        }

        // Validate amount value
        if (!Double.isFinite(amount) || amount <= 0) {

            throw new FeeServiceException(
                "Payment amount must be greater than zero."
            );
        }

        // Simulate bank confirmation for demonstration
        try {

            Thread.sleep(3000);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new FeeServiceException(
                "Payment processing was interrupted.", e
            );
        }

        String sql =
            "INSERT INTO fee_payments (reg_no, amount) VALUES (?, ?)";

        // Connect to database and insert payment
        try (
            Connection con =
                DriverManager.getConnection(URL, USER, PASSWORD);

            PreparedStatement ps =
                con.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS
                )
        ) {

            ps.setString(1, regNo);
            ps.setBigDecimal(2, BigDecimal.valueOf(amount));

            ps.executeUpdate();

            long paymentId = -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    paymentId = rs.getLong(1);
                }
            }

            return new PaymentReceipt(paymentId, regNo, amount);

        } catch (SQLException e) {

            throw new FeeServiceException(
                "Unable to complete the payment. Please try again.", e
            );
        }
    }

    // Stores successful payment details
    public static class PaymentReceipt {

        private final long paymentId;
        private final String regNo;
        private final double amount;

        public PaymentReceipt(
                long paymentId, String regNo, double amount) {

            this.paymentId = paymentId;
            this.regNo = regNo;
            this.amount = amount;
        }

        public String toString() {

            return "Payment ID: " + paymentId
                + " | Registration No: " + regNo
                + " | Amount: Rs. "
                + String.format("%.2f", amount);
        }
    }

    // Custom exception
    public static class FeeServiceException extends Exception {

        public FeeServiceException(String message) {
            super(message);
        }

        public FeeServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
