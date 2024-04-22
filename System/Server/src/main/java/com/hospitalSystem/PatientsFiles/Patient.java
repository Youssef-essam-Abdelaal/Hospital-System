package com.hospitalSystem.PatientsFiles;

import jakarta.persistence.*;

@Entity
@Table
public class Patient {
    @Id
    @SequenceGenerator(
            name="pat_seq",
            sequenceName = "pat_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator="pat_seq"
    )
    private long id;
    @Column(nullable = false)
    private String name;
    //private LocalDate dob;
    private int age;
    private String gender;
    @Column(unique = true,nullable = false)
    private String ssn;
    private String address;
    private String phonenum;

    public Patient() {
    }

    public Patient(long id, String name, int age, String gender, String ssn, String address, String phonenum) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.ssn = ssn;
        this.address = address;
        this.phonenum = phonenum;
    }

    public Patient(String name, int age, String gender, String ssn, String address, String phonenum) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.ssn = ssn;
        this.address = address;
        this.phonenum = phonenum;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", ssn='" + ssn + '\'' +
                ", address='" + address + '\'' +
                ", phonenum='" + phonenum + '\'' +
                '}';
    }
}