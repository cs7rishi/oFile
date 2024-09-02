package com.cs7rishi.oFile.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "file_detail")
@Getter
@Setter
@NoArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    Long id;
    @Column(name="user_id")
    String userId;
    @Column(name="file_name")
    String fileName;
    @Column(name="file_url")
    String fileUrl;

    public FileEntity(String fileUrl) {
        this.fileUrl = fileUrl;
        this.fileName = fileUrl;
    }
}
