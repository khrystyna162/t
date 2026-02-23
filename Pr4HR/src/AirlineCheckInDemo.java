import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class AirlineCheckInDemo {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printHeader("СИСТЕМА РЕЄСТРАЦІЇ НА РЕЙС - ДОМЕННІ ПОДІЇ");

        // 1. Ініціалізація публікатора подій
        DomainEventPublisher publisher = DomainEventPublisher.getInstance();
        publisher.subscribe(new CheckInLoggingHandler());
        publisher.subscribe(new EmailNotificationHandler());

        System.out.println("✓ Систему ініціалізовано");
        System.out.println("✓ Обробники подій зареєстровано: " + publisher.getHandlersCount());
        System.out.println();

        // 2. Створення рейсу
        Flight flight = createFlight();

        // 3. Головне меню
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Ваш вибір: ");

            switch (choice) {
                case 1 -> registerPassenger(flight);
                case 2 -> showFlightInfo(flight);
                case 3 -> {
                    System.out.println("\nДякуємо за використання системи!");
                    running = false;
                }
                default -> System.out.println("Невірний вибір! Спробуйте ще раз.\n");
            }
        }

        scanner.close();
        printFooter();
    }

    private static Flight createFlight() {
        printHeader("СТВОРЕННЯ РЕЙСУ");

        System.out.print("Введіть номер рейсу (наприклад, UA-777): ");
        String flightNumber = scanner.nextLine().trim();

        System.out.print("Місто відправлення: ");
        String departure = scanner.nextLine().trim();

        System.out.print("Місто прибуття: ");
        String arrival = scanner.nextLine().trim();

        System.out.print("Вихід (наприклад, A12): ");
        String gate = scanner.nextLine().trim();

        System.out.print("Через скільки годин виліт? ");
        int hours = readInt("");

        LocalDateTime departureTime = LocalDateTime.now().plusHours(hours);

        Flight flight = new Flight(flightNumber, departure, arrival, departureTime, gate);

        System.out.println("\n✅ Рейс створено: " + flight.getFlightNumber());
        System.out.println("   Маршрут: " + flight.getDepartureCity() + " → " + flight.getArrivalCity());
        System.out.println("   Вихід: " + flight.getGate());
        System.out.println("   Час вильоту: " + departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println();

        return flight;
    }

    private static void registerPassenger(Flight flight) {
        printHeader("РЕЄСТРАЦІЯ ПАСАЖИРА");

        System.out.print("ID пасажира: ");
        String id = scanner.nextLine().trim();

        System.out.print("Ім'я: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Прізвище: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Номер паспорта: ");
        String passport = scanner.nextLine().trim();

        System.out.print("Місце (наприклад, 12A): ");
        String seat = scanner.nextLine().trim();

        Passenger passenger = new Passenger(id, firstName, lastName, email, passport);

        System.out.println("\n🔄 Виконується реєстрація...\n");

        try {
            flight.checkInPassenger(passenger, seat);
            System.out.println("\n✅ Пасажира " + passenger.getFullName() + " успішно зареєстровано!");
            System.out.println("   Місце: " + seat);
            System.out.println("   Посадковий талон відправлено на: " + email);
        } catch (Exception e) {
            System.out.println("\n❌ Помилка: " + e.getMessage());
        }

        System.out.println();
    }

    private static void showFlightInfo(Flight flight) {
        printHeader("ІНФОРМАЦІЯ ПРО РЕЙС");

        System.out.println("Номер рейсу: " + flight.getFlightNumber());
        System.out.println("Маршрут: " + flight.getDepartureCity() + " → " + flight.getArrivalCity());
        System.out.println("Вихід: " + flight.getGate());
        System.out.println("Час вильоту: " + flight.getDepartureTime().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("Зареєстровано пасажирів: " + flight.getCheckedInPassengersCount());
        System.out.println();
    }

    private static void printMenu() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║           ГОЛОВНЕ МЕНЮ                 ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 1. Зареєструвати пасажира              ║");
        System.out.println("║ 2. Інформація про рейс                 ║");
        System.out.println("║ 3. Вихід                               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Будь ласка, введіть число!");
            }
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  " + title);
        System.out.println("=".repeat(70) + "\n");
    }

    private static void printFooter() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  РОБОТА ЗАВЕРШЕНА");
        System.out.println("=".repeat(70));
    }
}