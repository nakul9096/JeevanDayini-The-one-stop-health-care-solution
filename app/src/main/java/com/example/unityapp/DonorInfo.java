package com.example.unityapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DonorInfo {
    private String fullName;
    private String bloodGroup;
    private String birthDate;
    private String gender;
    private String occupation;
    private String phoneNumber;
    private String email;
    private String address;
    private String state;
    private String city;
    private String postalCode;
    private String aadhaarNumber;
    private String bloodDonationHistory;
    private String diseaseHistory;
    private String additionalInfo;
    private String imageUrl;
    private String documentUrl;
    private String timestamp;
    private String status;
    private String relationType;
    private String userId;
    private String profilePhoto;
    public DonorInfo() {
    }

    // Parameterized constructor
    public DonorInfo(String fullName, String bloodGroup, String birthDate, String gender,
                     String occupation, String phoneNumber, String email, String address,
                     String state, String city, String postalCode, String aadhaarNumber,
                     String bloodDonationHistory, String diseaseHistory, String additionalInfo,
                     String imageUrl, String documentUrl, String relationType, String timestamp, String status, String userId, String profilePhoto) {
        this.fullName = fullName;
        this.bloodGroup = bloodGroup;
        this.birthDate = birthDate;
        this.gender = gender;
        this.occupation = occupation;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.state = state;
        this.city = city;
        this.postalCode = postalCode;
        this.aadhaarNumber = aadhaarNumber;
        this.bloodDonationHistory = bloodDonationHistory;
        this.diseaseHistory = diseaseHistory;
        this.additionalInfo = additionalInfo;
        this.imageUrl = imageUrl;
        this.documentUrl = documentUrl;
        this.relationType = relationType;
        this.timestamp = timestamp;
        this.status = status;
        this.userId = userId;
        this.profilePhoto = profilePhoto;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
    // Getters and Setters for each field

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getUserId()
    {
        return userId;
    }
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
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

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
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

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    // Getter and setter methods for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and setter methods for timestamp
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
