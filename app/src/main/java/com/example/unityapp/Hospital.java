package com.example.unityapp;

public class Hospital {
    private String name;
    private String address;
    private String phone;
    private String state;
    private String city;
    private String type;
    private String created_by;
    private String email;
    private String img1, img2, img3, img4;
    private String mapurl;

    // Required default constructor for Firebase
    public Hospital() {
    }

    public Hospital(String name, String address, String phone, String state, String city, String type, String created_by, String email, String img1, String img2, String img3, String img4, String mapurl) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.state = state;
        this.city = city;
        this.type = type;
        this.created_by = created_by;
        this.email = email;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        this.mapurl = mapurl;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    // Setter methods (if needed)
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMapurl() {
        return mapurl;
    }

    public void setMapurl(String mapurl) {
        this.mapurl = mapurl;
    }

}
