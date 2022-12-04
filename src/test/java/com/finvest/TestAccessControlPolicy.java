package test.java.com.finvest;

import main.java.com.finvest.AccessControlPolicy;
import main.java.com.finvest.User;

import static main.java.com.finvest.RoleEnum.*;

/**
 * Drives the testing of Finvest Holdings' access control policy.
 *
 * @author Paul Roode
 */
public class TestAccessControlPolicy {

    public static void main(String[] argv) {
        testRoles();
        testOperationAuthorization();
        System.out.println("Error messages:");
    }

    /**
     * Tests that system roles abide the access control policy.
     */
    public static void testRoles() {
        System.out.println("\nTestAccessControlPolicy::testRoles results:");
        System.out.println("==================================================");
        System.out.println("Testing Client permissions...\n");
        User client = new User(CLIENT, "mlowery", "Mischa Lowery", "(555) 555-5555", "mlowery@finvest.ca");
        System.out.println("Expected:");
        System.out.println(
                """
                        Client
                        Read permissions: Client Information, Account Balance, Investment Portfolio, Financial Advisor Contact Details
                        Write permissions: Client Information
                        Special permissions: Request Technical Support""");
        System.out.println("\nActual:");
        System.out.println(client.getRole());
        System.out.println("--------------------------------------------------");

        System.out.println("Testing Premium Client permissions...\n");
        User premiumClient = new User(PREMIUM_CLIENT, "wgarza", "Willow Garza", "(555) 555-5555", "wgarza@finvest.ca");
        System.out.println("Expected:");
        System.out.println(
                """
                        Premium Client
                        Read permissions: Client Information, Account Balance, Investment Portfolio, Financial Advisor Contact Details, Financial Planner Contact Details, Investment Analyst Contact Details
                        Write permissions: Client Information, Investment Portfolio
                        Special permissions: Request Technical Support""");
        System.out.println("\nActual:");
        System.out.println(premiumClient.getRole());
        System.out.println("--------------------------------------------------");

        System.out.println("Testing Financial Advisor permissions...\n");
        User financialAdvisor = new User(FINANCIAL_ADVISOR, "nwilkins", "Nelson Wilkins", "(555) 555-5555", "nwilkins@finvest.ca");
        System.out.println("Expected:");
        System.out.println(
                """
                        Financial Advisor
                        Read permissions: Client Information, Account Balance, Investment Portfolio, Financial Advisor Contact Details, Financial Planner Contact Details, Investment Analyst Contact Details, Private Consumer Instruments
                        Write permissions: Investment Portfolio, Financial Advisor Contact Details
                        Special permissions:""");
        System.out.println("\nActual:");
        System.out.println(financialAdvisor.getRole());
        System.out.println("--------------------------------------------------");

        System.out.println("Testing Financial Planner permissions...\n");
        User financialPlanner = new User(FINANCIAL_PLANNER, "kmatthews", "Kodi Matthews", "(555) 555-5555", "kmatthews@finvest.ca");
        System.out.println("Expected:");
        System.out.println(
                """
                        Financial Planner
                        Read permissions: Client Information, Account Balance, Investment Portfolio, Financial Advisor Contact Details, Financial Planner Contact Details, Investment Analyst Contact Details, Private Consumer Instruments, Money Market Instruments
                        Write permissions: Investment Portfolio, Financial Planner Contact Details
                        Special permissions:""");
        System.out.println("\nActual:");
        System.out.println(financialPlanner.getRole());
        System.out.println("--------------------------------------------------");

        System.out.println("Testing Investment Analyst permissions...\n");
        User investmentAnalyst = new User(INVESTMENT_ANALYST, "skent", "Stacy Kent", "(555) 555 5555", "skent@finvest.ca");
        System.out.println("Expected:");
        System.out.println(
                """
                        Investment Analyst
                        Read permissions: Client Information, Account Balance, Investment Portfolio, Financial Advisor Contact Details, Financial Planner Contact Details, Investment Analyst Contact Details, Private Consumer Instruments, Money Market Instruments, Derivatives Trading, Interest Instruments
                        Write permissions: Investment Portfolio, Investment Analyst Contact Details
                        Special permissions:""");
        System.out.println("\nActual:");
        System.out.println(investmentAnalyst.getRole());
        System.out.println("--------------------------------------------------");

        System.out.println("Testing Teller permissions...\n");
        User teller = new User(TELLER, "wcallahan", "Winston Callahan", "(555) 555-5555", "wcallahan@finvest.ca");
        System.out.println("Expected:");
        System.out.println(
                """
                        Teller
                        Read permissions: Client Information, Account Balance, Investment Portfolio, Financial Advisor Contact Details, Financial Planner Contact Details, Investment Analyst Contact Details
                        Write permissions:
                        Special permissions:""");
        System.out.println("\nActual:");
        System.out.println(teller.getRole());
        System.out.println("--------------------------------------------------");

        System.out.println("Testing Technical Support permissions...\n");
        User technicalSupport = new User(TECHNICAL_SUPPORT, "clopez", "Caroline Lopez", "(555) 555-5555", "clopez@finvest.ca");
        System.out.println("Expected:");
        System.out.println(
                """
                        Technical Support
                        Read permissions: Client Information, Financial Advisor Contact Details, Financial Planner Contact Details, Investment Analyst Contact Details
                        Write permissions:
                        Special permissions: Request Client Account Access""");
        System.out.println("\nActual:");
        System.out.println(technicalSupport.getRole());
        System.out.println("--------------------------------------------------");

        System.out.println("Testing Compliance Officer permissions...\n");
        User complianceOfficer = new User(COMPLIANCE_OFFICER, "hlinkler", "Howard Linkler", "(555) 555-5555", "hlinkler@finvest.ca");
        System.out.println("Expected:");
        System.out.println(
                """
                        Compliance Officer
                        Read permissions: Client Information, Investment Portfolio
                        Write permissions:
                        Special permissions: Validate Modification of Investment Portfolio""");
        System.out.println("\nActual:");
        System.out.println(complianceOfficer.getRole());
        System.out.println("--------------------------------------------------");
    }

