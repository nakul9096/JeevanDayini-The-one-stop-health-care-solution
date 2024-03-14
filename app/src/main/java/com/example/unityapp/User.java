package com.example.unityapp;

public class User {
    private String name;
    private String phone;
    private String email;
    private String invoiceNumber;
    private String dueBalance;
    private String address;
    private String productName;
    private String transactionId;
    private String tokenNumber;
    private String baseAmount;
    private String gstAmount;
    private String totalAmount;
    private String description;
    private String platformfee;

    public User(String name, String phone, String email, String invoiceNumber, String dueBalance, String address, String productName, String transactionId, String tokenNumber, String baseAmount, String gstAmount, String totalAmount, String description, String platformfee) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.invoiceNumber = invoiceNumber;
        this.dueBalance = dueBalance;
        this.address = address;
        this.productName = productName;
        this.transactionId = transactionId;
        this.tokenNumber = tokenNumber;
        this.baseAmount = baseAmount;
        this.gstAmount = gstAmount;
        this.totalAmount = totalAmount;
        this.description = description;
        this.platformfee = platformfee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPlatformfee() {
        return platformfee;
    }

    public void setPlatformfee(String platformfee) {
        this.platformfee = platformfee;
    }

    public String getDueBalance() {
        return dueBalance;
    }

    public void setDueBalance(String dueBalance) {
        this.dueBalance = dueBalance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(String baseAmount) {
        this.baseAmount = baseAmount;
    }

    public String getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(String gstAmount) {
        this.gstAmount = gstAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}