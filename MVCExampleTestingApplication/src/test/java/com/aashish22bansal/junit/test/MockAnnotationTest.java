package com.aashish22bansal.junit.test;

import com.aashish22bansal.junit.test.dao.ApplicationDao;
import com.aashish22bansal.junit.test.models.CollegeStudent;
import com.aashish22bansal.junit.test.models.StudentGrades;
import com.aashish22bansal.junit.test.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

//import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MvcExampleTestingApplication.class)
public class MockAnnotationTest {
    @Autowired
    ApplicationContext context;

    @Autowired
    CollegeStudent studentOne;

    @Autowired
    StudentGrades studentGrades;

    // Setting up the Test Double
    // @Mock
    @MockBean
    private ApplicationDao applicationDao;

    // @InjectMocks
    @Autowired
    private ApplicationService applicationService;

    @BeforeEach
    public void beforeEach(){
        studentOne.setFirstName("StudentFirstNameThree");
        studentOne.setLastName("StudentLastNameThree");
        studentOne.setEmailAddress("studentthree@gmail.com");
        studentOne.setStudentGrades(studentGrades);
    }

    @DisplayName("When & Verify")
    @Test
    public void assertEqualsTestAddGrades(){
        /**
         * Setting up expectations with Mock Responses.
         * In this, when Math Grade Results are passed from studentGrades to applicationDao, then return 100.00
         * When addGradeResultsForSingleClass() method is called, then return 100.00
         * So, we are just setting up a Mock Response.
         */
        when(
                applicationDao.addGradeResultsForSingleClass(
                        studentGrades.getMathGradeResults()
                )
        ).thenReturn(100.00);

        // Setting up Asserts
        assertEquals(applicationService.addGradeResultsForSingleClass(
                    studentOne.getStudentGrades().getMathGradeResults()
                ), 100
        );

        // Verifying the called method
        verify(applicationDao).addGradeResultsForSingleClass(
                studentGrades.getMathGradeResults()
        );

        // Verifying the number of times method is called
        verify(applicationDao, times(1)).addGradeResultsForSingleClass(
                studentGrades.getMathGradeResults()
        );
    }

    @DisplayName("Find Gpa")
    @Test
    public void assertEqualsTestFindGpa(){
        when(
                applicationDao.findGradePointAverage(
                        studentGrades.getMathGradeResults()
                )
        ).thenReturn(88.31);

        assertEquals(88.31, applicationService.findGradePointAverage(
                                        studentOne.getStudentGrades().getMathGradeResults()
                                    )
        );
    }

    @DisplayName("Not Null")
    @Test
    public void testAssertNotNull(){
        when(applicationDao.checkNull(studentGrades.getMathGradeResults())).thenReturn(true);

        assertNotNull(
                applicationService.checkNull(
                        studentOne.getStudentGrades().getMathGradeResults()
                ),
                "Object should not be null"
        );
    }

    @DisplayName("Throw runtime exception")
    @Test
    public void throwRuntimeError(){
        // Retrieving a College Student from Application Context
        CollegeStudent nullStudent = (CollegeStudent) context.getBean("collegeStudent");

        // Setting up expectation to throw an exception
        doThrow(new RuntimeException()).when(applicationDao).checkNull(nullStudent);

        // Performing Assertions
        assertThrows(RuntimeException.class, () -> {
            applicationService.checkNull(nullStudent);
        });

        // Verifying the number of executions
        verify(applicationDao, times(1)).checkNull(nullStudent);
    }

    @DisplayName("Multiple Stubbing")
    @Test
    public void stubbingConsecutiveCalls(){
        CollegeStudent nullStudent = (CollegeStudent) context.getBean("collegeStudent");

        // Setting up expectations
        when(applicationDao.checkNull(nullStudent))
                .thenThrow(new RuntimeException())   // First Call
                .thenReturn("Do not throw Exception second time."); // Second Call

        // Assertions for the expectations
        assertThrows(RuntimeException.class, () -> {
            applicationService.checkNull(nullStudent);
        });
        assertEquals(
                "Do not throw Exception second time.",
                applicationService.checkNull(nullStudent)
        );

        // Verifying the number of method calls
        verify(applicationDao, times(2)).checkNull(nullStudent);
    }
}
