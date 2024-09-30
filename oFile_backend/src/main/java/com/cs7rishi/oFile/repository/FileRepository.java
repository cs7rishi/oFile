package com.cs7rishi.oFile.repository;

import com.cs7rishi.oFile.entity.Customer;
import com.cs7rishi.oFile.entity.FileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends CrudRepository<FileEntity, Long> {
    List<FileEntity> findByCustomer(Customer customer);
}
