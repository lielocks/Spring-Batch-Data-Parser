package com.batch.sample.springbatch.processor;

import com.batch.sample.springbatch.domain.Item;
import com.batch.sample.springbatch.dto.CsvDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CsvItemProcessor implements ItemProcessor<CsvDto, Item> {
    @Override
    public Item process(CsvDto dto) throws Exception {
        return new Item(
                dto.getId(), dto.getName(), dto.getBrand(), dto.getCategory(), dto.getItemCategory(), dto.getPrice(),
                dto.getBrandUrl(), dto.getKakaoUrl(), dto.getCoupangUrl(), dto.getNaverUrl(), dto.getDescription(), dto.getJobName(), dto.getJobChildName(),
                dto.getAge(), dto.getSituation(), dto.getEmotion(), dto.getGender(), dto.getType(), dto.getRelation());
    }
}