    /**
     * Tests access control policy enforcement.
     */
    public static void testOperationAuthorization() {
        System.out.println("\nTestAccessControlPolicy::testOperationAuthorization results:");
        System.out.println("==================================================");
        AccessControlPolicy accessControlPolicy = new AccessControlPolicy();
        System.out.println("Testing an authorized read operation...\n");
        User premiumClient = new User(PREMIUM_CLIENT, "wgarza", "Willow Garza", "(555) 555-5555", "wgarza@finvest.ca");
        System.out.println("Expected:");
        System.out.println("Read permission granted to Financial Planner Contact Details");
        System.out.println("\nActual:");
        accessControlPolicy.doOperation(premiumClient, "read", "Financial Planner Contact Details");
        System.out.println("--------------------------------------------------");

        System.out.println("Testing an authorized write operation...\n");
        System.out.println("Expected:");
        System.out.println("Write permission granted to Client Information");
        System.out.println("\nActual:");
        accessControlPolicy.doOperation(premiumClient, "write", "Client Information");
        System.out.println("--------------------------------------------------");

        System.out.println("Testing an unauthorized read operation...\n");
        System.out.println("Expected:");
        System.out.println("Read permission denied");
        System.out.println("\nActual:");
        accessControlPolicy.doOperation(premiumClient, "read", "Derivatives Trading");
        System.out.println("--------------------------------------------------");

        System.out.println("Testing an unauthorized write operation...\n");
        System.out.println("Expected:");
        System.out.println("Write permission denied");
        System.out.println("\nActual:");
        accessControlPolicy.doOperation(premiumClient, "write", "Account Balance");
        System.out.println("--------------------------------------------------");

        System.out.println("Testing modification of an Investment Portfolio by a Financial Planner...\n");
        User financialPlanner = new User(FINANCIAL_PLANNER, "kmatthews", "Kodi Matthews", "(555) 555-5555", "kmatthews@finvest.ca");
        System.out.println("Expected:");
        System.out.println("Write permission granted to Investment Portfolio\n"
                + "Modification pending validation by a Compliance Officer");
        System.out.println("\nActual:");
        accessControlPolicy.doOperation(financialPlanner, "write", "Investment Portfolio");
        System.out.println("--------------------------------------------------");

        System.out.println("Testing modification of an Investment Portfolio by a Financial Advisor...\n");
        User financialAdvisor = new User(FINANCIAL_ADVISOR, "nwilkins", "Nelson Wilkins", "(555) 555-5555", "nwilkins@finvest.ca");
        System.out.println("Expected:");
        System.out.println("Write permission granted to Investment Portfolio\nModification pending validation by a Compliance Officer");
        System.out.println("\nActual:");
        accessControlPolicy.doOperation(financialAdvisor, "write", "Investment Portfolio");
        System.out.println("--------------------------------------------------");

        System.out.println("Testing the validation of said modifications by a Compliance Officer...\n");
        User complianceOfficer = new User(COMPLIANCE_OFFICER, "hlinkler", "Howard Linkler", "(555) 555-5555", "hlinkler@finvest.ca");
        System.out.println("Expected:");
        System.out.println("Modifications by Kodi Matthews validated\n\nModifications by Nelson Wilkins validated");
        System.out.println("\nActual:");
        accessControlPolicy.doOperation(complianceOfficer, "Validate Modification of Investment Portfolio", null);
        System.out.println("--------------------------------------------------");

        System.out.println("Testing a Client granting Technical Support permission to access their account...\n");
        System.out.println("Expected:");
        System.out.println("Account access permission given to Technical Support");
        System.out.println("\nActual: ");
        accessControlPolicy.doOperation(premiumClient, "Request Technical Support", null);
        System.out.println("--------------------------------------------------");

        System.out.println("Testing Technical Support permissions augmentation...\n");
        User technicalSupport = new User(TECHNICAL_SUPPORT, "clopez", "Caroline Lopez", "(555) 555-5555", "clopez@finvest.ca");
        System.out.println("Expected:");
        System.out.println(
                """
                        Access granted to the account of Willow Garza
                        Permissions augmented to authorize client account access. The following permissions were updated:
                        Read permissions: Client Information, Financial Advisor Contact Details, Financial Planner Contact Details, Investment Analyst Contact Details, Account Balance, Investment Portfolio""");
        System.out.println("\nActual:");
        accessControlPolicy.doOperation(technicalSupport, "Request Client Account Access", null);
        System.out.println("--------------------------------------------------");

        System.out.println("Testing newly augmented Technical Support permissions...\n");
        System.out.println("Expected:");
        System.out.println("Read permission granted to Investment Portfolio");
        System.out.println("\nActual:");
        accessControlPolicy.doOperation(technicalSupport, "read", "Investment Portfolio");
        System.out.println("--------------------------------------------------");
    }

}