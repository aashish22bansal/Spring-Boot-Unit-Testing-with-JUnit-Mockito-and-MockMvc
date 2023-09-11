package com.aashish22bansal.junit.test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.aashish22bansal.junit.test.models.CollegeStudent;
import com.aashish22bansal.junit.test.models.Grade;
import com.aashish22bansal.junit.test.models.HistoryGrade;
import com.aashish22bansal.junit.test.models.MathGrade;
import com.aashish22bansal.junit.test.models.ScienceGrade;

@SpringBootApplication
public class StudentGradeBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentGradeBookApplication.class, args);
	}
	
	@Bean
	@Scope(value = "prototype")
	CollegeStudent getCollegeStudent() {
		return new CollegeStudent();
	}

	@Bean
	@Scope(value = "prototype")
	Grade getMathGrade(double grade) {
		return new MathGrade(grade);
	}

	@Bean
	@Scope(value = "prototype")
	@Qualifier("mathGrades")
	MathGrade getGrade() {
		return new MathGrade();
	}

	@Bean
	@Scope(value = "prototype")
	@Qualifier("scienceGrades")
	ScienceGrade getScienceGrade() {
		return new ScienceGrade();
	}

	@Bean
	@Scope(value = "prototype")
	@Qualifier("historyGrades")
	HistoryGrade getHistoryGrade() {
		return new HistoryGrade();
	}

}
