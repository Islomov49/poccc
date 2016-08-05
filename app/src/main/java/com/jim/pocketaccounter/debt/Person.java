package com.jim.pocketaccounter.debt;
public class Person {
    private String name, phoneNumber, photo;
    public Person(String name, String phoneNumber, String photo) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
    }
    public Person() {}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getPhoneNumber() {return phoneNumber;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
    public String getPhoto() {return photo;}
    public void setPhoto(String photo) {this.photo = photo;}
}