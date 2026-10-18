package com.javamastery.module10.persistence;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class StudentEntity {

    @Id
    private String id;
    private String name;
    private String email;
    private double gpa;

    @Convert(converter = EncryptedStringConverter.class)
    private String nationalId;


    protected StudentEntity() { } // required by JPA

    public StudentEntity(String id, String name, String email, double gpa) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.gpa = gpa;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public double getGpa() { return gpa; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
}
