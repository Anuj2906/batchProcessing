package com.example.batchProcessing.config;

import com.example.batchProcessing.entity.Student;
import com.example.batchProcessing.repository.StudentRepo;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableBatchProcessing
@AllArgsConstructor
public class ConfigBatch {

    @Autowired
    private StudentRepo studentRepo;


    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;


    // Create Reader
    @Bean
    @StepScope
    public FlatFileItemReader<Student> dataReader() {
        FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();
        try {
            itemReader.setResource(new FileSystemResource("src/main/resources/NEET_2024_RESULTS.csv"));

            itemReader.setName("csv-reader");
            itemReader.setLinesToSkip(1);
            itemReader.setLineMapper(lineMapper());
            System.out.println("CSV file found");
            return itemReader;
        }
        catch(Error err){
            System.out.println("could not find CSV file");
            return  null;
        }

    }

    // Line Mapper useful to convert each line as a Java Object
    private LineMapper<Student> lineMapper() {
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setStrict(false);
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames("serial_number", "marks", "state", "city", "center_name", "center_number");

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Student.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    // Create Processor
    @Bean
    public StudentDataConfig studentDataConfig() {
        return new StudentDataConfig();
    }

    // Create Writer
    @Bean
    public RepositoryItemWriter<Student> customWriter() {
        RepositoryItemWriter<Student> repositoryWriter = new RepositoryItemWriter<>();
        repositoryWriter.setRepository(studentRepo);
        repositoryWriter.setMethodName("save");
        return repositoryWriter;
    }

    // Create Step
    @Bean
    public Step step() {
        return new StepBuilder("step-1", jobRepository)
                .<Student, Student>chunk(10, transactionManager)
                .reader(dataReader())
                .processor(studentDataConfig())
                .writer(customWriter())
                .build();
    }

    // Create Job
    @Bean
    public Job job() {
        return new JobBuilder("job-1", jobRepository)
                .start(step())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }
}
