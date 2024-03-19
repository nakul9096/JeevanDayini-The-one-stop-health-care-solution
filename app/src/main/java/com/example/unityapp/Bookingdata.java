package com.example.unityapp;
public class Bookingdata {
    private String appointmentSlot;
    private String hospitalName;
    private String transactionId;
    private String tokenNumber;
    private String date;

    public Bookingdata() {
    }

    public Bookingdata(String appointmentSlot, String hospitalName, String transactionId, String tokenNumber, String date) {
        this.appointmentSlot = appointmentSlot;
        this.hospitalName = hospitalName;
        this.transactionId = transactionId;
        this.tokenNumber = tokenNumber;
        this.date = date;
    }

    public String getAppointmentSlot() {
        return appointmentSlot;
    }

    public void setAppointmentSlot(String appointmentSlot) {
        this.appointmentSlot = appointmentSlot;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
