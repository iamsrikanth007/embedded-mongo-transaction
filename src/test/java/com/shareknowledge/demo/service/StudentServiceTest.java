package com.shareknowledge.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.shareknowledge.demo.dto.StudentModel;
import com.shareknowledge.demo.repository.StudentRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentServiceTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoClient mongoClient;

    @Test
    @Order(0)
    void init() throws InterruptedException {
        Thread.sleep(10000);
        MongoDatabase db = mongoClient.getDatabase("demo");
        db.createCollection("students");
    }

    @Test
    @Order(1)
    void saveStudentTest() throws JsonProcessingException {
        Long id = saveStudent();
        assert (studentRepository.findAll().size() == 1);
    }

    @Test
    @Order(2)
    void getStudentTest() throws JsonProcessingException {
        Long id = saveStudent();
        StudentModel studentModel = studentService.getStudent(id);
        assert (studentRepository.findAll().size() == 2);
    }

    @Test
    @Order(3)
    void deleteStudentTest() throws JsonProcessingException {
        Long id = saveStudent();
        studentService.deleteStudent(id);
        assert (studentRepository.findAll().size() == 2);
    }

    private String getRequestBody() {
        String body = "{\n" +
                "    \"fullName\": \"Srikanth\",\n" +
                "    \"email\": \"sri@sri.com\",\n" +
                "    \"address\": {\n" +
                "        \"street\": \"official colony\",\n" +
                "        \"dno\": \"845\"\n" +
                "    }\n" +
                "}";
        return body;
    }

    private Long saveStudent() throws JsonProcessingException {
        StudentModel studentModel = objectMapper.readValue(getRequestBody(), StudentModel.class);
        studentModel = studentService.saveStudent(studentModel);
        return studentModel.getId();
    }
}
