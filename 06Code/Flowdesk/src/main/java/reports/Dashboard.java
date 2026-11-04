package reports;

import enums.OrderStatus;
import models.WorkOrder;
import java.util.List;
import java.util.ArrayList;

public class Dashboard {
<<<<<<< HEAD
    
    private List<WorkOrder> orders;

    public Dashboard() {
        this.orders = new ArrayList<>();
    }

    public void setOrders(List<WorkOrder> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        if (orders == null || orders.isEmpty()) {
            return "No hay ordenes de trabajo registradas.";
        }

        int waitingCount = 0;
        int approvedCount = 0;
        int inProgressCount = 0;
        int completedCount = 0;
        int deliveredCount = 0;

        for (WorkOrder order : orders) {
            if (order.getStatus() != null) {
                switch (order.getStatus()) {
                    case WAITING -> waitingCount++;
                    case APPROVED -> approvedCount++;
                    case IN_PROGRESS -> inProgressCount++;
                    case COMPLETED -> completedCount++;
                    case DELIVERED -> deliveredCount++;
                }
            }
        }

        int totalOrders = orders.size();

        return "\n==========================================\n" +
               "          DASHBOARD DE ÓRDENES            \n" +
               "==========================================\n" +
               " Total de Órdenes Registradas: " + totalOrders + "\n" +
               " ----------------------------------------\n" +
               "  [WAITING]     En espera   : " + waitingCount + "\n" +
               "  [APPROVED]    Aprobadas   : " + approvedCount + "\n" +
               "  [IN_PROGRESS] En proceso  : " + inProgressCount + "\n" +
               "  [COMPLETED]   Completadas : " + completedCount + "\n" +
               "  [DELIVERED]   Entregadas  : " + deliveredCount + "\n" +
               "==========================================";
=======
    private List<WorkOrder> reportOrders;

    public Dashboard() {
        reportOrders = null;
    }

    public List<WorkOrder> getOrders() {
        return reportOrders;
    }

    public void setOrders(List<WorkOrder> orders) {
        this.reportOrders = orders;
    }

    public void showDashboard() {
        System.out.println("\n=============================================");
        System.out.println("       DASHBOARD DE ORDENES (US003)          ");
        System.out.println("=============================================");

        if (reportOrders == null || reportOrders.isEmpty()) {
            System.out.println("La lista de ordenes esta vacia.");
            System.out.println("=============================================\n");
            return;
        }

        System.out.println("Total de ordenes en el sistema: " + reportOrders.size());

        int[] counts = new int[5];
        for (WorkOrder o : reportOrders) {
            if (o.getStatus() != null) {
                counts[o.getStatus().ordinal()]++;
            }
        }
        System.out.println("En espera: " + counts[OrderStatus.WAITING.ordinal()] + " orden(es)");
        System.out.println("Aprobada: " + counts[OrderStatus.APPROVED.ordinal()] + " orden(es)");
        System.out.println(
                "En proceso: " + counts[OrderStatus.IN_PROGRESS.ordinal()] + " orden(es)");
        System.out.println("Completada: " + counts[OrderStatus.COMPLETED.ordinal()] + " orden(es)");
        System.out.println("Entregada: " + counts[OrderStatus.DELIVERED.ordinal()] + " orden(es)");

        System.out.println("\n--- ORDENES PENDIENTES ---");
        for (WorkOrder o : reportOrders) {
            String customerName =
                    o.getCustomer() != null ? o.getCustomer().getName() : "Sin cliente";
            System.out.println(
                    "["
                            + o.getOrderId()
                            + "] Cliente: "
                            + customerName
                            + " | Estado: "
                            + o.getStatus()
                            + " | "
                            + o.getProgressBar());
        }

        System.out.println("=============================================\n");
>>>>>>> 6eafda688a8232aa3874123faeb60417b132a3b0
    }
}