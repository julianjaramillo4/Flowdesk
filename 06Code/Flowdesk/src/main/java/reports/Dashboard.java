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
    
    public List<WorkOrder> getOrders() {
        return this.orders;
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

        StringBuilder sb = new StringBuilder();
        sb.append("\n==========================================\n");
        sb.append("          DASHBOARD DE ÓRDENES            \n");
        sb.append("==========================================\n");
        sb.append(" Total de Órdenes Registradas: ").append(totalOrders).append("\n");
        sb.append(" ----------------------------------------\n");
        sb.append("  [WAITING]     En espera   : ").append(waitingCount).append("\n");
        sb.append("  [APPROVED]    Aprobadas   : ").append(approvedCount).append("\n");
        sb.append("  [IN_PROGRESS] En proceso  : ").append(inProgressCount).append("\n");
        sb.append("  [COMPLETED]   Completadas : ").append(completedCount).append("\n");
        sb.append("  [DELIVERED]   Entregadas  : ").append(deliveredCount).append("\n");
        sb.append(" ----------------------------------------\n");
        sb.append("          DETALLE DE ÓRDENES             \n");
        sb.append(" ----------------------------------------\n");
        
        for (WorkOrder order : orders) {
            sb.append(order.toString()).append("\n");
        }
        
        sb.append("==========================================");
        return sb.toString();
    }
}