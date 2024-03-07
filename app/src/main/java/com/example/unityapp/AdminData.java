package com.example.unityapp;

public class AdminData {
    private String email2;
    private String organizationPhone;

    public AdminData(String email2, String organizationPhone) {
        this.email2 = email2;
        this.organizationPhone = organizationPhone;
    }

    public String getEmail2() {
        return email2;
    }

    public String getOrganizationPhone() {
        return organizationPhone;
    }
}
