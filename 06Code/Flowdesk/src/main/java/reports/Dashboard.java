package reports;

import enums.OrderStatus;
import models.WorkOrder;
import java.util.List;
import java.util.ArrayList;

public class Dashboard {
    
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
    }
}