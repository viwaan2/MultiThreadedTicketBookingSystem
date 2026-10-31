public class Seat {
    private final int seatId;
    private boolean isBooked;

    public Seat(int seatId) {
        this.seatId = seatId;
        this.isBooked = false;
    }

    public int getSeatId() {
        return seatId;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    @Override
    public String toString() {
        return String.format("Seat %02d [%s]", seatId, (isBooked ? "BOOKED" : "AVAILABLE"));
    }
}
