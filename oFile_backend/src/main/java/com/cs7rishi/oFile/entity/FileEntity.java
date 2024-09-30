package com.cs7rishi.oFile.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long id;
    @Column(name="user_email")
    String userEmail;
    @Column(name="file_name")
    String fileName;
    @Column(name="file_url")
    String fileUrl;
    @ManyToOne
    Customer customer;
}
