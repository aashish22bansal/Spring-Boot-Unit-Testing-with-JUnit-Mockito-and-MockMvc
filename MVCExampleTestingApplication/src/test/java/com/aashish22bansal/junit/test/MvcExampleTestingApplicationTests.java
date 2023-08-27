package com.aashish22bansal.junit.test;

import com.aashish22bansal.junit.test.models.CollegeStudent;
import com.aashish22bansal.junit.test.models.StudentGrades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * With the @SpringBootTest() Annotation, we will specify that this is the main class
 * for beginning the test cases. Within this Annotation, we can also use the "classes"
 * parameter within which we can specify the Application Execution Class.
 *
 * The @Value("${}") Annotation would be used to specify the property from the
 * application.properties file. We will specify the value within '{}'.
 *
 * THe @Autowired Annotation is used the Spring Beans so that the configuration can
 * automatically be taken care by Spring MVC.
 */
@SpringBootTest(classes = MvcExampleTestingApplication.class)
class MvcExampleTestingApplicationTests {

	private static int count = 0;

	@Value("${info.app.name}")
	private String appInfo;

	@Value("${info.app.description}")
	private String appDescription;

	@Value("${info.app.version}")
	private String appVersion;

	@Value("${info.school.name}")
	private String schoolName;

	@Autowired
	CollegeStudent student;

	@Autowired
	StudentGrades studentGrades;

	@Autowired
	ApplicationContext context;

	@BeforeEach
	public void beforeEach(){
		count = count + 1;
		System.out.println("Testing: " + appInfo + " which is " + appDescription + "for Version: " + appVersion + ". Execution of test method " + count);

		// Initialization on Student Object that we Autowired.
		student.setFirstName("Aashish");
		student.setLastName("Bansal");
		student.setEmailAddress("analyst.aashish@gmail.com");
		studentGrades.setMathGradeResults(new ArrayList<>(Arrays.asList(100.0, 85.0, 76.50, 91.75)));
		student.setStudentGrades(studentGrades);
	}

	@DisplayName("Add grade results for student grades")
	@Test
	public void addGradeResultsForStudentGrades(){
		// Using assertEquals(EXPECTED_VALUE, CALCULATED_VALUE)
		assertEquals(353.25, studentGrades.addGradeResultsForSingleClass(
				student.getStudentGrades().getMathGradeResults()
		));
	}

	@DisplayName("Add grade results for student grades not equal")
	@Test
	public void addGradeResultsForStudentGradesAssertNotEquals(){
		// Using assertNotEquals(BAD_VALUE, CALCULATED_VALUE)
		assertNotEquals(0, studentGrades.addGradeResultsForSingleClass(
				student.getStudentGrades().getMathGradeResults()
		));
	}

	@DisplayName("Is grade greater")
	@Test
	public void isGradeGreaterStudentGrades(){
		assertTrue(studentGrades.isGradeGreater(90, 75), "failure - should be true");
	}

	@DisplayName("Is grade greater false")
	@Test
	public void isGradeGreaterStudentGradesAssertFalse(){
		assertFalse(studentGrades.isGradeGreater(89, 92), "failure - should be false");
	}

	@DisplayName("Check Null for student grades")
	@Test
	public void checkNullForStudentGrades(){
		assertNotNull(studentGrades.checkNull(student.getStudentGrades().getMathGradeResults()), "Object should not be null.");
	}

	@DisplayName("Create student without grade init")
	@Test
	public void createStudentWithoutGradeInit(){
		// Retrieving another college student
		CollegeStudent studentTwo = context.getBean("collegeStudent", CollegeStudent.class);
		// Setting up some Information for the Student
		studentTwo.setFirstName("Firstname 2");
		studentTwo.setLastName("Lastname 2");
		studentTwo.setEmailAddress("student2@gmail.com");
		// Basic tests on the properties
		assertNotNull(studentTwo.getFirstName());
		assertNotNull(studentTwo.getLastName());
		assertNotNull(studentTwo.getEmailAddress());
		assertNotNull(studentGrades.checkNull(studentTwo.getStudentGrades()));
	}

	@DisplayName("Verify students are prototypes")
	@Test
	public void verifyStudentsArePrototypes(){
		CollegeStudent studentTwo = context.getBean("collegeStudent", CollegeStudent.class);
		assertNotSame(student, studentTwo);
	}

	@DisplayName("Find Grade Point Average")
	@Test
	public void findGradePointAverage(){
		assertAll("Testing all assertEquals",
				() -> assertEquals(353.25, studentGrades.addGradeResultsForSingleClass(
						student.getStudentGrades().getMathGradeResults())),
				() -> assertEquals(88.31, studentGrades.findGradePointAverage(
						student.getStudentGrades().getMathGradeResults()))
		);
	}

	@Test
	void contextLoads() {
	}

}
