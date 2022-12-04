package main.java.com.finvest;

import java.text.SimpleDateFormat;
import java.util.*;

import static main.java.com.finvest.RoleEnum.*;
import static main.java.com.finvest.UserLogin.ANSI_BLUE;
import static main.java.com.finvest.UserLogin.ANSI_RESET;

/**
 * Implements the Finvest Holdings access control policy, which integrates Role-, Attribute-, and Object-Based
 * Access Control models, represented as an access control matrix.
 *
 * @author Paul Roode
 */
public class AccessControlPolicy {

    // R/W permissions
    public final static String ACCOUNT_BALANCE = "Account Balance";
    public final static String CLIENT_INFORMATION = "Client Information";
    public final static String DERIVATIVES_TRADING = "Derivatives Trading";
    public final static String FINANCIAL_ADVISOR_CONTACT_DETAILS = "Financial Advisor Contact Details";
    public final static String FINANCIAL_PLANNER_CONTACT_DETAILS = "Financial Planner Contact Details";
    public final static String INTEREST_INSTRUMENTS = "Interest Instruments";
    public final static String INVESTMENT_ANALYST_CONTACT_DETAILS = "Investment Analyst Contact Details";
    public final static String INVESTMENT_PORTFOLIO = "Investment Portfolio";
    public final static String MONEY_MARKET_INSTRUMENTS = "Money Market Instruments";
    public final static String PRIVATE_CONSUMER_INSTRUMENTS = "Private Consumer Instruments";

    // Special permissions
    public final static String REQUEST_TECHNICAL_SUPPORT = "Request Technical Support";
    public final static String REQUEST_CLIENT_ACCOUNT_ACCESS = "Request Client Account Access";
    public final static String VALIDATE_MODIFICATION_OF_INVESTMENT_PORTFOLIO = "Validate Modification of Investment Portfolio";

    // Roles
    private final Role client;
    private final Role premiumClient;
    private final Role financialAdvisor;
    private final Role financialPlanner;
    private final Role investmentAnalyst;
    private final Role technicalSupport;
    private final Role teller;
    private final Role complianceOfficer;

    // Users with modifications pending validation by a Compliance Officer
    public Queue<User> usersWithModificationsPendingValidation;

    // Users granting permission to access their account, e.g., for technical support
    public Queue<User> usersGrantingAccountAccess;

    /**
     * Constructs an access control matrix representing the RBAC-ABAC-OBAC hybrid access control policy.
     */
    public AccessControlPolicy() {

        // Initialize roles
        client = new Role(CLIENT);
        premiumClient = new Role(PREMIUM_CLIENT);
        financialAdvisor = new Role(FINANCIAL_ADVISOR);
        financialPlanner = new Role(FINANCIAL_PLANNER);
        investmentAnalyst = new Role(INVESTMENT_ANALYST);
        technicalSupport = new Role(TECHNICAL_SUPPORT);
        teller = new Role(TELLER);
        complianceOfficer = new Role(COMPLIANCE_OFFICER);

        // Initialize RBAC permissions
        initRolePermissions();

        // Initialize fields utilized in OBAC operations pertaining to special permissions
        usersWithModificationsPendingValidation = new LinkedList<>();
        usersGrantingAccountAccess = new LinkedList<>();
    }

    /**
     * Initializes Role-Based Access Control (RBAC) permissions.
     */
    private void initRolePermissions() {
        initRegularClientPermissions();
        initPremiumClientPermissions();
        initFinancialAdvisorPermissions();
        initFinancialPlannerPermissions();
        initInvestmentAnalystPermissions();
        initTellerPermissions();
        initTechnicalSupportPermissions();
        initComplianceOfficerPermissions();
    }

    /**
     * Enforces Attribute-Based Access Control (ABAC) on the given user.
     *
     * @param user The user on which to enforce ABAC.
     * @return True if ABAC-enforced access was granted, false if access was denied.
     */
    public boolean enforceABAC(User user) {

        // Tellers can only access the system during business hours, i.e., between 9am and 5pm
        if (user.getRole().getRoleEnum().equals(TELLER)) {

            // Get the current time
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
            String time = simpleDateFormat.format(calendar.getTime());

            // Check if the given teller can access the system
            int hour = Integer.parseInt(time);
            if (hour < 9 || hour > 17) {
                System.err.println("System access is only available between 9am and 5pm (the current time is " + time + ":00");
                return false;
            }
        }
        return true;
    }

