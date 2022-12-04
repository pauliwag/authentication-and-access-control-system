package main.java.com.finvest;

import java.util.UUID;

/**
 * A Finvest Holdings user.
 *
 * @author Paul Roode
 */
public class User {

    private final UUID id;
    private final Role role;
    private final String username;

    // Contact details
    private final String name;
    private final String phoneNumber;
    private final String email;

    /**
     * Constructs a Finvest Holdings user with their given information.
     *
     * @param role        The user's role, according to the access control policy.
     * @param username    The user's username.
     * @param name        The user's name.
     * @param phoneNumber The user's phone number.
     * @param email       The user's email.
     */
    public User(RoleEnum role, String username, String name, String phoneNumber, String email) {

        // Assign the user's ID
        id = UUID.randomUUID();

        // Assign the user's role as per the access control policy
        this.role = new AccessControlPolicy().getRole(role);

        this.username = username;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // Getters
    public UUID getID() { return id; }
    public Role getRole() { return role; }
    public String getUsername() { return username; }
    public String getName() { return name; }

    @Override
    public String toString() { return name + ", " + phoneNumber + ", " + email; }

}