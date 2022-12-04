package main.java.com.finvest;

import java.util.Objects;
import java.util.Scanner;

/**
 * Drives user login into the Finvest Holdings system.
 *
 * @author Paul Roode
 */
public class UserLogin {

    public final static String ANSI_BLUE = "\u001B[34m";
    public final static String ANSI_ITALIC = "\033[3m";
    public final static String ANSI_RESET = "\u001B[0m";

    public static void main(String[] argv) {

        PasswordManager passwordManager = new PasswordManager(); // For validating roles
        AccessControlPolicy accessControlPolicy = new AccessControlPolicy(); // For enforcing ABAC and OBAC

        // Initiate user login
        System.out.println("Finvest Holdings");
        System.out.println("Client Holdings and Information System");
        System.out.println("User Login");
        System.out.println("--------------------------------------------------");
        Scanner scanner = new Scanner(System.in);
        String userWantsToRetryLogin; // Stores the user's choice of whether to retry logging in after a failed attempt
        String username;
        String password;
        do {
            do { // Prompt for username
                System.out.println("Enter username: ");
                username = scanner.nextLine().trim();
            } while (username.isEmpty() || username.equals("/n"));
            do { // Prompt for password
                System.out.println("Enter password: ");
                password = scanner.nextLine().trim();
            } while (password.isEmpty() || password.equals("/n"));

            // Validate the user's login credentials
            User user = passwordManager.getValidatedUser(username, password);
            if (user != null) {
                if (accessControlPolicy.enforceABAC(user)) { // Enforce ABAC on the user
                    System.out.println("ACCESS GRANTED\n");

                    // Provide the user's actionable access permissions
                    System.out.println(ANSI_BLUE + "Username: " + user.getUsername() + ", Role: " + user.getRole() + ANSI_RESET + "\n");
                    System.out.println("Select an operation from the authorized permissions indicated above (case sensitive), or type "
                            + ANSI_ITALIC + "logout" + ANSI_RESET + ". HINTS:");
                    System.out.println("- To perform a read or write operation, precede the permission by "
                            + ANSI_ITALIC + "read" + ANSI_RESET + " or " + ANSI_ITALIC + "write" + ANSI_RESET
                            + ", e.g., " + ANSI_ITALIC + "read Client Information" + ANSI_RESET);
                    System.out.println("- To perform a special operation, simply enter the permission, e.g., "
                            + ANSI_ITALIC + "Request Technical Support" + ANSI_RESET + "\n");
                    boolean isThereAUserActionToProcess = true;
                    String userInput;
                    String[] delimitedUserInput;
                    while (isThereAUserActionToProcess) {
                        System.out.println("Enter operation:");
                        userInput = scanner.nextLine();
                        delimitedUserInput = userInput.split(" ");
                        if (delimitedUserInput.length == 0) continue;

                        // Enforce OBAC
                        String operation = delimitedUserInput[0];
                        StringBuilder object = null;
                        if (Objects.equals(operation, "read") || Objects.equals(operation, "write")) {
                            for (String word : delimitedUserInput) {
                                if (Objects.equals(word, operation)) continue;
                                object = (object == null ? new StringBuilder() : object).append(word);
                                if (!Objects.equals(word, delimitedUserInput[delimitedUserInput.length - 1])) {
                                    object.append(" ");
                                }
                            }
                        } else {
                            operation = userInput;
                        }
                        isThereAUserActionToProcess = accessControlPolicy.doOperation(user, operation, object == null ? null : object.toString());
                    }

                }
            } else {
                System.err.println("The given login credentials are invalid\n");
            }
            do { // Prompt for another login
                System.out.println("Log in again? (Y/N)");
                userWantsToRetryLogin = scanner.nextLine().trim();
                if (userWantsToRetryLogin.equalsIgnoreCase("Y")) {
                    System.out.println();
                }
            } while (!userWantsToRetryLogin.equalsIgnoreCase("Y") && !userWantsToRetryLogin.equalsIgnoreCase("N"));
        } while (userWantsToRetryLogin.equalsIgnoreCase("Y"));
        scanner.close();
        System.out.println("Program terminated");
        System.exit(0);
    }

}