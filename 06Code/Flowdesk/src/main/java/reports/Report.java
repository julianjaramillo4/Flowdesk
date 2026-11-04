package reports;

import enums.OrderStatus;
import enums.ReportType;
import models.CashRegister;
import models.WorkOrder;
import utils.ConsoleInput;
import java.util.Scanner;

public class Report {
    private String title;
    private String content;

    public Report() {
        this.title = "Reporte General Flowdesk";
        this.content = "";
    }

    public Report(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static void generateFromConsole(Scanner scanner, CashRegister cashRegister) {
        System.out.println("\n--- GENERACION DE REPORTES ---");
        System.out.println("1. Diario");
        System.out.println("2. Semanal");
        System.out.println("3. Mensual");
        System.out.println("0. Volver");

        int option = ConsoleInput.readOption(scanner, "Opcion: ", 0, 3);
        if (option == 0) return;

        ReportType type =
                switch (option) {
                    case 1 -> ReportType.DAILY;
                    case 2 -> ReportType.WEEKLY;
                    default -> ReportType.MONTHLY;
                };

        System.out.println("\n=== REPORTE " + type + " DE FLOWDESK ===");

        var orders = WorkOrder.getAll();
        System.out.println("Total de ordenes: " + orders.size());

        long completedCount =
                orders.stream()
                        .filter(
                                o ->
                                        o.getStatus() == OrderStatus.COMPLETED
                                                || o.getStatus() == OrderStatus.DELIVERED)
                        .count();
        long pendingCount =
                orders.stream()
                        .filter(
                                o ->
                                        o.getStatus() == OrderStatus.WAITING
                                                || o.getStatus() == OrderStatus.APPROVED
                                                || o.getStatus() == OrderStatus.IN_PROGRESS)
                        .count();

        System.out.println("Ordenes terminadas: " + completedCount);
        System.out.println("Ordenes pendientes: " + pendingCount);

        double outstanding = orders.stream().mapToDouble(WorkOrder::getBalance).sum();
        System.out.printf("Dinero por cobrar (Saldos): $%.1f\n", outstanding);

        if (cashRegister != null) {
            System.out.printf("Ingresos totales: $%.1f\n", cashRegister.calculateIncome());
            System.out.printf("Egresos totales: $%.1f\n", cashRegister.calculateExpenses());
            System.out.printf("Balance neto:     $%.1f\n", cashRegister.calculateBalance());
        }
        System.out.println("=======================================\n");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "=== " + title + " ===\n" + content;
    }
}