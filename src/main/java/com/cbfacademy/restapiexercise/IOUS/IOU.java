package com.cbfacademy.restapiexercise.IOUS;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // IOU class is marked as an entitiy that will be stored in the database
@Table(name = "ious") // Table name ious
public class IOU {
    
    @Id // Marks the id field as the primary key of the table
    @GeneratedValue(strategy = GenerationType.AUTO) // Tells the table to automatically generate a unique value for the id field whenever a new IOU is added. GenerationType.AUTO will handle generating the ID
    private UUID id;

    private String borrower;
    private String lender; 
    private BigDecimal amount;
    private Instant dateTime;

    public IOU() {
    }

    public IOU (String borrower, String lender, BigDecimal amount, Instant dateTime) {
        this.borrower = borrower;
        this.lender = lender;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    public UUID getId () {
        return id;
    }

    public void setId(UUID id) {
        this.id = id; 
    }
    
    public String getBorrower () {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public String getLender() {
        return lender;
    }

    public void setLender(String lender) {
        this.lender = lender;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }
}
