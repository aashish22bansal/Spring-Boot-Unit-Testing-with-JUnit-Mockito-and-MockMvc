package com.aashish22bansal.junit.test;

import com.aashish22bansal.junit.test.dao.ApplicationDao;
import com.aashish22bansal.junit.test.models.CollegeStudent;
import com.aashish22bansal.junit.test.service.ApplicationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
public class MvcExampleTestingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MvcExampleTestingApplication.class, args);
	}

	@Bean(name = "applicationExample")
	ApplicationService getApplicationService(){
		return new ApplicationService();
	}

	@Bean(name = "applicationDao")
	ApplicationDao getApplicationDao(){
		return new ApplicationDao();
	}

	@Bean(name = "collegeStudent")
	@Scope(value = "prototype")
	CollegeStudent getCollegeStudent(){
		return new CollegeStudent();
	}

}
