package com.example.bookstore.model;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.context.support.BeanDefinitionDsl;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue
    private long id; //will manually create user so no need to generate id

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role;

}
