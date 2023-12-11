package com.batch.sample.springbatch.batch;

import com.batch.sample.springbatch.domain.Item;
import com.batch.sample.springbatch.processor.CsvItemProcessor;
import com.batch.sample.springbatch.reader.CsvItemReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class KafkaJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private static int chunkSize = 5;
    private final KafkaTemplate<String, Item> itemKafkaTemplate;
    private final CsvItemProcessor csvItemProcessor;
    private final CsvItemReader csvItemReader;




}
