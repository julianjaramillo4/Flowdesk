package models;

import utils.Validation;
import java.util.ArrayList;
import java.util.List;

public class CashRegister {
    private double income;
    private double expenses;
    private List<CashMovement> movements;

    public CashRegister() {
        this.income = 0;
        this.expenses = 0;
        this.movements = new ArrayList<>();
    }

    public CashRegister(double income, double expenses) {
        Validation.requireNonNegativeAmount(income, "ingresos");
        Validation.requireNonNegativeAmount(expenses, "egresos");
        this.income = income;
        this.expenses = expenses;
        this.movements = new ArrayList<>();
    }

    public void addIncome(double amount) {
        Validation.requirePositiveAmount(amount, "monto");
        Validation.requireNonNegativeAmount(income + amount, "ingresos acumulados");
        this.income = roundToTwoDecimals(this.income + amount);
    }

    public void addExpense(double amount) {
        Validation.requirePositiveAmount(amount, "monto");
        Validation.requireNonNegativeAmount(expenses + amount, "egresos acumulados");
        this.expenses = roundToTwoDecimals(this.expenses + amount);
    }

    public void addMovement(CashMovement movement) {
        if (movement != null) {
            this.movements.add(movement);
        }
    }

    public double calculateIncome() {
        return income;
    }

    public double calculateExpenses() {
        return expenses;
    }

    public double calculateBalance() {
        return roundToTwoDecimals(income - expenses);
    }

    public void printStatus() {
        System.out.println("\n--- ESTADO DE CAJA ---");
        System.out.printf("Ingresos Totales: $%.2f\n", calculateIncome());
        System.out.printf("Egresos Totales:  $%.2f\n", calculateExpenses());
        System.out.printf("Balance Actual:   $%.2f\n", calculateBalance());
        System.out.println("----------------------");
    }

    public List<CashMovement> getMovements() {
        return movements;
    }

    public void setMovements(List<CashMovement> movements) {
        this.movements = movements == null ? new ArrayList<>() : movements;
    }

    public double getIncome() { return income; }
    
    public void setIncome(double income) {
        Validation.requireNonNegativeAmount(income, "ingresos");
        this.income = roundToTwoDecimals(income);
    }

    public double getExpenses() { return expenses; }
    
    public void setExpenses(double expenses) {
        Validation.requireNonNegativeAmount(expenses, "egresos");
        this.expenses = roundToTwoDecimals(expenses);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}