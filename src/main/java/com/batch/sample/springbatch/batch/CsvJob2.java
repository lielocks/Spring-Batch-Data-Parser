package com.batch.sample.springbatch.batch;

import com.batch.sample.springbatch.custom.CustomBeanWrapperFieldExtractor;
import com.batch.sample.springbatch.dto.CsvDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class CsvJob2 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private static int chunkSize = 5;

    @Bean
    public Job csvJob2_batchBuild() throws Exception {
        return jobBuilderFactory.get("CsvJob2")
                .start(csvJob2_batchStep1())
                .build();
    }

    @Bean
    public Step csvJob2_batchStep1() throws Exception {
        return stepBuilderFactory.get("csvJob2_batchStep1")
                .<CsvDto, CsvDto>chunk(chunkSize)
                .reader(csvJob2_FileReader())
                .writer(csvJob2_FileWriter(new FileSystemResource("output/csvJob2_output.csv")))
                .build();
    }

    @Bean
    public FlatFileItemReader<CsvDto> csvJob2_FileReader() {
        FlatFileItemReader<CsvDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/sample/csvJob2_input.csv"));

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

    @Bean
    public FlatFileItemWriter<CsvDto> csvJob2_FileWriter(Resource resource) throws Exception {
        CustomBeanWrapperFieldExtractor<CsvDto> customBeanWrapperFieldExtractor = new CustomBeanWrapperFieldExtractor();
        customBeanWrapperFieldExtractor.setNames(new String[]{"id", "name", "brand", "category","itemCategory","price",
                "brandUrl", "kakaoUrl", "coupangUrl", "naverUrl", "description", "jobName", "jobChildName",
                "age", "situation", "emotion", "gender", "type", "relation"});
        customBeanWrapperFieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<CsvDto> dtoDelimitedLineAggregator = new DelimitedLineAggregator<>();
        dtoDelimitedLineAggregator.setDelimiter("@");
        dtoDelimitedLineAggregator.setFieldExtractor(customBeanWrapperFieldExtractor);
        return new FlatFileItemWriterBuilder<CsvDto>().name("csvJob2_FileWriter")
                .resource(resource)
                .lineAggregator(dtoDelimitedLineAggregator)
                .build();
    }
}
