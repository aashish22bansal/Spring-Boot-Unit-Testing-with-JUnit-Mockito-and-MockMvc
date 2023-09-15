package com.aashish22bansal.junit.test;

import com.aashish22bansal.junit.test.models.CollegeStudent;
import com.aashish22bansal.junit.test.models.MathGrade;
import com.aashish22bansal.junit.test.repository.HistoryGradesDao;
import com.aashish22bansal.junit.test.repository.MathGradesDao;
import com.aashish22bansal.junit.test.repository.ScienceGradesDao;
import com.aashish22bansal.junit.test.repository.StudentDao;
import com.aashish22bansal.junit.test.service.StudentAndGradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.weaver.IHasSourceLocation;
import org.hibernate.boot.jaxb.internal.stax.BufferedXMLEventReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class GradebookControllerTest {
    private static MockHttpServletRequest mockHttpServletRequest;

    // Injecting the JPA Entity Manager
    @PersistenceContext
    private EntityManager entityManager;

    // Setting up a Mock for StudentAndGradeService
    @Mock
    private StudentAndGradeService studentCreateServiceMock;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CollegeStudent collegeStudent;

    public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;

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

    // Initialization Method
    @BeforeAll
    public static void setup(){
        // Creating a Mock Server Request with some initial data
        mockHttpServletRequest = new MockHttpServletRequest();

        // Setting up Parameters
        mockHttpServletRequest.setParameter("firstname", "Chad");
        mockHttpServletRequest.setParameter("lastname", "Darby");
        mockHttpServletRequest.setParameter("emailAddress", "chad.darby@luv2code_school.com");

    }

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @Test
    public void getStudentsHttpRequest() throws Exception{
        // Adding a Student using Entity Manager - Initializing
        collegeStudent.setFirstname("Aashish");
        collegeStudent.setLastname("Bansal");
        collegeStudent.setEmailAddress("analyst.aashish@gmail.com");
        // Adding a Student using Entity Manager - Saving Student
        entityManager.persist(collegeStudent);
        entityManager.flush();

        // Sending a Request
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void createStudentHttpRequest() throws Exception{
        // Adding a Student using Entity Manager - Initializing
        collegeStudent.setFirstname("Aashish");
        collegeStudent.setLastname("Bansal");
        collegeStudent.setEmailAddress("analyst.aashish@gmail.com");

        // Sending a POST Request to add a student to Database
        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(collegeStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Verifying using DAO
        CollegeStudent verifyStudent = studentDao.findByEmailAddress("analyst.aashish@gmail.com");
        assertNotNull(verifyStudent, "Student should be valid.");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception{
        // Checking if the Student exists
        assertTrue(studentDao.findById(1).isPresent(), "Student does not exist!");

        // Sending a DELETE Request
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(0)));

        // Verifying Deletion of student
        assertFalse(studentDao.findById(1).isPresent(), "Student was not deleted!");
    }

    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception{
        // Checking if the invalid student is present or not
        assertFalse(studentDao.findById(0).isPresent());

        // Setting expectation for JSON Error Response
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void studentInformationHttpRequest() throws Exception{
        // Obtaining Student Information
        Optional<CollegeStudent> student = studentDao.findById(1);

        // Verifying Student
        assertTrue(student.isPresent(), "Student is not present!");

        // Setting Expectations and obtaining JSON Response
        mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")));
    }

    @Test
    public void studentInformationHttpRequestEmptyResponse() throws Exception{
        // Obtaining Student Information
        Optional<CollegeStudent> student = studentDao.findById(0);

        // Verifying Student
        assertFalse(student.isPresent(), "Student is not present!");

        // Setting Expectations and obtaining JSON Response
        mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void createAValidGradeHttpRequest() throws Exception{
        this.mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "85.00")
                .param("gradeType", "math")
                .param("studentId", "1")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(2)));
    }

    @Test
    public void createAValidGradeHttpRequestStudentDoesNotExistEmptyResponse() throws Exception{
        this.mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "85.00")
                .param("gradeType", "math")
                .param("studentId", "0")
        ).andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found"))
        );
    }

    @Test
    public void createANonValidGradeHttpRequestGradeTypesDoesNotExistEmptyResponse() throws Exception{
        this.mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "literature")
                        .param("studentId", "0")
                ).andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found"))
                );
    }

    @Test
    public void deleteAValidGradeHttpRequest() throws Exception{
        // Obtaining the Grade
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1);
        // Verfying grade using assertion
        assertTrue(mathGrade.isPresent(), "Grade not present!");

        // Performing Request
        mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 1, "math"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(0)));

    }

    @Test
    public void deleteAValidGradeHttpRequestStudentIdDoesNotExistEmptyResponse() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/gradeType", 2, "history"))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void deleteANonValidGradeHttpRequest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/gradeType", 2, "literature"))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }
}
