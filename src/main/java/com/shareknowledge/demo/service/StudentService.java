package com.shareknowledge.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shareknowledge.demo.dto.StudentModel;
import com.shareknowledge.demo.enity.Student;
import com.shareknowledge.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public StudentModel saveStudent(StudentModel studentModel) {
        try {
            Student student = objectMapper.convertValue(studentModel, Student.class);
            if (student.getId() == null || student.getId() == 0) {
                student.setId(Instant.now().toEpochMilli());
            }
            student = studentRepository.save(student);
            studentModel = objectMapper.convertValue(student, StudentModel.class);
            return studentModel;
        } catch (Exception e) {
            throw e;
        }
    }

    public StudentModel getStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found with id : " + id));
        return objectMapper.convertValue(student, StudentModel.class);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found with id : " + id));
        studentRepository.delete(student);
    }
}
