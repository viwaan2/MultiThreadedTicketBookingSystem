import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TicketSystem ticketSystem = new TicketSystem();
        boolean running = true;

        System.out.println("==================================================");
        System.out.println("   Welcome to Multithreaded Ticket Booking System  ");
        System.out.println("==================================================");

        while (running) {
            printMenu();
            System.out.print("Enter your choice (1-7): ");
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number between 1 and 7.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    ticketSystem.displaySeats();
                    break;
                case 2:
                    System.out.print("Enter Customer Name or ID: ");
                    String customerId = scanner.nextLine().trim();
                    if (customerId.isEmpty()) {
                        System.out.println("Customer name/ID cannot be empty.\n");
                        break;
                    }
                    System.out.print("Enter Seat Number to book (1-" + ticketSystem.getTotalSeats() + "): ");
                    String seatInput = scanner.nextLine().trim();
                    try {
                        int seatId = Integer.parseInt(seatInput);
                        ticketSystem.bookSeat(customerId, seatId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid seat number. Please enter a valid integer.\n");
                    }
                    break;
                case 3:
                    System.out.print("Enter number of concurrent customers (10-20, default 15): ");
                    String numInput = scanner.nextLine().trim();
                    int numCustomers = 15;
                    if (!numInput.isEmpty()) {
                        try {
                            numCustomers = Integer.parseInt(numInput);
                            if (numCustomers < 10 || numCustomers > 20) {
                                System.out.println("Input out of range (10-20). Defaulting to 15 customers.");
                                numCustomers = 15;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format. Defaulting to 15 customers.");
                            numCustomers = 15;
                        }
                    }
                    runSimulation(ticketSystem, numCustomers);
                    break;
                case 4:
                    ticketSystem.displayBookingHistory();
                    break;
                case 5:
                    System.out.print("Enter Booking ID to cancel: ");
                    String cancelInput = scanner.nextLine().trim();
                    try {
                        int bookingId = Integer.parseInt(cancelInput);
                        ticketSystem.cancelBooking(bookingId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Booking ID. Please enter an integer.\n");
                    }
                    break;
                case 6:
                    ticketSystem.displaySummary();
                    break;
                case 7:
                    System.out.println("\nThank you for using the Ticket Booking Simulator. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose a number from 1 to 7.\n");
                    break;
            }
        }

        scanner.close();
    }

    private static void runSimulation(TicketSystem ticketSystem, int numCustomers) {
        System.out.println("\n--- Starting Concurrent Booking Simulation (" + numCustomers + " customers) ---");
        System.out.println("All threads share the same TicketSystem instance.");

        java.util.List<Thread> threads = new java.util.ArrayList<>();
        java.util.Random random = new java.util.Random();

        // High-contention seat range (e.g. seats 1 to 10) to simulate competition
        int contestedSeatRange = Math.min(10, ticketSystem.getTotalSeats());

        for (int i = 1; i <= numCustomers; i++) {
            String customerId = "Customer-" + String.format("%02d", i);
            int desiredSeat = random.nextInt(contestedSeatRange) + 1; // randomly picks between 1 and 10
            Customer customer = new Customer(customerId, ticketSystem, desiredSeat);
            Thread customerThread = new Thread(customer, "Thread-" + customerId);
            threads.add(customerThread);
        }

        // Launch all threads simultaneously
        for (Thread t : threads) {
            t.start();
        }

        // Wait for all threads to finish their execution
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted: " + t.getName());
            }
        }

        System.out.println("--- Concurrent Simulation Finished ---\n");
    }

    private static void printMenu() {
        System.out.println("------------------ MENU ------------------");
        System.out.println("1. View Seat Availability");
        System.out.println("2. Book a Seat (Manual)");
        System.out.println("3. Run Concurrent Booking Simulation");
        System.out.println("4. View Booking History");
        System.out.println("5. Cancel a Booking");
        System.out.println("6. Display System Summary");
        System.out.println("7. Exit");
        System.out.println("------------------------------------------");
    }
}
