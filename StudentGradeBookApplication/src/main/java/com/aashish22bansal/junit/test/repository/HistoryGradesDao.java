package com.aashish22bansal.junit.test.repository;

import com.aashish22bansal.junit.test.models.HistoryGrade;
import org.springframework.data.repository.CrudRepository;

public interface HistoryGradesDao extends CrudRepository<HistoryGrade, Integer> {
    public Iterable<HistoryGrade> findGradeByStudentId(int id);

    public void deleteByStudentId(int id);
}