    /**
     * Implements Object-Based Access Control (OBAC) to enable users to perform a variety of authorized operations.
     *
     * @param user      The user requesting to perform an operation.
     * @param operation The operation that the user is requesting to perform.
     * @param object    An object on which to perform a read or write operation.
     * @return True if the program should continue running after the operation, false if it should subsequently terminate.
     */
    public boolean doOperation(User user, String operation, String object) {
        Role role = user.getRole();
        switch (operation) {
            case "read" -> { // Enforce OBAC on read operations
                if (role.getReadPermissions().contains(object)) {
                    System.out.println("Read permission granted to " + object + "\n");
                    return true;
                }
                System.err.println("Read permission denied\n");
            }

            case "write" -> { // Enforce OBAC on write operations
                if (role.getWritePermissions().contains(object)) {
                    System.out.println("Write permission granted to " + object);
                    if (object.equals(INVESTMENT_PORTFOLIO)) { // Modifications to investment portfolios must be validated by a Compliance Officer
                        System.out.println("Modification pending validation by a Compliance Officer\n");
                        usersWithModificationsPendingValidation.add(user);
                    }
                    return true;
                }
                System.err.println("Write permission denied\n");
            }

            case REQUEST_TECHNICAL_SUPPORT -> { // Enforce OBAC on technical support requests
                if (role.getSpecialPermissions().contains(REQUEST_TECHNICAL_SUPPORT)) {
                    System.out.println("Account access permission given to Technical Support\n");
                    usersGrantingAccountAccess.add(user);
                    return true;
                }
                System.err.println("You are not authorized to request technical support\n");
            }

            case VALIDATE_MODIFICATION_OF_INVESTMENT_PORTFOLIO -> { // Enforce OBAC on the validation of modifications to investment portfolios
                if (role.getSpecialPermissions().contains(VALIDATE_MODIFICATION_OF_INVESTMENT_PORTFOLIO)) {
                    if (usersWithModificationsPendingValidation.isEmpty()) {
                        System.out.println("There are no modifications pending validation\n");
                        return true;
                    }
                    while (!usersWithModificationsPendingValidation.isEmpty()) {
                        System.out.println("Modifications by " + usersWithModificationsPendingValidation.remove().getName() + " validated\n");
                    }
                    return true;
                }
                System.err.println("You are not authorized to validate modifications to investment portfolios\n");
            }

            case REQUEST_CLIENT_ACCOUNT_ACCESS -> { // Enforce OBAC on the requesting of client account access
                if (role.getSpecialPermissions().contains(REQUEST_CLIENT_ACCOUNT_ACCESS)) {
                    if (usersGrantingAccountAccess.isEmpty()) {
                        System.out.println("There are currently no clients granting access to their account\n");
                        return true;
                    }
                    while (!usersGrantingAccountAccess.isEmpty()) {
                        User userGrantingAccountAccess = usersGrantingAccountAccess.remove();
                        System.out.println("Access granted to the account of " + userGrantingAccountAccess.getName());
                    }

                    /* Augment permissions to authorize client account access, in this case represented by authorizing
                     * reading a client's Account Balance and Investment Portfolio */
                    role.addReadPermissions(new String[]{ACCOUNT_BALANCE, INVESTMENT_PORTFOLIO});
                    System.out.println("Permissions augmented to authorize client account access. The following permissions were updated:");
                    System.out.println(ANSI_BLUE + "Read permissions: "
                            + role.getReadPermissions().toString().replace("[", "").replace("]", "")
                            + ANSI_RESET + "\n");
                    return true;
                }
                System.err.println("You are not authorized to request access to client accounts\n");
            }

            case "logout" -> {
                System.out.println("Logging out...\n");
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the role represented by the given RoleEnum.
     *
     * @param roleEnum The RoleEnum representing the Role to retrieve.
     * @return The desired Role represented by the given RoleEnum.
     */
    public Role getRole(RoleEnum roleEnum) {
        Role role = null;
        switch (roleEnum) {
            case CLIENT -> role = client;
            case COMPLIANCE_OFFICER -> role = complianceOfficer;
            case FINANCIAL_ADVISOR -> role = financialAdvisor;
            case FINANCIAL_PLANNER -> role = financialPlanner;
            case INVESTMENT_ANALYST -> role = investmentAnalyst;
            case PREMIUM_CLIENT -> role = premiumClient;
            case TECHNICAL_SUPPORT -> role = technicalSupport;
            case TELLER -> role = teller;
            default -> {
                System.err.println("Invalid role passed to AccessControlPolicy::getRole");
                System.exit(1);
            }
        }
        return role;
    }

    /**
     * Initializes Regular Client permissions.
     */
    private void initRegularClientPermissions() {
        client.addReadPermissions(new String[]{
                CLIENT_INFORMATION,
                ACCOUNT_BALANCE,
                INVESTMENT_PORTFOLIO,
                FINANCIAL_ADVISOR_CONTACT_DETAILS
        });
        client.addWritePermissions(new String[]{
                CLIENT_INFORMATION
        });
        client.addSpecialPermissions(new String[]{
                REQUEST_TECHNICAL_SUPPORT
        });
    }

    /**
     * Initializes Premium Client permissions.
     */
    private void initPremiumClientPermissions() {
        premiumClient.addReadPermissions(new String[]{
                CLIENT_INFORMATION,
                ACCOUNT_BALANCE,
                INVESTMENT_PORTFOLIO,
                FINANCIAL_ADVISOR_CONTACT_DETAILS,
                FINANCIAL_PLANNER_CONTACT_DETAILS,
                INVESTMENT_ANALYST_CONTACT_DETAILS
        });
        premiumClient.addWritePermissions(new String[]{
                CLIENT_INFORMATION,
                INVESTMENT_PORTFOLIO
        });
        premiumClient.addSpecialPermissions(new String[]{
                REQUEST_TECHNICAL_SUPPORT
        });
    }

    /**
     * Initializes Financial Advisor permissions.
     */
    private void initFinancialAdvisorPermissions() {
        financialAdvisor.addReadPermissions(new String[]{
                CLIENT_INFORMATION,
                ACCOUNT_BALANCE,
                INVESTMENT_PORTFOLIO,
                FINANCIAL_ADVISOR_CONTACT_DETAILS,
                FINANCIAL_PLANNER_CONTACT_DETAILS,
                INVESTMENT_ANALYST_CONTACT_DETAILS,
                PRIVATE_CONSUMER_INSTRUMENTS
        });
        financialAdvisor.addWritePermissions(new String[]{
                INVESTMENT_PORTFOLIO,
                FINANCIAL_ADVISOR_CONTACT_DETAILS
        });
    }

    /**
     * Initializes Financial Planner permissions.
     */
    private void initFinancialPlannerPermissions() {
        financialPlanner.addReadPermissions(new String[]{
                CLIENT_INFORMATION,
                ACCOUNT_BALANCE,
                INVESTMENT_PORTFOLIO,
                FINANCIAL_ADVISOR_CONTACT_DETAILS,
                FINANCIAL_PLANNER_CONTACT_DETAILS,
                INVESTMENT_ANALYST_CONTACT_DETAILS,
                PRIVATE_CONSUMER_INSTRUMENTS,
                MONEY_MARKET_INSTRUMENTS
        });
        financialPlanner.addWritePermissions(new String[]{
                INVESTMENT_PORTFOLIO,
                FINANCIAL_PLANNER_CONTACT_DETAILS
        });
    }

    /**
     * Initializes Investment Analyst permissions.
     */
    private void initInvestmentAnalystPermissions() {
        investmentAnalyst.addReadPermissions(new String[]{
                CLIENT_INFORMATION,
                ACCOUNT_BALANCE,
                INVESTMENT_PORTFOLIO,
                FINANCIAL_ADVISOR_CONTACT_DETAILS,
                FINANCIAL_PLANNER_CONTACT_DETAILS,
                INVESTMENT_ANALYST_CONTACT_DETAILS,
                PRIVATE_CONSUMER_INSTRUMENTS,
                MONEY_MARKET_INSTRUMENTS,
                DERIVATIVES_TRADING,
                INTEREST_INSTRUMENTS
        });
        investmentAnalyst.addWritePermissions(new String[]{
                INVESTMENT_PORTFOLIO,
                INVESTMENT_ANALYST_CONTACT_DETAILS
        });
    }

    /**
     * Initializes Teller permissions.
     */
    private void initTellerPermissions() {
        teller.addReadPermissions(new String[]{
                CLIENT_INFORMATION,
                ACCOUNT_BALANCE,
                INVESTMENT_PORTFOLIO,
                FINANCIAL_ADVISOR_CONTACT_DETAILS,
                FINANCIAL_PLANNER_CONTACT_DETAILS,
                INVESTMENT_ANALYST_CONTACT_DETAILS,
        });
    }

    /**
     * Initializes Technical Support permissions.
     */
    private void initTechnicalSupportPermissions() {
        technicalSupport.addReadPermissions(new String[]{
                CLIENT_INFORMATION,
                FINANCIAL_ADVISOR_CONTACT_DETAILS,
                FINANCIAL_PLANNER_CONTACT_DETAILS,
                INVESTMENT_ANALYST_CONTACT_DETAILS,
        });
        technicalSupport.addSpecialPermissions(new String[]{
                REQUEST_CLIENT_ACCOUNT_ACCESS
        });
    }

    /**
     * Initializes Compliance Officer permissions.
     */
    private void initComplianceOfficerPermissions() {
        complianceOfficer.addReadPermissions(new String[]{
                CLIENT_INFORMATION,
                INVESTMENT_PORTFOLIO
        });
        complianceOfficer.addSpecialPermissions(new String[]{
                VALIDATE_MODIFICATION_OF_INVESTMENT_PORTFOLIO
        });
    }

}