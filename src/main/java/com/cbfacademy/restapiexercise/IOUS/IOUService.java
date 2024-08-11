package com.cbfacademy.restapiexercise.IOUS;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service // Service component in Spring, containing business logic to define what should happen when certain actions are requested.
public class IOUService  {

    private final List<IOU> iouRepository; // will be used to interact with the database
    
    @Autowired // Tells spring to automatically inject an instance of IOURepository into the service class when the application starts
    public IOUService(IOURepository iouRepository) { // Constructor that accepts IOURepository object 
        this.iouRepository = iouRepository; // and assigns it to the iouRepository variable
    }

    public List<IOU> getAllIOUs() { // Return a list of all IOUs from the iouRepository
        return iouRepository; // Retrieves IOUs from the database and returns them as a list
    }

    public void getIOU(UUID id) {
        if (!exists) {
            throw new NoSuchElementException ("The following ID " + id + " does not exist");
        }
    }
    
    public void createIOU(IOU iou) { // Method creates a new IOU and adds it to the database
        Optional<IOU> iouOptional = iouRepository.findIOUById(iou.getId()); // Checks if an IOU with the ID already exists in the database 
        if (iouOptional.isPresent()) { // If a IOU with the same ID is found, the following code block will be executed
            throw new IllegalArgumentException ("Optimistic Locking Failure Exception - this IOU has already been created"); // Throws an exception, which stops the method and signals that the IOU cannot be added because the ID is already taken(?)
        }

        iouRepository.save(iou); // Saves the new IOU to the database if it doesn't already exist
    } 

    IOU updateIOU(UUID id, IOU updatedIOU) throws NoSuchElementException

    public void deleteIOU(UUID id) { // Method to delete a student from the database using their ID
        boolean exists = iouRepository.existsById(id); // Checks whether a Id exists in the database 
        if (!exists) { // If the Id does not exists the following code block is executed
            throw new IllegalStateException("The following ID " + id + " does not exist"); // Throws an exception, signalling that the ID cannot be deleted because it doesn't exist in the database
        }

        iouRepository.deleteById(id); // If the ID exists, this line deletes it from the database
    }
}
