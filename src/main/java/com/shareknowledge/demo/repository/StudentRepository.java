package com.shareknowledge.demo.repository;

import com.shareknowledge.demo.enity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends MongoRepository<Student, Long> {
}
