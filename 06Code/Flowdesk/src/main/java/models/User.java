package models;

import enums.Role;
import exceptions.ValidationException;
import utils.Validation;
import utils.ConsoleInput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    private String userId;
    private String username;
    private String password;
    private Role role;

    private static List<User> users = new ArrayList<>();

    public User() {}

    public User(String userId, String username, String password, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        validate();
    }

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        validate();
    }

    public void validate() {
        Validation.requireText(userId, "identificador de usuario");
        Validation.requireText(username, "nombre de usuario");
        Validation.requireText(password, "contrasena");
        Validation.requireValue(role, "rol");
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setUserId(String userId) {
        if (users.contains(this)) {
            for (User existing : users) {
                if (existing != this
                        && existing.getUserId() != null
                        && existing.getUserId().equals(userId)) {
                    throw new ValidationException("Ya existe un usuario con ese ID.");
                }
            }
        }
        this.userId = userId;
    }

    public void setUsername(String username) {
        if (users.contains(this)) {
            for (User existing : users) {
                if (existing != this
                        && existing.getUsername() != null
                        && existing.getUsername().equalsIgnoreCase(username)) {
                    throw new ValidationException("Ya existe un usuario con ese nombre.");
                }
            }
        }
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void register() {
        validate();

        for (User existing : users) {
            if (existing != this) {
                if (existing.getUserId() != null && existing.getUserId().equals(this.userId)) {
                    throw new ValidationException("Ya existe un usuario con ese ID.");
                }
                if (existing.getUsername() != null
                        && existing.getUsername().equalsIgnoreCase(this.username)) {
                    throw new ValidationException(
                            "Ya existe un usuario con ese nombre de usuario.");
                }
            }
        }

        if (!users.contains(this)) {
            users.add(this);
        }
    }

    public void login() {
        System.out.println("Sesion iniciada exitosamente para el usuario: " + username);
    }

    public void logout() {
        System.out.println("Sesion cerrada.");
    }

    public static List<User> getAll() {
        return users;
    }

    public static void setAll(List<User> userList) {
        users = userList == null ? new ArrayList<>() : new ArrayList<>(userList);
    }

    public static void setUsers(List<User> userList) {
        setAll(userList);
    }

    public boolean validateCredentials(String username, String password) {
        return this.username != null
                && this.username.equals(username)
                && this.password != null
                && this.password.equals(password);
    }

    public static User authenticateFromConsole(Role role, Scanner scanner) {
        return authenticateFromConsole(role, scanner, null);
    }

    public static User authenticateFromConsole(Role role, Scanner scanner, Runnable saveCallback) {
        System.out.println("\n--- INICIO DE SESION: " + role + " ---");
        String username = ConsoleInput.readRequiredText(scanner, "Usuario: ").trim();
        String password = ConsoleInput.readRequiredText(scanner, "Contrasena: ").trim();

        for (User u : users) {
            if (u.getUsername().equals(username)) {
                if (u.getRole() != role) {
                    System.out.println("El usuario no pertenece al rol seleccionado.");
                    return null;
                }
                if (u.getPassword().equals(password)) {
                    return u;
                } else {
                    System.out.println("Contrasena incorrecta.");
                    return null;
                }
            }
        }

        String newUserId = "U_" + role.name();

        for (User u : users) {
            if (u.getUserId() != null && u.getUserId().equals(newUserId)) {
                newUserId = newUserId + "_" + System.currentTimeMillis();
                break;
            }
        }

        User newUser = new User(newUserId, username, password, role);
        users.add(newUser);

        if (saveCallback != null) {
            try {
                saveCallback.run();
            } catch (RuntimeException e) {
                users.remove(newUser);
                throw e;
            }
        }
        return newUser;
    }
}
