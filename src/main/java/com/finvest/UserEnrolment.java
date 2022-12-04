package main.java.com.finvest;

import java.util.Scanner;

/**
 * Drives user enrolment into the Finvest Holdings system.
 *
 * @author Paul Roode
 */
public class UserEnrolment {

    public static void main(String[] argv) {

        /* Instantiate a password manager to be utilized for proactive password checking, validating roles, and
         * adding user records to the password file */
        PasswordManager passwordManager = new PasswordManager();

        // Initiate user enrolment
        System.out.println("Finvest Holdings");
        System.out.println("Client Holdings and Information System");
        System.out.println("User Enrolment");
        System.out.println("--------------------------------------------------");
        Scanner scanner = new Scanner(System.in);
        String userWantsToEnrolAnotherUser; // Stores the user's choice of whether to enrol more than one user
        do {
            String username;
            do { // Prompt for username
                System.out.println("Enter username: ");
                username = scanner.nextLine().trim();
            } while (username.isEmpty() || username.equals("/n"));
            String password;
            do { // Prompt for password
                System.out.println("Enter password: ");
                password = scanner.nextLine();
            } while (!passwordManager.checkPassword(username, password)); // Proactive password checking

            // Indicate the role options from which a user can select their role
            System.out.println("\nSelect your role from the following options:");
            for (RoleEnum role : RoleEnum.values()) {
                System.out.println("- " + role);
            }
            System.out.println();
            String role;
            do { // Prompt for role selection
                System.out.println("Enter role selection (case sensitive): ");
                role = scanner.nextLine().trim();
            } while (!passwordManager.validateRole(role)); // Role selection validation
            String name;
            do { // Prompt for name
                System.out.println("Enter name: ");
                name = scanner.nextLine().trim();
            } while (name.isEmpty() || name.equals("/n"));
            String phoneNumber;
            do { // Prompt for phone number
                System.out.println("Enter phone number: ");
                phoneNumber = scanner.nextLine().trim();
            } while (phoneNumber.isEmpty() || phoneNumber.equals("/n"));
            String email;
            do { // Prompt for email
                System.out.println("Enter email address: ");
                email = scanner.nextLine().trim();
            } while (email.isEmpty() || email.equals("/n"));

            // Add a user record to the password file
            if (passwordManager.addUserRecordToPasswordFile(username, password, role, name, phoneNumber, email)) {
                System.out.println("You were successfully enrolled into the system!\n");
            } else {
                System.err.println("Unable to create user record");
                System.exit(1);
            }

            do { // Prompt for additional enrolment
                System.out.println("Enrol another user? (Y/N)");
                userWantsToEnrolAnotherUser = scanner.nextLine().trim();
                if (userWantsToEnrolAnotherUser.equalsIgnoreCase("Y")) {
                    System.out.println();
                }
            } while (!userWantsToEnrolAnotherUser.equalsIgnoreCase("Y") && !userWantsToEnrolAnotherUser.equalsIgnoreCase("N"));
        } while (userWantsToEnrolAnotherUser.equalsIgnoreCase("Y"));
        scanner.close();
        System.out.println("Enrolment complete");
        System.exit(0);
    }

}