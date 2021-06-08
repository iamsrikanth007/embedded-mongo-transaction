package com.shareknowledge.demo.enity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Document(collection = "students")
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id should not be null")
    @Id
    private Long id;
    @NotBlank(message = "fullName should not be blank")
    private String fullName;
    @NotBlank(message = "email should not be blank")
    private String email;
    @NotEmpty(message = "address should not be empty")
    private Map<String, Object> address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Object> getAddress() {
        return address;
    }

    public void setAddress(Map<String, Object> address) {
        this.address = address;
    }
}
