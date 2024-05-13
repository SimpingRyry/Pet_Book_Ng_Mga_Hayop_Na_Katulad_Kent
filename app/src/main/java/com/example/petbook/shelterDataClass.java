package com.example.petbook;

public class shelterDataClass {
    public String imageurl, sheltername, email,address;

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getSheltername() {
        return sheltername;
    }

    public void setSheltername(String sheltername) {
        this.sheltername = sheltername;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public shelterDataClass(String imageurl, String sheltername, String email,String address) {
        this.imageurl = imageurl;
        this.sheltername = sheltername;
        this.email = email;
        this.address = address;
    }
}
