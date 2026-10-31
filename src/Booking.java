import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking {

    public enum Status {
        CONFIRMED,
        CANCELLED
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int bookingId;
    private final String customerId;
    private final int seatId;
    private final LocalDateTime timestamp;
    private Status status;

    public Booking(int bookingId, String customerId, int seatId) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.seatId = seatId;
        this.timestamp = LocalDateTime.now();
        this.status = Status.CONFIRMED;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getSeatId() {
        return seatId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void cancel() {
        this.status = Status.CANCELLED;
    }

    @Override
    public String toString() {
        return String.format("Booking ID: %d | Customer: %s | Seat: %d | Status: %s | Time: %s",
                bookingId,
                customerId,
                seatId,
                status,
                timestamp.format(FORMATTER));
    }
}
