package main.java.com.finvest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages role permissions.
 *
 * @author Paul Roode
 */
public class Role {

    private final RoleEnum role;
    private final List<String> readPermissions;
    private final List<String> writePermissions;
    private final List<String> specialPermissions;

    /**
     * Constructs the given role.
     *
     * @param role The role to construct.
     */
    public Role(RoleEnum role) {

        // Assign the given role
        this.role = role;

        // Initialize permissions
        readPermissions = new ArrayList<>();
        writePermissions = new ArrayList<>();
        specialPermissions = new ArrayList<>();
    }

    // Getters
    public RoleEnum getRoleEnum() { return role; }
    public List<String> getReadPermissions() { return readPermissions; }
    public List<String> getWritePermissions() { return writePermissions; }
    public List<String> getSpecialPermissions() { return specialPermissions; }

    // Methods for augmenting permissions
    public void addReadPermissions(String[] readPermissions) { Collections.addAll(this.readPermissions, readPermissions); }
    public void addWritePermissions(String[] writePermissions) { Collections.addAll(this.writePermissions, writePermissions); }
    public void addSpecialPermissions(String[] specialPermissions) { Collections.addAll(this.specialPermissions, specialPermissions); }

    @Override
    public String toString() {
        return role
                + "\nRead permissions: " + readPermissions.toString().replace("[","").replace("]","")
                + "\nWrite permissions: " + writePermissions.toString().replace("[","").replace("]","")
                + "\nSpecial permissions: " + specialPermissions.toString().replace("[","").replace("]","");
    }

}