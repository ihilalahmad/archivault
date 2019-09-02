package com.example.archivault.model;

public class ProjectsSummaryModel {

    private String total_balance;
    private String incomes;
    private String expenses;


    public ProjectsSummaryModel(String total_balance, String incomes, String expenses) {
        this.total_balance = total_balance;
        this.incomes = incomes;
        this.expenses = expenses;
    }

    public String getTotal_balance() {
        return total_balance;
    }

    public String getIncomes() {
        return incomes;
    }

    public String getExpenses() {
        return expenses;
    }
}
