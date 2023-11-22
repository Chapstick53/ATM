import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class ATM {

    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("************************************************");
        System.out.println("*            *        *****      *     *       *");
        System.out.println("*           * *         *        * * * *       *");
        System.out.println("*          *****        *        *  *  *       *");
        System.out.println("*         *     *       *        *     *       *");
        System.out.println("*                                              *");
        System.out.println("*         Press ENTER key to Enter in ATM        *");
        System.out.println("************************************************");

        // Wait for the user to press any key
        scanner.nextLine();

        System.out.println(" Welcome to the ATM!");
        System.out.println();
        System.out.println("Choose:");

        System.out.println("1. Login to ATM Account");
        System.out.println("2. Create an ATM Account");

        int choice = scanner.nextInt();

        long accountNumber = 0; // Initialize accountNumber outside the switch statement

        switch (choice) {
            case 1:
                // Implement the login logic here
                System.out.println("You chose to log in.");
                scanner.nextLine(); // Consume the newline character

                // Read the user's PIN
                System.out.print("Enter your PIN: ");
                int enteredPin = scanner.nextInt();

                // Check if the entered account number and PIN match the stored account details
                long loggedInAccountNumber = login(enteredPin);
                if (loggedInAccountNumber != -1) {
                    System.out.println("Login Successfully!");
                    System.out.println();
                    accountNumber = loggedInAccountNumber;
                    showMainMenu(accountNumber);
                } else {
                    System.out.println("Either incorrect PIN or Account does not exist.");
                }
                break;

            case 2:
                // Implement the account creation logic here
                System.out.println("You chose to create an ATM account.");

                scanner.nextLine(); // Consume the previous newline character
                System.out.print("Enter your name: ");
                String name = scanner.nextLine();

                System.out.print("Enter your phone number: ");
                long phoneNumber = scanner.nextLong();

                int pin;
                while (true) {
                    System.out.print("Enter 6-digits PIN: ");
                    pin = scanner.nextInt();
                    if (isValidPin(pin)) {
                        break;
                    } else {
                        System.out.println("Invalid PIN.");
                    }
                }

                accountNumber = generateAccountNumber();

                writeAccountToFile(name, phoneNumber, pin, accountNumber);

                System.out.println("Congratulations...");
                System.out.println("Dear " + name + ", your account is successfully created.");
                System.out.println("Your account no. is: " + accountNumber);
                System.out.println();
                System.out.println("Logging out successfully for Confirmation...");
                break;

            default:
                System.out.println("Invalid choice. Please choose 1 or 2.");
        }

        // If you need to use accountNumber outside the switch, it's now accessible here
        // For example, you can use it to display the main menu after creating an account
        if (choice == 2) {
            showMainMenu(accountNumber);
        }

        scanner.close();
    }

    private static boolean isValidPin(int pin) {
        String pinString = String.valueOf(pin);
        return pin >= 100000 && pin <= 999999;
    }

    private static long generateAccountNumber() {
        // Generate a random 10-digit account number
        Random random = new Random();
        return (long) (1000000000L + random.nextInt(900000000));
    }

    private static void writeAccountToFile(String name, long phoneNumber, int pin, long accountNumber) {
        // Write account details to the file
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACCOUNTS_FILE, true))) {
            writer.println(accountNumber + "," + name + "," + phoneNumber + "," + pin + ",0.0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long login(int enteredPin) {
        // Check if the entered PIN matches any stored PIN
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int storedPin = Integer.parseInt(parts[3]);
                if (storedPin == enteredPin) {
                    // PIN matches, return the account number
                    return Long.parseLong(parts[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return -1 for unsuccessful login
        return -1;
    }

    private static void showMainMenu(long accountNumber) {

        boolean loggedIn = true;

        while (loggedIn) {
            // Display the main menu options after successful login
            System.out.println("Choose:");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. Logout");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // Deposit
                    System.out.print("Enter the amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    deposit(accountNumber, depositAmount);
                    break;
                case 2:
                    // Withdraw
                    System.out.print("Enter the amount to withdraw: ");
                    double withdrawAmount = scanner.nextDouble();
                    withdraw(accountNumber, withdrawAmount);
                    break;
                case 3:
                    // Check Balance
                    double balance = checkBalance(accountNumber);
                    System.out.println("Your current balance: Rs. " + balance);
                    break;
                case 4:
                    // Logout
                    System.out.println("Logging out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice. Please choose 1, 2, 3, or 4.");
            }
        }
    }

    private static void deposit(long accountNumber, double amount) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(ACCOUNTS_FILE));
            String line;
            StringBuilder inputBuffer = new StringBuilder();

            while ((line = file.readLine()) != null) {
                String[] parts = line.split(",");
                long currentAccountNumber = Long.parseLong(parts[0]);
                if (currentAccountNumber == accountNumber) {
                    // Update the balance
                    if (parts.length >= 5) {
                        double currentBalance = Double.parseDouble(parts[4]);
                        currentBalance += amount;
                        line = parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + currentBalance;
                    } else {
                        System.out.println("Invalid data format in the file.");
                        return;
                    }
                }
                inputBuffer.append(line).append('\n');
            }
            file.close();

            // Write the new content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE));
            writer.write(inputBuffer.toString());
            writer.close();

            System.out.println("Deposit successful.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void withdraw(long accountNumber, double amount) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(ACCOUNTS_FILE));
            String line;
            StringBuilder inputBuffer = new StringBuilder();

            while ((line = file.readLine()) != null) {
                String[] parts = line.split(",");
                long currentAccountNumber = Long.parseLong(parts[0]);
                if (currentAccountNumber == accountNumber) {
                    // Update the balance
                    if (parts.length >= 5) {
                        double currentBalance = Double.parseDouble(parts[4]);
                        if (amount <= currentBalance) {
                            currentBalance -= amount;
                            line = parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + currentBalance;
                        } else {
                            System.out.println("Insufficient funds. Withdrawal failed.");
                            return;
                        }
                    } else {
                        System.out.println("Invalid data format in the file.");
                        return;
                    }
                }
                inputBuffer.append(line).append('\n');
            }
            file.close();

            // Write the new content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE));
            writer.write(inputBuffer.toString());
            writer.close();

            System.out.println("Withdrawal successful.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double checkBalance(long accountNumber) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                long currentAccountNumber = Long.parseLong(parts[0]);
                if (currentAccountNumber == accountNumber) {
                    // Check if the array has enough elements before accessing index 4
                    if (parts.length >= 5) {
                        // Return the current balance
                        return Double.parseDouble(parts[4]);
                    } else {
                        System.out.println("Invalid data format in the file.");
                        return 0.0;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0; // Default value if account not found
    }
}
