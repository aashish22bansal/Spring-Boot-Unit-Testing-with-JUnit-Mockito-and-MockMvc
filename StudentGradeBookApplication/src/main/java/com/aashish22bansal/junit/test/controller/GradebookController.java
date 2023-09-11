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
		studentAndGradeService.createStudent(student.getFirstname(), student.getLastname(), student.getEmailAddress());
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

		GradebookCollegeStudent studentEntity = studentAndGradeService.studentInformation(id);
		m.addAttribute("student", studentEntity);

		// Adding the Math Average to the Model
		if(studentEntity.getStudentGrades().getMathGradeResults().size() > 0){
			m.addAttribute("mathAverage", studentEntity.getStudentGrades().findGradePointAverage(
					studentEntity.getStudentGrades().getMathGradeResults()
			));
		}
		else{
			// For the Students who do not have a math grade available, then we will say N/A (Not Applicable)
			m.addAttribute("mathAverage", "N/A");
		}
		// Adding the Science Average to the Model
		if(studentEntity.getStudentGrades().getScienceGradeResults().size() > 0){
			m.addAttribute("scienceAverage", studentEntity.getStudentGrades().findGradePointAverage(
					studentEntity.getStudentGrades().getScienceGradeResults()
			));
		}
		else{
			// For the Students who do not have a science grade available, then we will say N/A (Not Applicable)
			m.addAttribute("scienceAverage", "N/A");
		}
		// Adding the History Average to the Model
		if(studentEntity.getStudentGrades().getHistoryGradeResults().size() > 0){
			m.addAttribute("historyAverage", studentEntity.getStudentGrades().findGradePointAverage(
					studentEntity.getStudentGrades().getHistoryGradeResults()
			));
		}
		else{
			m.addAttribute("historyAverage", "N/A");
		}

		// Returning the View Name
		return "studentInformation";
	}

}
