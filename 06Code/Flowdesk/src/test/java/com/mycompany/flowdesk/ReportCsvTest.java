package com.mycompany.flowdesk;

import org.junit.jupiter.api.Test;
import reports.CsvExporter;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportCsvTest {

    @Test
    void testEscapeCsvCommas() {
        String input = "Cliente, Juan";
        String expected = "\"Cliente, Juan\"";
        String result = CsvExporter.escapeCsvField(input);
        assertEquals(expected, result, "Error en escape con comas");
    }

    @Test
    void testEscapeCsvQuotes() {
        String input = "Pago \"Efectivo\"";
        String expected = "\"Pago \"\"Efectivo\"\"\"";
        String result = CsvExporter.escapeCsvField(input);
        assertEquals(expected, result, "Error en escape con comillas");
    }

    @Test
    void testEscapeCsvNormalText() {
        String input = "Orden123";
        String expected = "Orden123";
        String result = CsvExporter.escapeCsvField(input);
        assertEquals(expected, result, "Error en texto normal");
    }
}