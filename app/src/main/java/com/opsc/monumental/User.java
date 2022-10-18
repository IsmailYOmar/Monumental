package com.opsc.monumental;

public class User {
    //creating string variables for user
    String firstName;
    String lastName;
    String emailAddress;

    public User() {

    }
    //creating a method for user which will take in and set the users firstname, last name and email address
    public User(String firstName, String lastName, String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
    }

    //creating getters for user
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}


