package com.batch.sample.springbatch.batch;

import com.batch.sample.springbatch.dto.CsvDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class CsvJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private static int chunkSize = 5;

    @Bean
    public Job csvJob1_batchBuild() {
        return jobBuilderFactory.get("CsvJob")
                .start(csvJob1_batchStep1())
                .build();
    }

    @Bean
    public Step csvJob1_batchStep1() {
        return stepBuilderFactory.get("csvJob1_batchStep1")
                .<CsvDto, CsvDto>chunk(chunkSize)
                .reader(csvJob1_FileReader())
                .writer(csvDto -> csvDto.stream().forEach(csvDto2 ->
                        log.debug(csvDto2.toString())))
                .build();
    }

    @Bean
    public FlatFileItemReader<CsvDto> csvJob1_FileReader() {
        FlatFileItemReader<CsvDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/sample/csvJob1_input.csv"));

        DefaultLineMapper<CsvDto> dtoDefaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("id", "name", "brand", "category","itemCategory","price",
        "brandUrl", "kakaoUrl", "coupangUrl", "naverUrl", "description", "jobName", "jobChildName",
        "age", "situation", "emotion", "gender", "type", "relation");
        delimitedLineTokenizer.setDelimiter(",");

        BeanWrapperFieldSetMapper<CsvDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(CsvDto.class);
        dtoDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        dtoDefaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        flatFileItemReader.setLineMapper(dtoDefaultLineMapper);

        return flatFileItemReader;
    }
}
