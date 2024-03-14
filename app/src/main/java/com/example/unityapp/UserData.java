package com.example.unityapp;

import android.os.Parcel;
import android.os.Parcelable;

public class UserData implements Parcelable {
    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };
    private String fullName;
    private String adminImage;
    private String selectedState;
    private String selectedCity;
    private String selectedBloodGroup;
    private String email;
    private String phoneNumber;
    private String aadharNumber;
    private String password;
    private String aadhaarNumber;
    private String gender;
    private String profilePictureUrl;
    private String planType;
    private String localFileName;
    private String organizationName;
    private String organizationPhone;
    private String adminType;
    private String imageUrlIdentity1;
    private String adminId;
    private String fcmToken;
    private String aadhaarImage;
    private String imageUrlHorizontal1;
    private String imageUrlHorizontal2;
    private String imageUrlHorizontal3;
    private String aadharImageUrl;
    private String userid;

    public UserData() {
    }

    protected UserData(Parcel in) {
        fullName = in.readString();
        selectedState = in.readString();
        selectedCity = in.readString();
        selectedBloodGroup = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        aadharNumber = in.readString();
        password = in.readString();
        gender = in.readString();
        profilePictureUrl = in.readString();
        aadhaarNumber = in.readString();
        localFileName = in.readString();
        organizationName = in.readString();
        organizationPhone = in.readString();
        adminType = in.readString();
        imageUrlIdentity1 = in.readString();
        fcmToken = in.readString();
        adminImage = in.readString();
        aadhaarImage = in.readString();
        planType = in.readString();
        userid = in.readString();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAadhaarImage() {
        return aadhaarImage;
    }

    public String getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(String selectedState) {
        this.selectedState = selectedState;
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getSelectedBloodGroup() {
        return selectedBloodGroup;
    }

    public void setSelectedBloodGroup(String selectedBloodGroup) {
        this.selectedBloodGroup = selectedBloodGroup;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdminImage() {
        return adminImage;
    }

    public void setAdminImage(String adminImage) {
        this.adminImage = adminImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationPhone() {
        return organizationPhone;
    }

    public void setOrganizationPhone(String organizationPhone) {
        this.organizationPhone = organizationPhone;
    }

    public String getAdminType() {
        return adminType;
    }

    public void setAdminType(String adminType) {
        this.adminType = adminType;
    }

    public String getImageUrlIdentity1() {
        return imageUrlIdentity1;
    }

    public void setImageUrlIdentity1(String imageUrlIdentity1) {
        this.imageUrlIdentity1 = imageUrlIdentity1;
    }
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(selectedState);
        dest.writeString(selectedCity);
        dest.writeString(selectedBloodGroup);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(aadhaarNumber);
        dest.writeString(aadharNumber);
        dest.writeString(password);
        dest.writeString(gender);
        dest.writeString(profilePictureUrl);
        dest.writeString(localFileName);
        dest.writeString(organizationName);
        dest.writeString(organizationPhone);
        dest.writeString(adminType);
        dest.writeString(imageUrlIdentity1);
        dest.writeString(fcmToken);
        dest.writeString(adminImage);
        dest.writeString(aadhaarImage);
        dest.writeString(planType);
        dest.writeString(userid);
    }

    public String getAadharImageUrl() {
        return aadharImageUrl;
    }

    public void setAadharImageUrl(String imageUrl) {
        this.aadharImageUrl = imageUrl;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getImageUrlHorizontal1() {
        return imageUrlHorizontal1;
    }

    public void setImageUrlHorizontal1(String url) {
        this.imageUrlHorizontal1 = url;
    }

    public String getImageUrlHorizontal2() {
        return imageUrlHorizontal2;
    }

    public void setImageUrlHorizontal2(String url) {
        this.imageUrlHorizontal2 = url;
    }

    public String getImageUrlHorizontal3() {
        return imageUrlHorizontal3;
    }

    public void setImageUrlHorizontal3(String url) {
        this.imageUrlHorizontal3 = url;
    }
}
