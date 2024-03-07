package com.example.unityapp;

import java.util.ArrayList;
import java.util.List;

public class BloodDonation {
    private String firstName;
    private String relationType;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String address;
    private String state;
    private String city;
    private String postalCode;
    private String occupation;
    private String timestamp;
    private List<String> imageUrls;
    private String imageUrl;
    private String aadhaarNumber;
    private String bloodGroup;
    private String gender;
    private String bloodDonationHistory;
    private String applicationType;
    private String diseaseHistory;
    private String additionalInfo;
    private String birthdate;

    public BloodDonation() {
        this.applicationType = "Blood donation form";
    }

    public BloodDonation(String firstName, String lastName, String phoneNumber, String email, String address,
                         String state, String city, String postalCode, String occupation, String aadhaarNumber,
                         String bloodGroup, String gender, String bloodDonationHistory, String diseaseHistory,
                         String additionalInfo, String birthdate, String relationType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.state = state;
        this.city = city;
        this.postalCode = postalCode;
        this.occupation = occupation;
        this.aadhaarNumber = aadhaarNumber;
        this.bloodGroup = bloodGroup;
        this.gender = gender;
        this.bloodDonationHistory = bloodDonationHistory;
        this.diseaseHistory = diseaseHistory;
        this.additionalInfo = additionalInfo;
        this.birthdate = birthdate;
        this.relationType = relationType;
        this.imageUrls = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodDonationHistory() {
        return bloodDonationHistory;
    }

    public void setBloodDonationHistory(String bloodDonationHistory) {
        this.bloodDonationHistory = bloodDonationHistory;
    }

    public String getDiseaseHistory() {
        return diseaseHistory;
    }

    public void setDiseaseHistory(String diseaseHistory) {
        this.diseaseHistory = diseaseHistory;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public void setBloodDonationType(String bloodDonationType) {
        this.bloodDonationHistory = bloodDonationType;
        // Assuming that 'bloodDonationHistory' field is used to store blood donation type
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.birthdate = dateOfBirth;
    }

    public void setDiseaseHistoryDescription(String diseaseHistoryDescription) {
        this.diseaseHistory = diseaseHistoryDescription;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void addImageUrl(String imageUrl) {
        imageUrls.add(imageUrl);
    }
}
