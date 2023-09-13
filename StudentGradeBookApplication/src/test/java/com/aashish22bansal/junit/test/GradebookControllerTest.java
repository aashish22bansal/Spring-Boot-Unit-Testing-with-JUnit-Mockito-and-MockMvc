package com.aashish22bansal.junit.test;

import com.aashish22bansal.junit.test.models.CollegeStudent;
import com.aashish22bansal.junit.test.models.GradebookCollegeStudent;
import com.aashish22bansal.junit.test.models.MathGrade;
import com.aashish22bansal.junit.test.repository.HistoryGradesDao;
import com.aashish22bansal.junit.test.repository.MathGradesDao;
import com.aashish22bansal.junit.test.repository.ScienceGradesDao;
import com.aashish22bansal.junit.test.repository.StudentDAO;
import com.aashish22bansal.junit.test.service.StudentAndGradeService;
import com.fasterxml.jackson.annotation.OptBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring6.expression.Mvc;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

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

    @BeforeAll
    public static void setup(){
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "StudentHfirstname");
        request.setParameter("lastname", "StudentHlastname");
        request.setParameter("emailAddress", "studentHemailaddress@gmail.com");
    }

    @BeforeEach
    public void beforeEach(){
        jdbcTemplate.execute(sqlAddStudent);

        jdbcTemplate.execute(sqlAddMathGrade);
        jdbcTemplate.execute(sqlAddScienceGrade);
        jdbcTemplate.execute(sqlAddHistoryGrade);
    }

    @Test
        public void getStudentsHttpRequest() throws Exception{
        // Setting up some Objects
        CollegeStudent studentOne = new GradebookCollegeStudent("StudentFfirstname", "StudentFlastname", "studentFemailaddress@gmail.com");
        CollegeStudent studentTwo = new GradebookCollegeStudent("StudentGfirstname", "StudentGlastname", "studentGemailaddress@gmail.com");

        // Adding student objects to a list
        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne, studentTwo));

        // Setting up Mocks
        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        // Assertions
        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        // Performing Web-related Testing
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/")
        ).andExpect(
                status().isOk()
        ).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");
    }

    @Test
    public void createStudentHttpRequest() throws Exception{

        // Creating a list for assertions
        CollegeStudent studentOne = new CollegeStudent("StudentIfirstname", "StudentIlastname", "studentIemailaddress@gmail.com");
        //Adding the above student to the list
        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne));

        // Setting up a Mock for the studentService
        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        // Assertion
        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = this.mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("firstname", request.getParameterValues("firstname"))
                .param("lastname", request.getParameterValues("lastname"))
                .param("emailAddress", request.getParameterValues("emailAddress")))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        // Performing Assertion
        ModelAndViewAssert.assertViewName(mav, "index");

        // Setting up CollegeStudent
        CollegeStudent verifyStudent = studentDao.findByEmailAddress("analyst.aashish@gmail.com");

        // asserting to verify creation
        assertNotNull(verifyStudent, "Student should be found");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception{
        assertTrue(studentDao.findById(6).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(
                "/delete/student/{id}", 6)
        ).andExpect(
                status().isOk()
        ).andReturn();

        // Obtaining the ModelAndView using the mvcResult
        ModelAndView mav = mvcResult.getModelAndView();

        // Creating Assertion
        ModelAndViewAssert.assertViewName(mav, "index");

        // Asserting to check if the student was actually deleted
        assertFalse(studentDao.findById(6).isPresent());
    }

    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception{
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/delete/student/{id}", 0)
        ).andExpect(
                status().isOk()
        ).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void studentInformationHttpRequest() throws Exception {
        // Asserting that the Student does not exist
        assertTrue(studentDao.findById(6).isPresent(), "Student does not exist");

        // Performing Request
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(
                "/studentInformation/{id}",6)
        ).andExpect(
                status().isOk()
        ).andReturn();

        // Retrieving the ModelAndView
        ModelAndView mav = mvcResult.getModelAndView();

        // Asserting View Name
        ModelAndViewAssert.assertViewName(mav, "studentInformation");
    }

    @Test
    public void StudentInformationHttpStudentDoesNotExistRequest() throws Exception{
        // Asserting that the Student does not exist
        assertFalse(studentDao.findById(0).isPresent(), "Student Exists");

        // Performing Request
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(
                "/studentInformation/{id}",0)
        ).andExpect(
                status().isOk()
        ).andReturn();

        // Retrieving the ModelAndView
        ModelAndView mav = mvcResult.getModelAndView();

        // Asserting View Name
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void createValidGradeHttpRequest() throws Exception{
        // Asserting if the student exists
        assertTrue(studentDao.findById(6).isPresent(), "Student is not present!");

        // Obtaining information for the Student ID
        GradebookCollegeStudent student = studentAndGradeService.studentInformation(6);

        // Obtaining Student Grades
        assertEquals(1,
                student.getStudentGrades().getMathGradeResults().size(),
                "No. of grades are not equal for Math 1"
        );
//        assertEquals(1,
//                student.getStudentGrades().getScienceGradeResults().size(),
//                "No. of grades are not equal for Science 1"
//        );
//        assertEquals(1,
//                student.getStudentGrades().getHistoryGradeResults().size(),
//                "No. of grades are not equal History 1"
//        );

        // Sending a Request
        MvcResult mvcResult;
        mvcResult = this.mockMvc.perform(
                post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "math")
                        .param("studentId", "6")
        ).andExpect(
                status().isOk()
        ).andReturn();
