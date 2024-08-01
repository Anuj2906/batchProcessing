package com.example.batchProcessing.entity;

import  jakarta.persistence.Column;
import  jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Student_Result")
@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class Student {

    @Id
    @Column(name="Serial_Number")
    private int serial_number;

    @Column(name="Marks")
    private int marks;

    @Column(name="State")
    private String state;

    @Column(name="City")
    private String city;

    @Column(name="Center_Name")
    private String center_name;

    @Column(name="Center_Number")
    private int center_number;

    @Column(name="Result_Prediction")
    private String result_prediction;

}
