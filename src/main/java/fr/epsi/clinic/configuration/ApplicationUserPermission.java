package fr.epsi.clinic.configuration;

public enum ApplicationUserPermission {
    USER_WRITE("user:write"),
    USER_READ("user:read");

    private final String permission;

    ApplicationUserPermission(String permission){
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
