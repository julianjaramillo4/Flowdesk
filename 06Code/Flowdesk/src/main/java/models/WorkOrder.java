package models;

import enums.OrderStatus;
import utils.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import utils.ConsoleInput;
import utils.Validation;
import exceptions.ValidationException;
import exceptions.InvalidPaymentException;

public class WorkOrder {

    private String orderId;
    private Date date;
    private double total;
    private double balance;
    private OrderStatus status;
    private Customer customer;
    private List<OrderDetail> details;
    private List<Payment> payments;
    private User user;

    private static List<WorkOrder> orders = new ArrayList<>();
    private static int orderCounter = 1;

    public WorkOrder() {
        this.details = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    public WorkOrder(String orderId, Date date, OrderStatus status) {
        this.orderId = orderId;
        this.date = date;
        this.status = status;
        this.details = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.total = 0;
        this.balance = 0;
    }

    public static List<WorkOrder> getAll() { 
        return orders; 
    }

    public static void setAll(List<WorkOrder> newItems) {
        orders = newItems == null ? new ArrayList<>() : newItems;
        int maxId = 0;
        for (WorkOrder wo : orders) {
            try {
                if (wo.orderId != null && wo.orderId.startsWith("WO-")) {
                    int num = Integer.parseInt(wo.orderId.substring(3));
                    if (num > maxId) maxId = num;
                }
            } catch (NumberFormatException ignored) {}
        }
        orderCounter = maxId + 1;
    }

    public void create() {
        Validation.requireText(orderId, "identificador de la orden");
        Validation.requireValue(date, "fecha");
        Validation.requireValue(status, "estado");
        if (details == null || details.isEmpty()) {
            throw new ValidationException("La orden debe tener al menos un detalle.");
        }
        
        for (WorkOrder existing : orders) {
            if (Validation.sameIdentifier(existing.orderId, orderId)) {
                throw new ValidationException("Ya existe una orden con ese ID.");
            }
        }
        
        ArrayList<String> detailIds = new ArrayList<>();
        for (OrderDetail detail : details) {
            Validation.requireValue(detail, "detalle");
            detail.validate();
            for (String existingId : detailIds) {
                if (Validation.sameIdentifier(existingId, detail.getDetailId())) {
                    throw new ValidationException("La orden contiene detalles con el mismo ID.");
                }
            }
            detailIds.add(detail.getDetailId());
        }
        calculateTotals();
        orders.add(this);
    }

    public void addPayment(Payment payment) {
        if (payment == null) {
            throw new InvalidPaymentException("El pago no puede estar vacio.");
        }
        if (payment.getWorkOrder() == null) {
            payment.setWorkOrder(this);
        }
        calculateTotals();
        payment.validateFor(this);
        payments.add(payment);
        payment.markRegistered();
        calculateTotals();
    }

    public double calculateTotalPaid() {
        double totalPaid = 0;
        for (Payment payment : payments) {
            totalPaid += payment.getAmount();
        }
        return totalPaid;
    }

    public void calculateTotals() {
        double calculatedTotal = 0;
        for (OrderDetail detail : details) {
            Validation.requireValue(detail, "detalle");
            calculatedTotal += detail.calculateSubtotal();
        }
        Validation.requireNonNegativeAmount(calculatedTotal, "total de la orden");
        double paid = calculateTotalPaid();
        Validation.requireNonNegativeAmount(paid, "total pagado");
        double calculatedBalance = calculatedTotal - paid;
        Validation.requireNonNegativeAmount(calculatedBalance, "saldo de la orden");
        this.total = calculatedTotal;
        this.balance = calculatedBalance;
    }

    public void changeStatus(OrderStatus newStatus) {
        Validation.requireValue(newStatus, "estado");
        this.status = newStatus;
    }

    public int calculateProgressPercentage() {
        if (details == null || details.isEmpty()) return 0;
        int completedCount = 0;
        for (OrderDetail detail : details) {
            if (detail != null && detail.isCompleted()) {
                completedCount++;
            }
        }
        return (int) Math.round((completedCount * 100.0) / details.size());
    }

    public int getCompletedDetailCount() {
        if (details == null) return 0;
        int count = 0;
        for (OrderDetail detail : details) {
            if (detail != null && detail.isCompleted()) count++;
        }
        return count;
    }

    public int getTotalDetailCount() { 
        return details == null ? 0 : details.size(); 
    }

    public String getProgressBar() {
        int percent = calculateProgressPercentage();
        int filled = percent / 10;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            bar.append(i < filled ? '#' : '-');
        }
        bar.append("] ").append(percent).append("% (").append(getCompletedDetailCount()).append("/").append(getTotalDetailCount()).append(")");
        return bar.toString();
    }

