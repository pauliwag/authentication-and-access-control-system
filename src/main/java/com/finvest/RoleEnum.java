package main.java.com.finvest;

/**
 * An enumeration of Finvest Holdings system roles.
 *
 * @author Paul Roode
 */
public enum RoleEnum {

    CLIENT             ( Roles.CLIENT             ),
    PREMIUM_CLIENT     ( Roles.PREMIUM_CLIENT     ),
    FINANCIAL_ADVISOR  ( Roles.FINANCIAL_ADVISOR  ),
    FINANCIAL_PLANNER  ( Roles.FINANCIAL_PLANNER  ),
    INVESTMENT_ANALYST ( Roles.INVESTMENT_ANALYST ),
    TELLER             ( Roles.TELLER             ),
    TECHNICAL_SUPPORT  ( Roles.TECHNICAL_SUPPORT  ),
    COMPLIANCE_OFFICER ( Roles.COMPLIANCE_OFFICER );

    private final String role;

    // Constructors
    RoleEnum(String role) { this.role = role; }

    // Getters
    public static RoleEnum getRoleEnum(String role) {
        return switch (role) {
            case Roles.CLIENT             -> CLIENT;
            case Roles.PREMIUM_CLIENT     -> PREMIUM_CLIENT;
            case Roles.FINANCIAL_ADVISOR  -> FINANCIAL_ADVISOR;
            case Roles.FINANCIAL_PLANNER  -> FINANCIAL_PLANNER;
            case Roles.INVESTMENT_ANALYST -> INVESTMENT_ANALYST;
            case Roles.TELLER             -> TELLER;
            case Roles.TECHNICAL_SUPPORT  -> TECHNICAL_SUPPORT;
            case Roles.COMPLIANCE_OFFICER -> COMPLIANCE_OFFICER;
            default                       -> null;
        };
    }

    @Override
    public String toString() {
        return role;
    }

    /**
     * An auxiliary static nested class for extracting RoleEnum constants.
     */
    private static class Roles {
        public final static String CLIENT             = "Client";
        public final static String PREMIUM_CLIENT     = "Premium Client";
        public final static String FINANCIAL_ADVISOR  = "Financial Advisor";
        public final static String FINANCIAL_PLANNER  = "Financial Planner";
        public final static String INVESTMENT_ANALYST = "Investment Analyst";
        public final static String TELLER             = "Teller";
        public final static String TECHNICAL_SUPPORT  = "Technical Support";
        public final static String COMPLIANCE_OFFICER = "Compliance Officer";
    }

}