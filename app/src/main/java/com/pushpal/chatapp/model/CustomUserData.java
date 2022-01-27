package com.pushpal.chatapp.model;

import java.io.Serializable;


public class CustomUserData implements Serializable {
    private String Address, Division, Contact,imageURL;
    private String Name, BloodGroup;
    private String Time, Date;


    public CustomUserData() {

    }

    public CustomUserData(String imageURL,String address, String division, String contact, String name, String bloodGroup, String time, String date) {

        imageURL = imageURL;
        Address = address;
        Division = division;
        Contact = contact;
        Name = name;
        BloodGroup = bloodGroup;
        Time = time;
        Date = date;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getDivision() {
        return Division;
    }

    public void setDivision(String division) {
        this.Division = division;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        this.Contact = contact;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.BloodGroup = bloodGroup;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }
}

