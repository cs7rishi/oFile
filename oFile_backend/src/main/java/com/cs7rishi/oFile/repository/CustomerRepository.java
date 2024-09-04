package com.cs7rishi.oFile.repository;

import com.cs7rishi.oFile.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer,Long> {
}
