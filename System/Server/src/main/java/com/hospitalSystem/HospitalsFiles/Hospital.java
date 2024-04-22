package com.hospitalSystem.HospitalsFiles;

import jakarta.persistence.*;

@Entity
@Table
public class Hospital {
    @Id
    @SequenceGenerator(
            name="hos_seq",
            sequenceName = "hos_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator="hos_seq"
    )
    private long id;
    @Column(nullable = false)
    private String name;
    private String address;
    private String specialization;

    public Hospital() {
    }

    public Hospital(String name, String address, String specialization) {
        this.name = name;
        this.address = address;
        this.specialization = specialization;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "Hospital{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}