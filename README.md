# Multithreaded Ticket Booking Simulator

A high-performance, thread-safe Java Command-Line Interface (CLI) application simulating concurrent ticket booking and cancellation across a shared venue inventory.

---

## 1. Project Overview

The **Multithreaded Ticket Booking Simulator** demonstrates fundamental Object-Oriented Programming (OOP), Java Concurrency, and thread synchronization principles. It models an interactive ticket counter where customers can book and cancel seats manually or launch a concurrent simulation where 10–20 threads simultaneously compete for seats.

### Key Highlights
- **Inventory Management**: 50 individual seats visualized in an interactive 10x5 console grid.
- **Atomic Operations**: Thread-safe booking and cancellation with `synchronized` methods to prevent double-booking.
- **Concurrent Simulation**: Multi-threaded execution using Java's `Runnable`, `Thread`, and `join()` coordination.
- **Audit History**: Complete transaction log with unique booking IDs, customer IDs, seat IDs, timestamps, and booking statuses.
- **System Metrics & Reporting**: Live inventory statistics tracking available seats, active bookings, cancellations, and successful vs. rejected transaction attempts.

---

## 2. Project Structure

All Java source files reside directly inside `src/` without package declarations:

```text
TicketBookingSimulator/
├── src/
│   ├── Main.java          # CLI entry point, user menu, and simulation runner
│   ├── Seat.java          # Seat entity modeling seat ID and availability state
│   ├── Customer.java      # Runnable task representing concurrent ticket buyers
│   ├── Booking.java       # Booking model with timestamping and status enum
│   └── TicketSystem.java  # Shared inventory, synchronization, and reporting engine
├── README.md              # Project documentation
├── .gitignore             # Git ignore file for compiled classes and IDE metadata
└── out/                   # Output directory for compiled .class bytecode
```

### Class Responsibilities

| File | Class | Responsibility |
|---|---|---|
| `Main.java` | `Main` | Handles user interactions, input validation, CLI routing, and thread pool simulation execution. |
| `Seat.java` | `Seat` | Encapsulates seat number, availability status, and formatted string representation. |
| `Customer.java` | `Customer` | Implements `Runnable` to simulate customer booking behavior with realistic network latency. |
| `Booking.java` | `Booking` | Immutable booking record with auto-generated ID, timestamp, and `Booking.Status` (`CONFIRMED`, `CANCELLED`). |
| `TicketSystem.java` | `TicketSystem` | Central inventory controller managing seats (`HashMap`), history (`ArrayList`), synchronization (`synchronized`), and reporting. |

---

## 3. Core Technical Concepts

### Object-Oriented Programming (OOP)
1. **Encapsulation**: All fields (`seats`, `bookings`, `isBooked`, `status`) are strictly `private`. State transitions (e.g., reserving or releasing a seat) are exposed exclusively through validated public methods.
2. **Immutability**: Crucial identification fields (`seatId`, `bookingId`, `customerId`, `timestamp`) are marked `final` to ensure transactional integrity.
3. **Type-Safe Enums**: `Booking.Status` defines `CONFIRMED` and `CANCELLED` states, preventing typo bugs and invalid status assignments.
4. **Data Structures**:
   - `HashMap<Integer, Seat>`: Provides instant $O(1)$ seat lookup by seat number.
   - `ArrayList<Booking>`: Retains chronological order of all transaction records for audit reporting.

### Multithreading & Thread Synchronization
1. **The Concurrency Problem ("Check-Then-Act" Race Condition)**:
   - If two threads check `seat.isBooked()` simultaneously when a seat is free, both evaluate to `false`.
   - Both threads proceed to book the seat, resulting in a catastrophic **double-booking**.
   - Additionally, `nextBookingId++` and `bookings.add()` are non-atomic and subject to race conditions and collection corruption.
2. **The Solution (`synchronized`)**:
   - Methods `bookSeat()` and `cancelBooking()` in `TicketSystem` are marked `synchronized`.
   - A customer thread must acquire the intrinsic monitor lock on the shared `TicketSystem` instance (`this`) before entering the critical section.
   - Checking availability, reserving the seat, incrementing the counter, and appending the record occur as an indivisible **atomic unit**.
3. **Thread Coordination (`Thread.join()`)**:
   - The main simulation thread calls `t.join()` on all customer threads to ensure all concurrent operations finish before displaying results back to the user.

---

## 4. Compilation & Execution

### Prerequisites
- Java Development Kit (JDK) 17 or higher (tested with OpenJDK 21).

### Build & Run Commands

From the project root directory (`MultithreadedTicketBooking`):

```bash
# 1. Clean and compile all Java classes into the out/ directory
javac -d out src/*.java

# 2. Run the application
java -cp out Main
```

---

## 5. User Guide & Menu Options

When launched, the interactive CLI displays the main menu:

```text
==================================================
   Welcome to Multithreaded Ticket Booking System  
==================================================
------------------ MENU ------------------
1. View Seat Availability
2. Book a Seat (Manual)
3. Run Concurrent Booking Simulation
4. View Booking History
5. Cancel a Booking
6. Display System Summary
7. Exit
------------------------------------------
Enter your choice (1-7):
```

### Features Walkthrough

1. **View Seat Availability (`1`)**:
   - Prints the 50 seats in a 10x5 grid: `[O]` indicates available, `[X]` indicates booked.
2. **Book a Seat (`2`)**:
   - Prompts for customer name and seat number (1–50).
   - Validates existence and verifies that the seat is not already booked.
3. **Run Concurrent Booking Simulation (`3`)**:
   - Prompts for 10–20 concurrent customer threads (default: 15).
   - Simulates realistic contention where threads compete simultaneously for seats.
4. **View Booking History (`4`)**:
   - Displays the complete audit log with booking ID, customer ID, seat ID, timestamp, and status.
5. **Cancel a Booking (`5`)**:
   - Cancels a confirmed booking by ID, updates its status to `CANCELLED`, and automatically releases the seat back to the pool.
6. **Display System Summary (`6`)**:
   - Renders a metrics summary: total seats, available seats, active bookings, cancellations, successful booking attempts, and failed attempts.
7. **Exit (`7`)**:
   - Safely closes scanners and exits the application.

---

## 6. Verification & Test Scenarios

### Thread Safety Contention Test
To verify zero double-bookings under extreme race conditions, 10 threads were spawned simultaneously targeting **Seat #1**:
- **Result**: Exactly 1 thread obtained `[SUCCESS]`, and exactly 9 threads received `[REJECTED]`.
- **Integrity**: Available seat count decreased by exactly 1, confirming 100% thread safety.

### Full End-to-End Test Run
- Booked seats manually and concurrently.
- Successfully cancelled bookings and verified seats returned to `AVAILABLE` (`[O]`) status.
- Confirmed that cancelled bookings cannot be re-cancelled and invalid seat/booking IDs are handled gracefully.
