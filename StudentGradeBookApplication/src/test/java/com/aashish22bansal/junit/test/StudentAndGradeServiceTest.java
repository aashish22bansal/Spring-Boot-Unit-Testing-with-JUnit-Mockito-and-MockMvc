package com.aashish22bansal.junit.test;

import com.aashish22bansal.junit.test.models.*;
import com.aashish22bansal.junit.test.repository.HistoryGradesDao;
import com.aashish22bansal.junit.test.repository.MathGradesDao;
import com.aashish22bansal.junit.test.repository.ScienceGradesDao;
import com.aashish22bansal.junit.test.repository.StudentDAO;
import com.aashish22bansal.junit.test.service.StudentAndGradeService;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDAO studentDao;

    @Autowired
    private MathGradesDao mathGradesDao;

    @Autowired
    private ScienceGradesDao scienceGradesDao;

    @Autowired
    private HistoryGradesDao historyGradesDao;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;


//    @BeforeAll
//    public static void setup(){
//
//    }

    @BeforeEach
    public void setupDatabase(){
        jdbcTemplate.execute(sqlAddStudent);

        jdbcTemplate.execute(sqlAddMathGrade);
        jdbcTemplate.execute(sqlAddScienceGrade);
        jdbcTemplate.execute(sqlAddHistoryGrade);
    }

    @Test
    public void createStudentService(){
        // Using the studentService to create a new Student
        studentService.createStudent("StudentAFirstName", "StudentAFirstName", "StudentAEmail@gmail.com");

        // Retrieving the Student from the DAO as:
        CollegeStudent student = studentDao.findByEmailAddress("StudentAEmail@gmail.com");

        // Asserting Test
        assertEquals("StudentAEmail@gmail.com", student.getEmailAddress(), "find by email");
    }

    @Test
    public void isStudentNullCheck(){
        assertTrue(studentService.checkIfStudentIsNull(6));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService(){
        // Retrieving the student with ID 1
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(6);
        Optional<MathGrade> deletedMathGrade = mathGradesDao.findById(6);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradesDao.findById(6);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradesDao.findById(6);

        // Checking if we have actually found the student or not
        assertTrue(deletedCollegeStudent.isPresent(), "Return True");
        assertTrue(deletedMathGrade.isPresent(), "Math Grade is present!");
        assertTrue(deletedScienceGrade.isPresent(), "Science Grade is present!");
        assertTrue(deletedHistoryGrade.isPresent(), "History Grade is present!");

        // Deleting the Student
        studentService.deleteStudent(6);

        // Attempting the retrieve the student again
        deletedCollegeStudent = studentDao.findById(6);
        deletedMathGrade = mathGradesDao.findById(6);
        deletedScienceGrade = scienceGradesDao.findById(6);
        deletedHistoryGrade = historyGradesDao.findById(6);

        // Making sure that the student is not present
        assertFalse(deletedCollegeStudent.isPresent(), "Return False");
        assertFalse(deletedMathGrade.isPresent(), "Math Grade is deleted!");
        assertFalse(deletedScienceGrade.isPresent(), "Science Grade is deleted!");
        assertFalse(deletedHistoryGrade.isPresent(), "History Grade is deleted!");
    }

    @Sql("/insertData.sql")
    @Test
    public void getGradeBootService(){
        // Creating an iterable and fetching all the students
        Iterable<CollegeStudent> iterableCollegeStudent = studentService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        // Grabbing each student and placing it in a list
        for(CollegeStudent collegeStudent:iterableCollegeStudent){
            collegeStudents.add(collegeStudent);
        }

        // Asserting number of students in the list
        assertEquals(5, collegeStudents.size());
    }

    @Test
    public void createGradeService(){
        // Create the grade
        assertTrue(studentService.createGrade(80.50, 6, "math"));
        assertTrue(studentService.createGrade(80.50, 6, "science"));
        assertTrue(studentService.createGrade(80.50, 6, "history"));
        // Get all grades with studentId
        Iterable<MathGrade> mathGrades = mathGradesDao.findGradeByStudentId(6);
        Iterable<ScienceGrade> scienceGrades = scienceGradesDao.findGradeByStudentId(6);
        Iterable<HistoryGrade> historyGrades = historyGradesDao.findGradeByStudentId(6);
        // Verify there is grades
        assertTrue(
                ((Collection<MathGrade>) mathGrades).size() == 2,
                "Student has Math Grades!"
        );
        assertTrue(
                ((Collection<ScienceGrade>) scienceGrades).size() == 2,
                "Student has Science Grades!"
        );
        assertTrue(
                ((Collection<HistoryGrade>) historyGrades).size() == 2,
                "Student has History Grades!"
        );
    }

    @Test
    public void createGradeServiceReturnFalse(){
        // Invalid Student Marks
        assertFalse(studentService.createGrade(105, 6, "math"));
        // Invalid Student Marks
        assertFalse(studentService.createGrade(-5, 6, "math"));
        // Invalid Student ID
        assertFalse(studentService.createGrade(80.50, 2, "math"));
        // Invalid Subject
        assertFalse(studentService.createGrade(80.50, 6, "literature"));
    }

    @Test
    public void deleteGradeService(){
        assertEquals(
                6, studentService.deleteGrade(6, "math"),
                "Returns Student ID after Delete!"
        );
        assertEquals(
                6, studentService.deleteGrade(6, "science"),
                "Returns Student ID after Delete!"
        );
        assertEquals(
                6, studentService.deleteGrade(6, "history"),
                "Returns Student ID after Delete!"
        );
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero(){
        // Invalid Grade ID
        assertEquals(
                0, studentService.deleteGrade(0, "science"),
                "Student not have a Grade ID of 0"
        );
        // Invalid Subject
        assertEquals(
                0, studentService.deleteGrade(6, "literature"),
                "No Student should have a literature class!"
        );
    }

    @Test
    public void StudentInformation(){
        // Retrieving the Gradebook for a CollegeStudent
        GradebookCollegeStudent gradebookCollegeStudent;
        gradebookCollegeStudent = studentService.studentInformation(6);

        // Adding Asserts
        assertNotNull(gradebookCollegeStudent);

        // Verifying the Student Information - Student ID
        assertEquals(6, gradebookCollegeStudent.getId(), "Student ID Received!");
        // Verifying the Student Information - Student First Name
        assertEquals("Aashish", gradebookCollegeStudent.getFirstname(), "Student Firstname Received!");
        // Verifying the Student Information - Student Last Name
        assertEquals("Bansal", gradebookCollegeStudent.getLastname(), "Student Lastname Received!");
        // Verifying the Student Information - Student Email Address
        assertEquals(
                "analyst.aashish@gmail.com", gradebookCollegeStudent.getEmailAddress(),
                "Student Email Address Received!"
        );

        // Verifying the Student Information - Number of Grades for a given Subjects
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size(), "Received a 1");
        assertTrue(
                gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1,
                "Student Math Grade not Received!"
        );
        assertTrue(
                gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1,
                "Student Science Grade not Received!"
        );
        assertTrue(
                gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1,
                "Student History Grade not Received"
        );
    }

    @Test
    public void studentInformationServiceReturnNull(){
        // Obtaining Student Information
        GradebookCollegeStudent gradebookCollegeStudent;
        gradebookCollegeStudent = studentService.studentInformation(0);

        // Making Assertions
        assertNull(gradebookCollegeStudent);
    }

    @AfterEach
    public void setupAfterTransaction(){
        jdbcTemplate.execute(sqlDeleteStudent);
        jdbcTemplate.execute(sqlDeleteMathGrade);
        jdbcTemplate.execute(sqlDeleteScienceGrade);
        jdbcTemplate.execute(sqlDeleteHistoryGrade);
    }
}
