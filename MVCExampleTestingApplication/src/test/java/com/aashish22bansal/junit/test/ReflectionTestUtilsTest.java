package com.aashish22bansal.junit.test;

import com.aashish22bansal.junit.test.models.CollegeStudent;
import com.aashish22bansal.junit.test.models.StudentGrades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = MvcExampleTestingApplication.class)
public class ReflectionTestUtilsTest {
    @Autowired
    ApplicationContext context;

    @Autowired
    CollegeStudent studentOne;

    @Autowired
    StudentGrades studentGrades;

    @BeforeEach
    public void studentBeforeEach(){
        studentOne.setFirstName("StudentFirstNameFour");
        studentOne.setLastName("studentLastNameFour");
        studentOne.setEmailAddress("studentFourEmail@gmail.com");
        studentOne.setStudentGrades(studentGrades);

        ReflectionTestUtils.setField(studentOne, "id", 4);
        ReflectionTestUtils.setField(
                studentOne,
                "studentGrades",
                new StudentGrades(
                        new ArrayList<>(
                                Arrays.asList(100.0, 85.0, 76.50, 91.75)
                        )
                )
        );
    }

    @Test
    public void getPrivateField(){
        // Reading the id field from the student
        assertEquals("IDs match", 4, ReflectionTestUtils.getField(studentOne, "id"));
    }

    @Test
    public void invokePrivateMethod(){
        assertEquals(
                "Content matches",
                "StudentFirstNameFour 4",
                ReflectionTestUtils.invokeMethod(studentOne, "getFirstNameAndId")
        );
    }
}
