package com.shareknowledge.demo.controller;

import com.shareknowledge.demo.dto.StudentModel;
import com.shareknowledge.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    StudentModel saveTenant(@RequestBody StudentModel studentModel) {
        return studentService.saveStudent(studentModel);
    }

    @GetMapping("/{id}")
    StudentModel getStudentById(@PathVariable("id") Long id) {
        return studentService.getStudent(id);
    }

    @DeleteMapping("/{id}")
    void deleteStudentById(@PathVariable("id") Long id) {
        studentService.deleteStudent(id);
    }
}
