package com.batch.sample.springbatch.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface DeptRepository extends CrudRepository<Dept, Long> {
}
