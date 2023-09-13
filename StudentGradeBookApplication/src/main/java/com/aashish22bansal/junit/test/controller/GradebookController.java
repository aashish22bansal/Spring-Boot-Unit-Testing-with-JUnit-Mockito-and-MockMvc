package com.aashish22bansal.junit.test.controller;

import com.aashish22bansal.junit.test.models.CollegeStudent;
import com.aashish22bansal.junit.test.models.GradebookCollegeStudent;
import com.aashish22bansal.junit.test.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.aashish22bansal.junit.test.models.Gradebook;

@Controller
public class GradebookController {

	@Autowired
	private Gradebook gradebook;

	@Autowired
	private StudentAndGradeService studentAndGradeService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getStudents(Model m) {
		Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
		m.addAttribute("students", collegeStudents);
		return "index";
	}

	@PostMapping(value = "/")
	public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m){
		studentAndGradeService.createStudent(
				student.getFirstname(),
				student.getLastname(),
				student.getEmailAddress()
		);
		// Creating students, then adding them to a list and then adding them as a Model Attribute
		Iterable<CollegeStudent> collegeStudentIterable = studentAndGradeService.getGradebook();
		//Adding this as a Model Attribute and provide a reference for collegeStudentIteable
		m.addAttribute("students", collegeStudentIterable);
		/**
		 * With this, effectively, once we add a new student, we will get a list of all the students from the database
		 * and then adding that as a Model Attribute.
		 */
		return "index";
	}

	@GetMapping("/delete/student/{id}")
	public String deleteStudent(@PathVariable int id, Model m){
		if(!studentAndGradeService.checkIfStudentIsNull(id)){
			// Return the View Name as "error"
			return "error";
		}
		studentAndGradeService.deleteStudent(id);
		Iterable<CollegeStudent> collegeStudentIterable = studentAndGradeService.getGradebook();
		m.addAttribute("students", collegeStudentIterable);
		return "index";
	}

	@GetMapping("/studentInformation/{id}")
	public String studentInformation(@PathVariable int id, Model m) {
		if(!studentAndGradeService.checkIfStudentIsNull(id)){
			return "error";
		}

		// Calling the Configured Method in StudentAndGradeService
		studentAndGradeService.configureStudentInformationModel(id, m);

		// Returning the View Name
		return "studentInformation";
	}

	@PostMapping(value = "/grades")
	public String createGrade(
			@RequestParam("grade") double grade,
			@RequestParam("gradeType") String gradeType,
			@RequestParam("studentId") int studentId,
			Model m
	){
		if(!studentAndGradeService.checkIfStudentIsNull(studentId)){
			return "error";
		}

		// Creating the grade using StudentAndGradeService
		boolean success = studentAndGradeService.createGrade(grade, studentId, gradeType);

		// Checking if the grade was created successfully
		if(!success){
			return "error";
		}

		// Calling the Configured method
		studentAndGradeService.configureStudentInformationModel(studentId, m);

		// Returning the View Name
		return "studentInformation";
	}

	@GetMapping("/grades/{id}/{gradeType}")
	public String deleteGrade(@PathVariable int id, @PathVariable String gradeType, Model m){
		// Deleting grade using StudentAndGradeService
		int studentId = studentAndGradeService.deleteGrade(id, gradeType);

		// Checking if there is any student with the received ID
		if(studentId == 0){
			return "error";
		}

		studentAndGradeService.configureStudentInformationModel(studentId, m);

		return "studentInformation";
	}

}
