import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketSystem {

    private final int totalSeats;
    private final Map<Integer, Seat> seats;
    private final List<Booking> bookings;
    private int nextBookingId;
    private int successfulAttempts;
    private int failedAttempts;
    private int cancellations;

    public TicketSystem() {
        this(50);
    }

    public TicketSystem(int totalSeats) {
        this.totalSeats = totalSeats;
        this.seats = new HashMap<>();
        this.bookings = new ArrayList<>();
        this.nextBookingId = 1;
        this.successfulAttempts = 0;
        this.failedAttempts = 0;
        this.cancellations = 0;

        // Initialize 50 seats
        for (int i = 1; i <= totalSeats; i++) {
            seats.put(i, new Seat(i));
        }
    }

    /**
     * Display all seats and their current availability in a 10x5 grid format.
     */
    public synchronized void displaySeats() {
        System.out.println("\n================== SEAT AVAILABILITY (Total: " + totalSeats + ") ==================");
        for (int i = 1; i <= totalSeats; i++) {
            Seat seat = seats.get(i);
            String status = seat.isBooked() ? "[X]" : "[O]"; // [O] = Available, [X] = Booked
            System.out.printf("Seat %02d %s   ", seat.getSeatId(), status);

            if (i % 5 == 0) {
                System.out.println();
            }
        }
        System.out.println("Legend: [O] Available   [X] Booked");
        System.out.println("Available Seats: " + getAvailableSeatsCount() + " / " + totalSeats);
        System.out.println("===================================================================\n");
    }

    /**
     * Thread-safe seat booking using synchronization.
     * Guarantees atomic check-then-act to prevent duplicate bookings,
     * race conditions on nextBookingId, and corruptions in the bookings list.
     */
    public synchronized boolean bookSeat(String customerId, int seatId) {
        if (!seats.containsKey(seatId)) {
            System.out.println("[REJECTED] Invalid seat ID: " + seatId + ". Valid range is 1 to " + totalSeats + ".");
            failedAttempts++;
            return false;
        }

        Seat seat = seats.get(seatId);

        if (seat.isBooked()) {
            System.out.println("[REJECTED] " + customerId + " failed to book Seat " + seatId + " (Already booked)");
            failedAttempts++;
            return false;
        }

        // Atomically reserve the seat and record the booking
        seat.setBooked(true);
        Booking booking = new Booking(nextBookingId++, customerId, seatId);
        bookings.add(booking);
        successfulAttempts++;

        System.out.println("[SUCCESS] " + customerId + " successfully booked Seat " + seatId + " (Booking ID: " + booking.getBookingId() + ")");
        return true;
    }

    /**
     * Thread-safe cancellation of an active booking.
     * Releases the seat and updates booking status to CANCELLED.
     */
    public synchronized boolean cancelBooking(int bookingId) {
        Booking targetBooking = null;
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                targetBooking = booking;
                break;
            }
        }

        if (targetBooking == null) {
            System.out.println("[REJECTED] Booking ID " + bookingId + " does not exist.");
            return false;
        }

        if (targetBooking.getStatus() == Booking.Status.CANCELLED) {
            System.out.println("[REJECTED] Booking ID " + bookingId + " has already been cancelled.");
            return false;
        }

        // Perform cancellation and release the seat
        targetBooking.cancel();
        Seat seat = seats.get(targetBooking.getSeatId());
        if (seat != null) {
            seat.setBooked(false);
        }
        cancellations++;

        System.out.println("[SUCCESS] Booking ID " + bookingId + " (Customer: " + targetBooking.getCustomerId()
                + ") cancelled. Seat " + targetBooking.getSeatId() + " is now released and available.");
        return true;
    }

    /**
     * Display the chronological booking audit log.
     */
    public synchronized void displayBookingHistory() {
        System.out.println("\n========================= BOOKING HISTORY =========================");
        if (bookings.isEmpty()) {
            System.out.println("No booking records found.");
        } else {
            for (Booking booking : bookings) {
                System.out.println(booking);
            }
        }
        System.out.println("===================================================================\n");
    }

    /**
     * Display complete system performance and inventory summary.
     */
    public synchronized void displaySummary() {
        int activeBookings = getActiveBookingsCount();
        int availableSeats = getAvailableSeatsCount();

        System.out.println("\n====================== SYSTEM SUMMARY REPORT ======================");
        System.out.printf("Total Seats:                  %d\n", totalSeats);
        System.out.printf("Available Seats:              %d\n", availableSeats);
        System.out.printf("Active Bookings (Confirmed):  %d\n", activeBookings);
        System.out.printf("Total Cancellations:          %d\n", cancellations);
        System.out.printf("Successful Booking Attempts:  %d\n", successfulAttempts);
        System.out.printf("Failed Booking Attempts:      %d\n", failedAttempts);
        System.out.printf("Total Transaction Attempts:   %d\n", (successfulAttempts + failedAttempts));
        System.out.println("===================================================================\n");
    }

    public synchronized int getAvailableSeatsCount() {
        int count = 0;
        for (Seat seat : seats.values()) {
            if (!seat.isBooked()) {
                count++;
            }
        }
        return count;
    }

    public synchronized int getActiveBookingsCount() {
        int count = 0;
        for (Booking booking : bookings) {
            if (booking.getStatus() == Booking.Status.CONFIRMED) {
                count++;
            }
        }
        return count;
    }

    public int getSuccessfulAttempts() {
        return successfulAttempts;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public int getCancellations() {
        return cancellations;
    }

    public int getTotalSeats() {
        return totalSeats;
    }
}
