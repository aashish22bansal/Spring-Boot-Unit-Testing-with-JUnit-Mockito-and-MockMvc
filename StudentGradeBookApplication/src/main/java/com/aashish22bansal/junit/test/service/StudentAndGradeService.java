package com.aashish22bansal.junit.test.service;

import com.aashish22bansal.junit.test.models.*;
import com.aashish22bansal.junit.test.repository.HistoryGradesDao;
import com.aashish22bansal.junit.test.repository.MathGradesDao;
import com.aashish22bansal.junit.test.repository.ScienceGradesDao;
import com.aashish22bansal.junit.test.repository.StudentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDAO studentDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private MathGradesDao mathGradesDao;

    @Autowired
    private ScienceGradesDao scienceGradesDao;

    @Autowired
    private HistoryGradesDao historyGradesDao;

    @Autowired
    private StudentGrades studentGrades;

    public void createStudent(String firstName, String lastName, String emailAddress){
        // Creating a new instance of CollegeStudent
        CollegeStudent student = new CollegeStudent(firstName, lastName, emailAddress);

        // Setting the ID to 0, since this is a new instance.
        student.setId(0);

        studentDao.save(student);
    }

    public boolean checkIfStudentIsNull(int id){
        // Retrieving the given id from database
        Optional<CollegeStudent> student = studentDao.findById(id);

        if(student.isPresent()){
            return true;
        }
        return false;
    }

    public void deleteStudent(int id){
        if(checkIfStudentIsNull(id)){
            studentDao.deleteById(id);
            mathGradesDao.deleteByStudentId(id);
            scienceGradesDao.deleteByStudentId(id);
            historyGradesDao.deleteByStudentId(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook(){
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();
        return collegeStudents;
    }

    public boolean createGrade(double grade, int studentId, String gradeType){
        // Checking if the Student exists
        if(!checkIfStudentIsNull(studentId)){
            return false;
        }
        // Checking if the Student Grade is valid
        if(grade >=0 && grade < 100){
            // Checking the GradeType
            if(gradeType.equals("math")){
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentId);
                // Saving the Grade using MathGradeDao
                mathGradesDao.save(mathGrade);
                return true;
            }
            else if (gradeType.equals("science")){
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(studentId);
                // Saving the Grade using ScienceGradeDao
                scienceGradesDao.save(scienceGrade);
                return true;
            }
            else if (gradeType.equals("history")) {
                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(studentId);
                // Saving the Grade using ScienceGradeDao
                historyGradesDao.save(historyGrade);
                return true;
            }
            return false;
        }
        return false;
    }

    public int deleteGrade(int id, String gradeType){
        // Initial Declaration
        int studentId = 0;
        // Checking the Grade Type
        if(gradeType.equals("math")){
            Optional<MathGrade> grade = mathGradesDao.findById(id);
            // If the grade is not present, then
            if(!grade.isPresent()){
                return studentId;
                // Return studentId of 0 because we didn't find it
            }
            // Obtaining the actual Student ID using Grade
            studentId = grade.get().getStudentId();
            // Deleting the Grade
            mathGradesDao.deleteById(id);
        }
        else if(gradeType.equals("science")){
            Optional<ScienceGrade> grade = scienceGradesDao.findById(id);
            // If the grade is not present, then
            if(!grade.isPresent()){
                return studentId;
                // Return studentId of 0 because we didn't find it
            }
            // Obtaining the actual Student ID using Grade
            studentId = grade.get().getStudentId();
            // Deleting the Grade
            scienceGradesDao.deleteById(id);
        }
        else if(gradeType.equals("history")){
            Optional<HistoryGrade> grade = historyGradesDao.findById(id);
            // If the grade is not present, then
            if(!grade.isPresent()){
                return studentId;
                // Return studentId of 0 because we didn't find it
            }
            // Obtaining the actual Student ID using Grade
            studentId = grade.get().getStudentId();
            // Deleting the Grade
            historyGradesDao.deleteById(id);
        }
        return studentId;
    }

    public GradebookCollegeStudent studentInformation(int id){
        // Checking if Student Exists
        if(!checkIfStudentIsNull(id)){
            return null;
        }

        // Retrieving the Student from the Database
        Optional<CollegeStudent> student = studentDao.findById(id);

        // Retrieving the Student Grades
        Iterable<MathGrade> mathGrades = mathGradesDao.findGradeByStudentId(id);
        Iterable<ScienceGrade> scienceGrades = scienceGradesDao.findGradeByStudentId(id);
        Iterable<HistoryGrade> historyGrades = historyGradesDao.findGradeByStudentId(id);

        // Converting Iterable to a List
        /**
         * First, we create an empty List and then we loop over the grades for the subject.
         * The :: Operator is the Method Reference Operator which, behind the scenes, will
         * call the add() method on the Grades List for the given item. So, basically, we
         * are looping through the Iterable and then adding them to the list.
         */
        List<Grade> mathGradesList = new ArrayList<>();
        mathGradesList.forEach(mathGradesList::add);
        List<Grade> scienceGradesList = new ArrayList<>();
        scienceGradesList.forEach(scienceGradesList::add);
        List<Grade> historyGradesList = new ArrayList<>();
        historyGradesList.forEach(historyGradesList::add);

        // Set the values for the Grades
        studentGrades.setMathGradeResults(mathGradesList);
        studentGrades.setScienceGradeResults(scienceGradesList);
        studentGrades.setHistoryGradeResults(historyGradesList);

        // Creating Instance of GradebookCollegeStudent and populating with appropriate
        // data (Student ID, Firstname, Lastname, etc.)
        GradebookCollegeStudent gradebookCollegeStudent;
        gradebookCollegeStudent = new GradebookCollegeStudent(
                student.get().getId(),
                student.get().getFirstname(),
                student.get().getLastname(),
                student.get().getEmailAddress(),
                studentGrades
        );

        return gradebookCollegeStudent;
    }
}
