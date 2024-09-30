package com.cs7rishi.oFile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String pwd;

    private String role;

    @Column(name="create_dt")
    private String createDt;

    public Customer(String email, String pwd, String role) {
        this.email = email;
        this.pwd = pwd;
        this.role = role;
    }
}
