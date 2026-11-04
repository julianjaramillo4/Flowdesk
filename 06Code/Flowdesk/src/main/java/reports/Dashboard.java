package reports;

import enums.OrderStatus;
import models.WorkOrder;
import java.util.List;

public class Dashboard {
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
    }
}