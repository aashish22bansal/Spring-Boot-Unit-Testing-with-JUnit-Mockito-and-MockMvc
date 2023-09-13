package com.aashish22bansal.junit.test.repository;

import com.aashish22bansal.junit.test.models.CollegeStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDao extends CrudRepository<CollegeStudent, Integer> {

    public CollegeStudent findByEmailAddress(String emailAddress);
}
