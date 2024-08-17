package com.cbfacademy.restapiexercise.IOUS;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface IOURepository extends ListCrudRepository<IOU, UUID> {
    
    List<IOU> findByBorrower(String borrower); // Returning a list of IOUs for each borrower

}
