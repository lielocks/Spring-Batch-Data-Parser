package com.batch.sample.springbatch.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    Integer id;
    String name;
    String brand;
    String category;
    String itemCategory;
    Integer price;
    String brandUrl;
    String kakaoUrl;
    String coupangUrl;
    String naverUrl;
    String description;
    String jobName;
    String jobChildName;
    String age;
    String situation;
    String emotion;
    String gender;
    String type;
    String relation;

}
