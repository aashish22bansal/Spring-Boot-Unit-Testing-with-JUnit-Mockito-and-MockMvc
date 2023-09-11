package com.aashish22bansal.junit.test.repository;

import com.aashish22bansal.junit.test.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

public interface ScienceGradesDao extends CrudRepository<ScienceGrade, Integer> {
    public Iterable<ScienceGrade> findGradeByStudentId(int id);

    public void deleteByStudentId(int id);
}
