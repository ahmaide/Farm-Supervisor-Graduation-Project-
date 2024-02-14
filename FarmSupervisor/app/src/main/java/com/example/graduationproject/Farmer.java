package com.example.graduationproject;

public class Farmer {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String mobileNumber;

    public Farmer(){

    }

    public Farmer(String email, String firstname, String lastname, String password, String phone) {
        this.email = email;
        this.firstName = firstname;
        this.lastName = lastname;
        this.password = password;
        this.mobileNumber = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }


}