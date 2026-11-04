package models;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import utils.ConsoleInput;
import utils.Validation;
import exceptions.ValidationException;

public class Customer {
    private String customerId;
    private String name;
    private String phone;
    private String idNumber;
    private String address;
    private String birthDate;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static List<Customer> customers = new ArrayList<>();

    public Customer(String customerId, String name, String phone, String idNumber, String address, String birthDate) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.idNumber = idNumber;
        this.address = address;
        this.birthDate = birthDate;
        validate();
    }

    public Customer() {}

    public void register() {
        validate();
        for (Customer existing : customers) {
            if (Validation.sameIdentifier(existing.customerId, customerId)) {
                throw new ValidationException("Ya existe un cliente con ese ID.");
            }
            if (Validation.sameIdentifier(existing.idNumber, idNumber)) {
                throw new ValidationException("Ya existe un cliente con esa cedula.");
            }
        }
        customers.add(this);
    }

    void validate() {
        Validation.requireText(customerId, "identificador del cliente");
        Validation.requireText(name, "nombre del cliente");
        Validation.requireText(phone, "telefono");
        
        // Permite cedulas numericas o IDs de prueba (DEMO-)
        if (!idNumber.matches("\\d+") && !idNumber.startsWith("DEMO-")) {
            throw new ValidationException("La cedula debe contener solo numeros.");
        }
        
        Validation.requireText(address, "direccion");
        
        if (birthDate != null && !birthDate.isBlank()) {
            parseBirthDate();
        }
    }

    private LocalDate parseBirthDate() {
        try {
            LocalDate date = LocalDate.parse(birthDate, DATE_FORMAT);
            if (date.isAfter(LocalDate.now())) {
                throw new ValidationException("La fecha de nacimiento no puede ser futura.");
            }
            return date;
        } catch (DateTimeParseException exception) {
            throw new ValidationException("La fecha de nacimiento debe tener el formato yyyy-MM-dd.");
        }
    }

    public int getAge() {
        if (birthDate == null || birthDate.isBlank()) return -1;
        try {
            return Period.between(parseBirthDate(), LocalDate.now()).getYears();
        } catch (ValidationException exception) {
            return -1;
        }
    }

    public static void setAll(List<Customer> newItems) {
        customers = newItems == null ? new ArrayList<>() : newItems;
    }

    public static Customer search(String searchTerm) {
        for (Customer c : customers) {
            if (Validation.sameIdentifier(c.getCustomerId(), searchTerm) || Validation.sameIdentifier(c.getIdNumber(), searchTerm)) {
                return c;
            }
        }
        return null;
    }

    public static List<Customer> getAll() {
        return customers;
    }

    public static void showManagementMenu(Scanner scanner) {
        boolean goBack = false;
        while (!goBack) {
            System.out.println("\n--- GESTION DE CLIENTES ---");
            System.out.println("1. Registrar nuevo Cliente");
            System.out.println("2. Buscar Cliente");
            System.out.println("3. Listar todos los Clientes");
            System.out.println("0. Volver al menu principal");
            int option = ConsoleInput.readOption(scanner, "Opcion: ", 0, 3);

            switch (option) {
                case 1 -> ConsoleInput.runAction(() -> registerFromConsole(scanner));
                case 2 -> ConsoleInput.runAction(() -> searchFromConsole(scanner));
                case 3 -> listFromConsole();
                case 0 -> goBack = true;
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private static void registerFromConsole(Scanner scanner) {
        System.out.println("\n--- REGISTRAR NUEVO CLIENTE ---");
        String id = ConsoleInput.readRequiredText(scanner, "ID del Cliente: ");

        if (search(id) != null) {
            System.out.println("Error: Ya existe un cliente con ese ID.");
            return;
        }

        String name = ConsoleInput.readRequiredText(scanner, "Nombre completo: ");
        String phone = ConsoleInput.readRequiredText(scanner, "Telefono: ");
        
        // Validacion instantanea
        String nationalId = ConsoleInput.readNumericText(scanner, "Cedula/DNI (solo numeros): ");
        
        String address = ConsoleInput.readRequiredText(scanner, "Direccion: ");
        String birthDate = readBirthDate(scanner);

        Customer newCustomer = new Customer(id, name, phone, nationalId, address, birthDate);
        newCustomer.register();
        System.out.println("¡Cliente registrado con exito! Edad calculada: " + newCustomer.getAge() + " años.");
    }

    private static String readBirthDate(Scanner scanner) {
        while (true) {
            String raw = ConsoleInput.readRequiredText(scanner, "Fecha de nacimiento (yyyy-MM-dd): ");
            try {
                LocalDate date = LocalDate.parse(raw.trim(), DATE_FORMAT);
                if (date.isAfter(LocalDate.now())) {
                    System.out.println("La fecha no puede ser futura. Intente de nuevo.");
                    continue;
                }
                return date.format(DATE_FORMAT);
            } catch (DateTimeParseException exception) {
                System.out.println("Formato invalido. Use yyyy-MM-dd (ejemplo: 1990-05-23).");
            }
        }
    }

    private static void searchFromConsole(Scanner scanner) {
        System.out.println("\n--- BUSCAR CLIENTE ---");
        String searchTerm = ConsoleInput.readRequiredText(scanner, "Ingrese el ID o la Cedula del cliente: ");
        Customer match = search(searchTerm);

        if (match != null) {
            System.out.println("\n--- CLIENTE ENCONTRADO ---");
            System.out.println("ID: " + match.getCustomerId());
            System.out.println("Nombre: " + match.getName());
            System.out.println("Telefono: " + match.getPhone());
            System.out.println("Cedula: " + match.getIdNumber());
            System.out.println("Direccion: " + match.getAddress());
            System.out.println("Fecha de nacimiento: " + formatBirthDate(match.getBirthDate()));
            System.out.println("Edad: " + formatAge(match.getAge()));
        } else {
            System.out.println("Cliente no encontrado.");
        }
    }

    private static void listFromConsole() {
        System.out.println("\n--- LISTA DE CLIENTES ---");
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados aun.");
        } else {
            for (Customer c : customers) {
                System.out.println("- [" + c.getCustomerId() + "] " + c.getName() + " | Tel: " + c.getPhone() + " | Edad: " + formatAge(c.getAge()));
            }
        }
    }

    private static String formatAge(int age) {
        return age < 0 ? "N/D" : age + " años";
    }

    private static String formatBirthDate(String birthDate) {
        return (birthDate == null || birthDate.isBlank()) ? "N/D" : birthDate;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getIdNumber() { return idNumber; }
    public String getAddress() { return address; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
}