    public static void registerFromConsole(Scanner scanner, User employee) {
        System.out.println("\n--- CREAR NUEVA ORDEN DE TRABAJO ---");
        String newId = String.format("WO-%03d", orderCounter++);
        System.out.println("Numero de Orden generado: " + newId);

        String searchTerm = ConsoleInput.readRequiredText(scanner, "Ingrese ID o Cedula del Cliente: ");
        Customer customer = Customer.search(searchTerm);

        if (customer == null) {
            System.out.println("Error: Cliente no encontrado.");
            return;
        }

        System.out.println("Cliente asignado: " + customer.getName());
        WorkOrder newOrder = new WorkOrder(newId, Date.now(), OrderStatus.WAITING);
        newOrder.customer = customer;
        newOrder.user = employee;

        int detailCounter = 1;
        while (true) {
            System.out.println("\n1. Agregar Producto");
            System.out.println("2. Agregar Servicio");
            System.out.println("0. Terminar y guardar Orden");
            int option = ConsoleInput.readOption(scanner, "Opcion: ", 0, 2);

            if (option == 0) break;
            OrderDetail detail = option == 1 ? readProductDetail(scanner, "D" + detailCounter) : readServiceDetail(scanner, "D" + detailCounter);

            if (detail != null) {
                newOrder.details.add(detail);
                detailCounter++;
                System.out.println("Elemento agregado. Subtotal: $" + detail.calculateSubtotal());
            } else {
                System.out.println("Elemento no valido.");
            }
        }

        newOrder.calculateTotals();
        newOrder.create();
        System.out.println("\n¡Orden " + newOrder.orderId + " creada exitosamente!");
        System.out.println("Total a pagar: $" + newOrder.total);
    }

    private static OrderDetail readProductDetail(Scanner scanner, String detailId) {
        if (Product.getAll().isEmpty()) { 
            System.out.println("No hay productos."); 
            return null; 
        }
        Product.listFromConsole();
        String productId = ConsoleInput.readRequiredText(scanner, "Ingrese ID del Producto: ");
        for (Product product : Product.getAll()) {
            if (Validation.sameIdentifier(product.getId(), productId)) {
                int quantity = ConsoleInput.readPositiveInt(scanner, "Cantidad: ");
                OrderDetail detail = new OrderDetail(detailId, product.getName(), quantity, product.getDescription(), product.getPrice());
                detail.setProduct(product);
                return detail;
            }
        }
        return null;
    }

    private static OrderDetail readServiceDetail(Scanner scanner, String detailId) {
        if (Service.getAll().isEmpty()) { 
            System.out.println("No hay servicios."); 
            return null; 
        }
        Service.listFromConsole();
        String serviceId = ConsoleInput.readRequiredText(scanner, "Ingrese ID del Servicio: ");
        for (Service service : Service.getAll()) {
            if (Validation.sameIdentifier(service.getId(), serviceId)) {
                int quantity = ConsoleInput.readPositiveInt(scanner, "Cantidad (Horas/Veces): ");
                double price = ConsoleInput.readAmount(scanner, "Precio Unitario a cobrar: $");
                OrderDetail detail = new OrderDetail(detailId, service.getName(), quantity, service.getDescription(), price);
                detail.setService(service);
                return detail;
            }
        }
        return null;
    }

    public static void listFromConsole() {
        System.out.println("\n--- HISTORIAL DE ORDENES ---");
        if (orders.isEmpty()) {
            System.out.println("No hay ordenes registradas.");
        } else {
            for (WorkOrder wo : orders) {
                String customerName = wo.customer == null ? "Sin cliente" : wo.customer.getName();
                System.out.println("- [" + wo.orderId + "] Cliente: " + customerName + " | Estado: " + wo.status + " | Progreso: " + wo.getProgressBar());
            }
        }
    }

    public static void listOrdersByPaymentStatus() {
        System.out.println("\n==========================================");
        System.out.println("      ESTADO DE PAGOS DE ÓRDENES          ");
        System.out.println("==========================================");
        if (orders == null || orders.isEmpty()) {
            System.out.println("No hay ordenes registradas.");
            System.out.println("==========================================\n");
            return;
        }

        System.out.println("--- ÓRDENES PENDIENTES DE PAGO ---");
        boolean pendingFound = false;
        for (WorkOrder wo : orders) {
            if (wo.getBalance() > 0) {
                String customerName = wo.getCustomer() == null ? "Sin cliente" : wo.getCustomer().getName();
                System.out.println("  - [" + wo.getOrderId() + "] Cliente: " + customerName +
                                   " | Total: $" + wo.getTotal() +
                                   " | Pagado: $" + wo.calculateTotalPaid() +
                                   " | Pendiente: $" + wo.getBalance());
                pendingFound = true;
            }
        }
        if (!pendingFound) {
            System.out.println("  (No hay ordenes pendientes de pago)");
        }

        System.out.println("\n--- ÓRDENES COMPLETAMENTE PAGADAS ---");
        boolean paidFound = false;
        for (WorkOrder wo : orders) {
            if (wo.getBalance() <= 0) {
                String customerName = wo.getCustomer() == null ? "Sin cliente" : wo.getCustomer().getName();
                System.out.println("  - [" + wo.getOrderId() + "] Cliente: " + customerName +
                                   " | Total: $" + wo.getTotal() +
                                   " | Pagado: $" + wo.calculateTotalPaid() +
                                   " | Estado: COMPLETADO");
                paidFound = true;
            }
        }
        System.out.println("==========================================\n");
    }