//        mvcResult = mockMvc.perform(
//                post("/grades")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("grade", "85.00")
//                        .param("gradeType", "science")
//                        .param("studentId", "6")
//        ).andExpect(
//                status().isOk()
//        ).andReturn();
//        mvcResult = mockMvc.perform(
//                post("/grades")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("grade", "85.00")
//                        .param("gradeType", "history")
//                        .param("studentId", "6")
//        ).andExpect(
//                status().isOk()
//        ).andReturn();

        // Obtaining the ModelAndView
        ModelAndView mav = mvcResult.getModelAndView();

        // Asserting the View Name
        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        // Retrieving Student Information again
        student = studentAndGradeService.studentInformation(6);

        // Asserting updated number of grades
        assertEquals(2,
                student.getStudentGrades().getMathGradeResults().size(),
                "No. of grades are not equal for Math 2"
        );
//        assertEquals(2,
//                student.getStudentGrades().getScienceGradeResults().size(),
//                "No. of grades are not equal for Science 2"
//        );
//        assertEquals(2,
//                student.getStudentGrades().getHistoryGradeResults().size(),
//                "No. of grades are not equal for History 2"
//        );
    }

    @Test
    public void createAValidGradeHttpRequestStudentDoesNotExistEmptyResponse() throws Exception{
        // Making a POST HTTP Request
        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "85.00")
                .param("gradeType", "history")
                .param("studentId", "0")
        ).andExpect(
                status().isOk()
        ).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void createANonValidGradeHttpRequestGradeTypeDoesNotExistEmptyResponse() throws Exception{
        // Making a POST HTTP Request
        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "85.00")
                .param("gradeType", "literature")
                .param("studentId", "6")
        ).andExpect(
                status().isOk()
        ).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void deleteAValidGradeHttpRequest() throws Exception{
        // Obtaining the Grade
        Optional<MathGrade> mathGrade = mathGradesDao.findById(6);

        // Checking if the grade is present or not
        assertTrue(mathGrade.isPresent(), "Math grade is not present!");

        // Performing Request
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 6, "math")
        ).andExpect(
                status().isOk()
        ).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        // Confirming the grade was deleted by attempting to retrieve from database
        mathGrade = mathGradesDao.findById(6);
        assertFalse(mathGrade.isPresent(), "Math grade was not deleted!");
    }

    @Test
    public void deleteAValidGradeHttpRequestStudentIdDoesNotExistEmptyResponse() throws Exception{
        // Retrieving Grade for a Grade ID that does not exist
        Optional<MathGrade> mathGrade = mathGradesDao.findById(7);

        // Confirming
        assertFalse(mathGrade.isPresent(), "Grade for the ID is present!");

        // Deleting the Grade for an invalid Grade ID
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 7, "math")
        ).andExpect(
                status().isOk()
        ).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void deleteANonValidGradeHttpRequest() throws Exception{
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 6, "literature")
        ).andExpect(
                status().isOk()
        ).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @AfterEach
    public void afterEachTransaction(){
        jdbcTemplate.execute(sqlDeleteStudent);
        jdbcTemplate.execute(sqlDeleteMathGrade);
        jdbcTemplate.execute(sqlDeleteScienceGrade);
        jdbcTemplate.execute(sqlDeleteHistoryGrade);
    }
}
