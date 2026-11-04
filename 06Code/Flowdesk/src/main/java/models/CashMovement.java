package models;

import enums.MovementType;
import enums.PaymentMethod;
import utils.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import utils.ConsoleInput;
import utils.Validation;
import exceptions.ValidationException;

public class CashMovement {
    private String movementId;
    private MovementType type;
    private double amount;
    private Date date;
    private String reason;
    private PaymentMethod paymentMethod;

    private static List<CashMovement> movements = new ArrayList<>();

    public CashMovement() {}

    public CashMovement(String movementId, MovementType type, double amount, Date date, String reason, PaymentMethod paymentMethod) {
        this.movementId = movementId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.reason = reason;
        this.paymentMethod = paymentMethod;
        validate();
    }

    public void registerMovement(CashRegister cashRegister) {
        validateRegistration(cashRegister);
        if (this.type == MovementType.INCOME) {
            cashRegister.addIncome(this.amount);
        } else {
            cashRegister.addExpense(this.amount);
        }
        cashRegister.addMovement(this);
        movements.add(this);
    }

    void validateRegistration(CashRegister cashRegister) {
        validate();
        Validation.requireValue(cashRegister, "caja");
        for (CashMovement existing : movements) {
            if (Validation.sameIdentifier(existing.movementId, movementId)) {
                throw new ValidationException("Ya existe un movimiento con ese ID.");
            }
        }
        double accumulatedAmount = type == MovementType.INCOME ? cashRegister.calculateIncome() + amount : cashRegister.calculateExpenses() + amount;
        Validation.requireNonNegativeAmount(accumulatedAmount, "monto acumulado de caja");
    }

    private void validate() {
        Validation.requireText(movementId, "identificador del movimiento");
        Validation.requireText(reason, "motivo");
        Validation.requirePositiveAmount(amount, "monto");
        Validation.requireValue(type, "tipo de movimiento");
        Validation.requireValue(date, "fecha");
        Validation.requireValue(paymentMethod, "metodo de pago");
    }

    public static List<CashMovement> getAll() { return movements; }

    public static void setAll(List<CashMovement> newItems) {
        movements = newItems == null ? new ArrayList<>() : newItems;
    }

    public static void registerFromConsole(Scanner scanner, CashRegister cashRegister) {
        System.out.println("\n--- REGISTRAR MOVIMIENTO DE CAJA ---");

        String id = ConsoleInput.readRequiredText(scanner, "ID del Movimiento: ");
        int typeOption = ConsoleInput.readOption(scanner, "Tipo (1. INGRESO / 2. EGRESO): ", 1, 2);
        MovementType type = (typeOption == 1) ? MovementType.INCOME : MovementType.EXPENSE;
        
        double finalAmount = 0.0;
        StringBuilder reasonBuilder = new StringBuilder();

        boolean keepAdding = true;
        while (keepAdding) {
            System.out.println("\n--- AGREGAR AL MOVIMIENTO (Total actual: $" + finalAmount + ") ---");
            System.out.println("1. Agregar Producto");
            System.out.println("2. Agregar Servicio");
            System.out.println("3. Agregar Otro (Monto y Descripcion manual)");
            System.out.println("4. Terminar y guardar movimiento");
            int opt = ConsoleInput.readOption(scanner, "Opcion: ", 1, 4);

            switch (opt) {
                case 1 -> {
                    if (Product.getAll().isEmpty()) {
                        System.out.println("No hay productos registrados en el sistema.");
                        break;
                    }
                    Product.listFromConsole();
                    String prodId = ConsoleInput.readRequiredText(scanner, "Ingrese ID del Producto: ");
                    Product p = null;
                    for (Product prod : Product.getAll()) {
                        if (Validation.sameIdentifier(prod.getId(), prodId)) {
                            p = prod;
                            break;
                        }
                    }
                    if (p != null) {
                        int qty = ConsoleInput.readPositiveInt(scanner, "Cantidad: ");
                        finalAmount += (p.getPrice() * qty);
                        reasonBuilder.append("\n  - ").append(qty).append("x ").append(p.getName()).append(" ($").append(p.getPrice()).append(" c/u)");
                        System.out.println("Producto agregado.");
                    } else {
                        System.out.println("Producto no encontrado.");
                    }
                }
                case 2 -> {
                    if (Service.getAll().isEmpty()) {
                        System.out.println("No hay servicios registrados en el sistema.");
                        break;
                    }
                    Service.listFromConsole();
                    String servId = ConsoleInput.readRequiredText(scanner, "Ingrese ID del Servicio: ");
                    Service s = null;
                    for (Service serv : Service.getAll()) {
                        if (Validation.sameIdentifier(serv.getId(), servId)) {
                            s = serv;
                            break;
                        }
                    }
                    if (s != null) {
                        int qty = ConsoleInput.readPositiveInt(scanner, "Cantidad (Horas/Veces): ");
                        double price = ConsoleInput.readAmount(scanner, "Precio Unitario a cobrar ($" + s.getPrice() + " sugerido): $");
                        finalAmount += (price * qty);
                        reasonBuilder.append("\n  - ").append(qty).append("x ").append(s.getName()).append(" ($").append(price).append(" c/u)");
                        System.out.println("Servicio agregado.");
                    } else {
                        System.out.println("Servicio no encontrado.");
                    }
                }
                case 3 -> {
                    double monto = ConsoleInput.readPositiveAmount(scanner, "Monto manual: $");
                    String details = ConsoleInput.readRequiredText(scanner, "Descripcion del movimiento: ");
                    finalAmount += monto;
                    reasonBuilder.append("\n  - Otro: ").append(details).append(" ($").append(monto).append(")");
                    System.out.println("Monto manual agregado.");
                }
                case 4 -> {
                    if (finalAmount <= 0) {
                        System.out.println("El monto total no puede ser 0. Agregue al menos un item o cancele.");
                        break;
                    }
                    keepAdding = false;
                }
            }
        }

        System.out.println("\n--- RESUMEN DEL MOVIMIENTO ---");
        System.out.println("Total a registrar: $" + finalAmount);
        System.out.println("Desglose:" + reasonBuilder.toString());

        int methodOption = ConsoleInput.readOption(scanner, "\nMetodo de pago (1. EFECTIVO / 2. TRANSFERENCIA): ", 1, 2);
        PaymentMethod method = (methodOption == 1) ? PaymentMethod.CASH : PaymentMethod.TRANSFER;

        CashMovement newMovement = new CashMovement(id, type, finalAmount, Date.now(), reasonBuilder.toString().trim(), method);
        newMovement.registerMovement(cashRegister);

        System.out.println("¡Movimiento registrado exitosamente en la caja!");
    }

    public static void listFromConsole() {
        System.out.println("\n--- HISTORIAL DE MOVIMIENTOS ---");
        if (movements.isEmpty()) {
            System.out.println("No hay movimientos registrados.");
        } else {
            for (CashMovement m : movements) {
                String sign = (m.type == MovementType.INCOME) ? "+" : "-";
                System.out.println("- [" + m.movementId + "] " + m.date.toString() + " | " + m.type + " | " + sign + "$" + m.amount + " (" + m.paymentMethod + ")");
                System.out.println("  Motivo: " + m.reason.replace("\n", "\n  "));
            }
        }
    }

    public String getMovementId() { return movementId; }
    public double getAmount() { return amount; }
    public MovementType getType() { return type; }
    public Date getDate() { return date; }
    public String getReason() { return reason; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
}