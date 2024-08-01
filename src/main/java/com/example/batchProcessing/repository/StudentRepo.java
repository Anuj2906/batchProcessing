package com.example.batchProcessing.repository;

import java.io.Serializable;
import com.example.batchProcessing.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
//This repo is for Student
public interface   StudentRepo extends JpaRepository <Student, Serializable>{

}

