package com.cbfacademy.restapiexercise.IOUS;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOURepository extends ListCrudRepository<IOU, UUID> {
    
    List<IOU> findByBorrower(String borrower); // Returning a list of IOUs for each borrower

    @Query(value = "SELECT * FROM ious WHERE ious.value > (SELECT AVG(ious.value) FROM ious)", nativeQuery = true)
    List<IOU> findHighValueIOUs();

}
