package com.example.batchProcessing.config;

import com.example.batchProcessing.entity.Student;
import org.springframework.batch.item.ItemProcessor;

//This is our processor where we write down the whole logic
public class StudentDataConfig implements ItemProcessor<Student,Student>   {

    @Override
    public Student process(Student item) throws Exception{

                if(item.getMarks() >= 650){
                    item.setResult_prediction("May get decent college");

                }
                else {
                    item.setResult_prediction("Might get a good college");
                }

        return item;
    }


}
