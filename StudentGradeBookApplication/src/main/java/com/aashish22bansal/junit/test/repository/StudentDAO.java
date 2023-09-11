package com.aashish22bansal.junit.test.repository;

import com.aashish22bansal.junit.test.models.CollegeStudent;
import org.springframework.data.repository.CrudRepository;

public interface StudentDAO extends CrudRepository<CollegeStudent, Integer> {
    public CollegeStudent findByEmailAddress(String emailAddress);
}
