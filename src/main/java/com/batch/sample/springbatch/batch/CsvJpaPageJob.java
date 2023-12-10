package com.batch.sample.springbatch.batch;

import com.batch.sample.springbatch.domain.Item;
import com.batch.sample.springbatch.dto.CsvDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CsvJpaPageJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
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
                .processor(csvJpaJob_processor())
                .writer(csvJpaJob_dbItemWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<CsvDto> csvJpa_FileReader() {
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

    private ItemProcessor<CsvDto, Item> csvJpaJob_processor() {
        return csvDto -> {
            return new Item(
                    csvDto.getId(), csvDto.getName(), csvDto.getBrand(), csvDto.getCategory(), csvDto.getItemCategory(), csvDto.getPrice(),
                    csvDto.getBrandUrl(),csvDto.getKakaoUrl(), csvDto.getCoupangUrl(),csvDto.getNaverUrl(), csvDto.getDescription(), csvDto.getJobName(), csvDto.getJobChildName(),
                    csvDto.getAge(), csvDto.getSituation(), csvDto.getEmotion(), csvDto.getGender(), csvDto.getType(), csvDto.getRelation());
        };
    }

    @Bean
    public JpaItemWriter<Item> csvJpaJob_dbItemWriter() {
        JpaItemWriter<Item> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
