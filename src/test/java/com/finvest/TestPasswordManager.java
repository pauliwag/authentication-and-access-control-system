package test.java.com.finvest;

import main.java.com.finvest.PasswordManager;

/**
 * Drives the testing of the password manager.
 *
 * @author Paul Roode
 */
public class TestPasswordManager {

    public static void main(String[] argv) {
        testProactivePasswordChecker();
        System.out.println("Error messages:");
    }

    /**
     * Verifies the proactive password checker.
     */
    public static void testProactivePasswordChecker() {
        PasswordManager passwordManager = new PasswordManager();
        boolean isPasswordValid;
        System.out.println("\nTestPasswordManager::testProactivePasswordChecker results:");
        System.out.println("==========================================================");
        System.out.println("Testing the validity of a password that is too short...");
        isPasswordValid = passwordManager.checkPassword("test", "Sh0rt!");
        System.out.println("Expected: false\nActual: " + isPasswordValid);
        System.out.println("----------------------------------------------------------");

        System.out.println("Testing the validity of a password that is too long...");
        isPasswordValid = passwordManager.checkPassword("test", "thisP@ssw0rdIsTooLong");
        System.out.println("Expected: false\nActual: " + isPasswordValid);
        System.out.println("----------------------------------------------------------");

        System.out.println("Testing the validity of a password that equals the username...");
        isPasswordValid = passwordManager.checkPassword("T3$ttest", "T3$ttest");
        System.out.println("Expected: false\nActual: " + isPasswordValid);
        System.out.println("----------------------------------------------------------");

        System.out.println("Testing the validity of a password without an uppercase character...");
        isPasswordValid = passwordManager.checkPassword("test", "n0upperc@se");
        System.out.println("Expected: false\nActual: " + isPasswordValid);
        System.out.println("----------------------------------------------------------");

        System.out.println("Testing the validity of a password without a lowercase character...");
        isPasswordValid = passwordManager.checkPassword("test", "N0LOWERC@SE");
        System.out.println("Expected: false\nActual: " + isPasswordValid);
        System.out.println("----------------------------------------------------------");

        System.out.println("Testing the validity of a password without a number...");
        isPasswordValid = passwordManager.checkPassword("test", "noNumber!");
        System.out.println("Expected: false\nActual: " + isPasswordValid);
        System.out.println("----------------------------------------------------------");

        System.out.println("Testing the validity of a password without an approved special character...");
        isPasswordValid = passwordManager.checkPassword("test", "noSp3cial");
        System.out.println("Expected: false\nActual: " + isPasswordValid);
        System.out.println("----------------------------------------------------------");

        System.out.println("Testing the validity of a common weak password...");
        isPasswordValid = passwordManager.checkPassword("test", "Pa$$word1");
        System.out.println("Expected: false\nActual: " + isPasswordValid);
        System.out.println("----------------------------------------------------------");

        System.out.println("Testing the validity of a valid password...");
        isPasswordValid = passwordManager.checkPassword("test", "aV@lid0ne!");
        System.out.println("Expected: true\nActual: " + isPasswordValid);
        System.out.println("----------------------------------------------------------");
    }

}