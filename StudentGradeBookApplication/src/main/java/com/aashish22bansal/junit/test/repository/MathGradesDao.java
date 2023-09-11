package com.aashish22bansal.junit.test.repository;

import com.aashish22bansal.junit.test.models.MathGrade;
import org.springframework.data.repository.CrudRepository;

public interface MathGradesDao extends CrudRepository<MathGrade, Integer> {
    public Iterable<MathGrade> findGradeByStudentId(int id);

    public void deleteByStudentId(int id);
}
