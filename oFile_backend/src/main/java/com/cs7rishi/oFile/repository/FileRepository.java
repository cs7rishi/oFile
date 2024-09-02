package com.cs7rishi.oFile.repository;

import com.cs7rishi.oFile.entity.FileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends CrudRepository<FileEntity, Long> {
   
}
