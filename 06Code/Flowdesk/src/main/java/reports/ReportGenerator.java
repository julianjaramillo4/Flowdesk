package reports;

import models.CashMovement;
import models.WorkOrder;

import java.util.List;

public class ReportGenerator {

    public static void generateGeneralReport(List<WorkOrder> orders, List<CashMovement> movements) {
        String fileOrders = CsvExporter.exportOrdersToCsv(orders);
        String fileMovements = CsvExporter.exportMovementsToCsv(movements);

        System.out.println("\n==================================================");
        System.out.println("          EXPORTACION A CSV COMPLETADA            ");
        System.out.println("==================================================");
        if (fileOrders != null) {
            System.out.println("-> Reporte de Ordenes guardado en: " + fileOrders);
        }
        if (fileMovements != null) {
            System.out.println("-> Reporte de Movimientos guardado en: " + fileMovements);
        }
        System.out.println("==================================================\n");
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
