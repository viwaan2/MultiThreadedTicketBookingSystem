import java.util.Random;

public class Customer implements Runnable {

    private final String customerId;
    private final TicketSystem ticketSystem;
    private final int desiredSeatId;

    public Customer(String customerId, TicketSystem ticketSystem, int desiredSeatId) {
        this.customerId = customerId;
        this.ticketSystem = ticketSystem;
        this.desiredSeatId = desiredSeatId;
    }

    public Customer(String customerId, TicketSystem ticketSystem) {
        // Randomly pick a seat between 1 and totalSeats
        this(customerId, ticketSystem, new Random().nextInt(ticketSystem.getTotalSeats()) + 1);
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getDesiredSeatId() {
        return desiredSeatId;
    }

    @Override
    public void run() {
        try {
            // Small random jitter (0-50ms) to simulate realistic network/user timing
            Thread.sleep(new Random().nextInt(50));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Attempt to book the desired seat on the shared ticket system
        ticketSystem.bookSeat(customerId, desiredSeatId);
    }
}
