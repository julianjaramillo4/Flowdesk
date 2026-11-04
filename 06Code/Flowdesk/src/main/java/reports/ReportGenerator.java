package reports;

import enums.ReportType;
import models.CashMovement;
import models.WorkOrder;
import utils.Date;

import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {

    public static void generateGeneralReport(List<WorkOrder> orders, List<CashMovement> movements) {
        System.out.println("\n==================================================");
        System.out.println("          GENERANDO REPORTE GENERAL               ");
        System.out.println("==================================================");
        String fileOrders = CsvExporter.exportOrdersToCsv(orders);
        String fileMovements = CsvExporter.exportMovementsToCsv(movements);

        if (fileOrders != null) {
            System.out.println("-> Reporte de Ordenes guardado en: " + fileOrders);
        }
        if (fileMovements != null) {
            System.out.println("-> Reporte de Movimientos guardado en: " + fileMovements);
        }
        System.out.println("==================================================\n");
    }

    public static void generateFilteredReport(List<WorkOrder> orders, List<CashMovement> movements, ReportType type) {
        List<WorkOrder> filteredOrders = new ArrayList<>();
        List<CashMovement> filteredMovements = new ArrayList<>();
        
        Date currentDate = Date.now();

        for (WorkOrder order : orders) {
            if (order.getDate() != null) {
                long daysDiff = Math.abs(currentDate.daysTo(order.getDate()));
                if (isWithinRange(daysDiff, type)) {
                    filteredOrders.add(order);
                }
            }
        }

        for (CashMovement movement : movements) {
            if (movement.getDate() != null) {
                long daysDiff = Math.abs(currentDate.daysTo(movement.getDate()));
                if (isWithinRange(daysDiff, type)) {
                    filteredMovements.add(movement);
                }
            }
        }

        System.out.println("\n==================================================");
        System.out.println("          GENERANDO REPORTE " + type.toString().toUpperCase());
        System.out.println("==================================================");
        
        if (filteredOrders.isEmpty() && filteredMovements.isEmpty()) {
            System.out.println("No hay datos para el periodo seleccionado.");
            System.out.println("==================================================\n");
            return;
        }

        String fileOrders = CsvExporter.exportOrdersToCsv(filteredOrders);
        String fileMovements = CsvExporter.exportMovementsToCsv(filteredMovements);

        if (fileOrders != null) {
            System.out.println("-> Reporte de Ordenes guardado en: " + fileOrders);
        }
        if (fileMovements != null) {
            System.out.println("-> Reporte de Movimientos guardado en: " + fileMovements);
        }
        System.out.println("==================================================\n");
    }

    private static boolean isWithinRange(long daysDiff, ReportType type) {
        if (type == null) return true;
        return switch (type) {
            case DAILY -> daysDiff == 0;
            case WEEKLY -> daysDiff <= 7;
            case MONTHLY -> daysDiff <= 30;
        };
    }

    public static void exportOrdersCsv(List<WorkOrder> orders) {
        String file = CsvExporter.exportOrdersToCsv(orders);
        if (file != null) {
            System.out.println("-> Archivo de ordenes exportado en: " + file);
        }
    }

    public static void exportMovementsCsv(List<CashMovement> movements) {
        String file = CsvExporter.exportMovementsToCsv(movements);
        if (file != null) {
            System.out.println("-> Archivo de movimientos exportado en: " + file);
        }
    }
}