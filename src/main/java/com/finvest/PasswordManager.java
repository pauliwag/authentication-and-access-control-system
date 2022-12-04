package main.java.com.finvest;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

import static main.java.com.finvest.RoleEnum.getRoleEnum;

/**
 * Manages the user password store.
 *
 * @author Paul Roode
 */
public class PasswordManager {

    public List<String> prohibitedPasswords; // A list of prohibited passwords

    /**
     * Constructs a PasswordManager for initializing and managing the password store.
     */
    public PasswordManager() {

        // Initialize passwd.txt in the root directory
        File passwd = new File("./passwd.txt");
        try {
            passwd.createNewFile();
            passwd.getParentFile().mkdirs();
        } catch (IOException e) {
            System.err.println("Unable to create passwd.txt file");
            e.printStackTrace();
        }

        // Set passwd.txt access permissions: readable to all users but writable only to the owner
        passwd.setReadable(true, false);
        passwd.setWritable(true);

        // Initialize the list of prohibited passwords with common weak passwords
        prohibitedPasswords = new ArrayList<>();
        String[] commonWeakPasswords = {
                "Pa$$word1",
                "Qwerty123!",
                "Q@z123wsx"
        };
        addProhibitedPasswords(commonWeakPasswords);
    }

    /**
     * Adds a user record to the passwd.txt file in the format:
     * username:salt:hash:role:contactInfo
     *
     * @param username    The user's username.
     * @param password    The user's password, an input to the password hashing algorithm.
     * @param role        The user's role.
     * @param name        The user's name.
     * @param phoneNumber The user's phone number.
     * @param email       The user's email address.
     * @return True if the record was successfully added to passwd.txt, false otherwise.
     */
    public boolean addUserRecordToPasswordFile(String username, String password, String role, String name, String phoneNumber, String email) {

        // Generate a 16-byte salt for hashing the user's password
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String saltStr = Base64.getEncoder().encodeToString(salt);

        // Generate a salted hash of the user's password
        String saltedHash = getSaltedHash(password, salt);

        // Generate the user record
        String userRecord = username + ":" + saltStr + ":" + saltedHash + ":" + role + ":" + name + "," + phoneNumber + "," + email;

        // Append the user record to passwd.txt
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./passwd.txt", true));
            writer.append(userRecord).append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false; // The record was not added
        }
        return true; // The record was successfully appended to passwd.txt
    }

    /**
     * Gets a validated User by parsing passwd.txt for a user record containing the given attributes.
     *
     * @param username The username of the User to get.
     * @param password The password of the User to get.
     * @return The validated User with the given username and password.
     */
    public User getValidatedUser(String username, String password) {
        User user;
        String[] userAttributes;
        try { // Parse passwd.txt for the given username
            BufferedReader reader = new BufferedReader(new FileReader("./passwd.txt"));
            String userRecord;
            while ((userRecord = reader.readLine()) != null) {
                userAttributes = userRecord.split(":"); // Split the record using ":" as the delimiter
                if (userAttributes[0].equals(username)) {
                    System.out.println("Verifying credentials...");

                    /* Recreate the salted hash of the user's password utilizing the given password
                     * and the salt retrieved from the user record in passwd.txt */
                    byte[] salt = Base64.getDecoder().decode(userAttributes[1]);
                    String recreatedSaltedHash = getSaltedHash(password, salt);

                    // Verify the given password by comparing the recreated salted hash to that stored in passwd.txt
                    if (Objects.equals(recreatedSaltedHash, userAttributes[2])) {
                        String[] userContactDetails = userAttributes[4].split(",");
                        user = new User(getRoleEnum(userAttributes[3]), userAttributes[0], userContactDetails[0], userContactDetails[1], userContactDetails[2]);
                        reader.close();
                        return user;
                    }
                }
            }
            reader.close(); // Unable to validate user
            return null;
        } catch (IOException e) {
            System.err.println("Unable to read passwd.txt file");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a salted hash of the given password utilizing the given salt.
     * Adapted from <a href="https://www.baeldung.com/java-password-hashing">...</a>
     *
     * @param password The password to hash.
     * @param salt     A byte array utilized to salt the hash of the given password.
     * @return The salted hash of the given password.
     */
    private String getSaltedHash(String password, byte[] salt) {
        String saltedHashStr;
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] saltedHash = secretKeyFactory.generateSecret(keySpec).getEncoded();
            saltedHashStr = Base64.getEncoder().encodeToString(saltedHash);
            return saltedHashStr;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * A proactive password checker for ensuring that all passwords adhere to the password policy.
     *
     * @param username The username of the user whose password is to be checked.
     * @param password The password to check.
     * @return True if the given password adheres to the password policy, false otherwise.
     */
    public boolean checkPassword(String username, String password) {

        // Ensure the password is 8 to 12 characters long, inclusive
        if (password.length() < 8 || password.length() > 12) {
            System.err.println("Your password must be 8 to 12 characters long, inclusive");
            return false;
        }

        /* Ensure the password contains at least one upper-case letter, one lower-case letter, one numerical digit, and
         * one special character from the set {!, @, #, $, %, ?, *}, thereby prohibiting numerical date (e.g., 31-10-2022),
         * license plate number (as they are exclusively upper case and/or numerical), and telephone number formats */
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        char[] specialChars = {'!', '@', '#', '$', '%', '?', '*'};
        for (int i = 0; i < password.length(); ++i) {
            if (Character.isUpperCase(password.charAt(i))) {
                hasUppercase = true;
            }
            if (Character.isLowerCase(password.charAt(i))) {
                hasLowercase = true;
            }
            if (Character.isDigit(password.charAt(i))) {
                hasDigit = true;
            }
            for (char c : specialChars) {
                if (password.charAt(i) == c) {
                    hasSpecialChar = true;
                    break;
                }
            }
            if (i == password.length() - 1) {
                if (!hasUppercase || !hasLowercase || !hasDigit || !hasSpecialChar) {
                    System.err.println("Your password must contain at least one upper-case letter, one lower-case letter, " +
                            "one numerical digit, and one special character from the set {!, @, #, $, %, ?, *}");
                    return false;
                }
            }
        }

        // Ensure the password is not in the list of prohibited (e.g., common weak) passwords
        if (prohibitedPasswords.contains(password)) {
            System.err.println("Your password is too weak");
            return false;
        }

        // Ensure the username and password are unequal
        if (password.equals(username)) {
            System.err.println("Your password must be different from your username");
            return false;
        }

        return true; // The password is acceptable
    }

    /**
     * Validates the given role.
     *
     * @param role The role to validate.
     * @return True if the given role is valid, false otherwise.
     */
    public boolean validateRole(String role) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.toString().equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the given passwords to the list of prohibited passwords.
     *
     * @param passwords An array of passwords to add to the list of prohibited passwords.
     */
    public void addProhibitedPasswords(String[] passwords) {
        Collections.addAll(prohibitedPasswords, passwords);
    }

}