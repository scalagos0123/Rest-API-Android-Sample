package com.example.shaun.apiapps;

/**
 * Created by Shaun on 2/21/2017.
 */

public class User {
    private String name;
    private String age;
    private String location;

    public User(String name, String age, String location) {
        this.name = name;
        this.age = age;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
