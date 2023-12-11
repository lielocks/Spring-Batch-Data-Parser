package com.batch.sample.springbatch.reader;

import com.batch.sample.springbatch.dto.CsvDto;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class CsvItemReader extends FlatFileItemReader<CsvDto> {

    public CsvItemReader() {
        this.setResource(new ClassPathResource("/sample/csvJob2_input.csv"));

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
        this.setLineMapper(dtoDefaultLineMapper);
    }
}
