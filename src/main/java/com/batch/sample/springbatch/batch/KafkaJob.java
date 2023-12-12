package com.batch.sample.springbatch.batch;

import com.batch.sample.springbatch.domain.Item;
import com.batch.sample.springbatch.dto.CsvDto;
import com.batch.sample.springbatch.processor.CsvItemProcessor;
import com.batch.sample.springbatch.reader.CsvItemReader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class KafkaJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final int chunkSize = 5;
    private final KafkaTemplate<String, Item> itemKafkaTemplate;
    private final CsvItemProcessor csvItemProcessor;
    private final CsvItemReader csvItemReader;

    @Bean
    public Job importItemInfo() {
        return jobBuilderFactory.get("KafkaJob")
                .incrementer(new RunIdIncrementer())
                .start(fromFileIntoKafka())
                .build();
    }

    @Bean
    public Step fromFileIntoKafka() {
        return stepBuilderFactory.get("fromFileIntoKafka")
                .<CsvDto, Future<Item>>chunk(chunkSize)
                .reader(csvItemReader)
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .faultTolerant()
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public JpaItemWriter<Item> itemJpaItemWriter() {
        return new JpaItemWriterBuilder<Item>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(15);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("Thread N -> : ");
        return executor;
    }

    @Bean
    public AsyncItemProcessor<CsvDto, Item> asyncItemProcessor() {
        var asyncItemProcessor = new AsyncItemProcessor<CsvDto, Item>();
        asyncItemProcessor.setDelegate(csvItemProcessor);
        asyncItemProcessor.setTaskExecutor(taskExecutor());
        return asyncItemProcessor;
    }

    @Bean
    public AsyncItemWriter<Item> asyncItemWriter() {
        var asyncWriter = new AsyncItemWriter<Item>();
        asyncWriter.setDelegate(itemKafkaItemWriter());
        return asyncWriter;
    }

    @Bean
    @SneakyThrows
    public KafkaItemWriter<String, Item> itemKafkaItemWriter() {
        var kafkaItemWriter = new KafkaItemWriter<String, Item>();
        kafkaItemWriter.setKafkaTemplate(itemKafkaTemplate);
        kafkaItemWriter.setItemKeyMapper(item -> String.valueOf(item.getId()));
        kafkaItemWriter.setDelete(Boolean.FALSE);
        kafkaItemWriter.afterPropertiesSet();
        return kafkaItemWriter;
    }



}
