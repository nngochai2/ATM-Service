package org.atm.model;

public class Admin {
    private String adminId;
    private String username;
    private String password;
    private String name;
    private String email;
    private boolean isActive;

    public Admin() {}

    public Admin(String adminId, String username, String password, String name, String email, boolean isActive) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.isActive = isActive;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
