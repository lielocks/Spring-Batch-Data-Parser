package com.batch.sample.springbatch.batch;

import com.batch.sample.springbatch.domain.Item;
import com.batch.sample.springbatch.dto.CsvDto;
import com.batch.sample.springbatch.processor.CsvItemProcessor;
import com.batch.sample.springbatch.reader.CsvItemReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CsvJpaPageJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final CsvItemProcessor csvItemProcessor;
    private final CsvItemReader csvItemReader;
    private static int chunkSize = 5;

    @Bean
    public Job csvJpaJob_batchBuild() throws Exception {
        return jobBuilderFactory.get("CsvJpaPageJob")
                .start(csvJpa_batchStep1())
                .build();
    }

    @Bean
    public Step csvJpa_batchStep1() throws Exception {
        return stepBuilderFactory.get("csvJpa_batchStep1")
                .<CsvDto, Item>chunk(chunkSize)
                .reader(csvJpa_FileReader())
                .processor(csvItemProcessor)
                .writer(csvJpaJob_dbItemWriter())
                .build();
    }

    @Bean
    public CsvItemReader csvJpa_FileReader() {
        return csvItemReader;
    }


    @Bean
    public JpaItemWriter<Item> csvJpaJob_dbItemWriter() {
        JpaItemWriter<Item> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