    public static void updateStatusFromConsole(Scanner scanner) {
        System.out.println("\n--- ACTUALIZAR ORDEN ---");
        if (orders.isEmpty()) {
            System.out.println("No hay ordenes registradas para actualizar.");
            return;
        }

        listFromConsole();
        System.out.println("--------------------------------------------------");

        String searchId = ConsoleInput.readRequiredText(scanner, "Ingrese el ID de la orden a modificar (ej. WO-001): ");
        WorkOrder order = null;
        for (WorkOrder wo : orders) {
            if (Validation.sameIdentifier(wo.getOrderId(), searchId)) {
                order = wo;
                break;
            }
        }

        if (order == null) {
            System.out.println("Error: Orden no encontrada.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\nOrden: " + order.getOrderId() + " | Estado: " + order.getStatus() + " | Progreso: " + order.getProgressBar());
            System.out.println("\n1. Ver y marcar detalles");
            System.out.println("2. Cambiar estado");
            System.out.println("0. Volver");
            int option = ConsoleInput.readOption(scanner, "Opcion: ", 0, 2);

            switch (option) {
                case 1 -> toggleDetails(scanner, order);
                case 2 -> changeStatusInteractive(scanner, order);
                case 0 -> back = true;
            }
        }
    }

    private static void toggleDetails(Scanner scanner, WorkOrder order) {
        if (order.details == null || order.details.isEmpty()) { 
            System.out.println("La orden no tiene detalles."); 
            return; 
        }
        System.out.println("\n--- DETALLES ---");
        for (int i = 0; i < order.details.size(); i++) {
            OrderDetail d = order.details.get(i);
            System.out.println((i + 1) + ". " + (d.isCompleted() ? "[X] " : "[ ] ") + d.getItemName() + " (x" + d.getQuantity() + ")");
        }
        System.out.println("0. Volver");
        int pick = ConsoleInput.readOption(scanner, "Seleccione detalle a alternar: ", 0, order.details.size());
        if (pick == 0) return;

        OrderDetail selected = order.details.get(pick - 1);
        selected.setCompleted(!selected.isCompleted());
        
        System.out.println("Detalle '" + selected.getItemName() + "' marcado como " + (selected.isCompleted() ? "COMPLETADO" : "PENDIENTE") + ".");
        System.out.println("Progreso actual: " + order.getProgressBar());

        syncStatusWithProgress(order);
    }

    private static void syncStatusWithProgress(WorkOrder order) {
        int p = order.calculateProgressPercentage();
        OrderStatus current = order.getStatus();
        if (p == 0 && current == OrderStatus.WAITING) return;
        
        if (p == 100 && current != OrderStatus.DELIVERED) {
            order.changeStatus(OrderStatus.COMPLETED);
            System.out.println("-> Estado ajustado automaticamente a: " + OrderStatus.COMPLETED);
        } else if (p > 0 && p < 100 && current != OrderStatus.DELIVERED && current != OrderStatus.IN_PROGRESS) {
            order.changeStatus(OrderStatus.IN_PROGRESS);
            System.out.println("-> Estado ajustado automaticamente a: " + OrderStatus.IN_PROGRESS);
        }
    }

    private static void changeStatusInteractive(Scanner scanner, WorkOrder order) {
        System.out.println("\nSeleccione el nuevo estado:");
        System.out.println("1. En espera");
        System.out.println("2. Aprobada");
        System.out.println("3. En proceso");
        System.out.println("4. Completada");
        System.out.println("5. Entregada");
        System.out.println("0. Volver");
        int option = ConsoleInput.readOption(scanner, "Opcion: ", 0, 5);
        if (option == 0) return;

        OrderStatus newStatus = switch (option) {
            case 1 -> OrderStatus.WAITING;
            case 2 -> OrderStatus.APPROVED;
            case 3 -> OrderStatus.IN_PROGRESS;
            case 4 -> OrderStatus.COMPLETED;
            case 5 -> OrderStatus.DELIVERED;
            default -> null;
        };
        if (newStatus == null) return;

        order.changeStatus(newStatus);
        System.out.println("¡Estado actualizado a: " + order.getStatus() + "!");
        System.out.println("Progreso calculado: " + order.getProgressBar());
    }

    public String getOrderId() { return orderId; }
    public Date getDate() { return date; }
    public double getTotal() { return total; }
    public double getBalance() { return balance; }
    public OrderStatus getStatus() { return status; }
    public Customer getCustomer() { return customer; }
    public List<OrderDetail> getDetails() { return details; }
    public List<Payment> getPayments() { return payments; }
}