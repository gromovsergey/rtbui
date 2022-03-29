package app.programmatic.ui.kickcaptcha.model;

import java.math.BigDecimal;

public class Payments {
    private BigDecimal balance;
    private BigDecimal paidAmount;
    private BigDecimal spentAmount;

    public BigDecimal getBalance() {
        return balance;
    }

    public Payments setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public Payments setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
        return this;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public Payments setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
        return this;
    }

    @Override
    public String toString() {
        return "Payments{" +
                "balance=" + balance +
                ", paidAmount=" + paidAmount +
                ", spentAmount=" + spentAmount +
                '}';
    }
}
