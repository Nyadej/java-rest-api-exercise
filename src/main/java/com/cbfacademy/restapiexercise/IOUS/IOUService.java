package com.cbfacademy.restapiexercise.IOUS;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service // Service component in Spring, containing business logic to define what should happen when certain actions are requested.
public class IOUService  {

    private final IOURepository iouRepository; // will be used to interact with the database
    
    @Autowired // Tells spring to automatically inject an instance of IOURepository into the service class when the application starts
    public IOUService(IOURepository iouRepository) { // Constructor that accepts IOURepository object 
        this.iouRepository = iouRepository; // and assigns it to the iouRepository variable
    }

    public List<IOU> getAllIOUs() { // Return a list of all IOUs from the iouRepository
        return iouRepository.findAll(); // Retrieves IOUs from the database and returns them as a list
    }

    public Optional<IOU> getIOU(UUID id) throws NoSuchElementException  { // 
        return iouRepository.findById(id);
    }
    
    public void createIOU(IOU iou) throws IllegalArgumentException, OptimisticLockingFailureException { 
        iouRepository.save(iou);
    }

    IOU updateIOU(UUID id, IOU updatedIOU) throws NoSuchElementException {
        if (iouRepository.existsById(id)) { // Checking if there is an pre-existing IOU with this ID, if there isn't the following line is
            updatedIOU.setId(id); // updating the IOU with the new ID
            return iouRepository.save(updatedIOU); // Saving the new ID to an updated IOU and returning the updated IOU
        }

        return null; 
    }

    public void deleteIOU(UUID id) { // Method to delete a student from the database using their ID
        boolean exists = iouRepository.existsById(id); // Checks whether a Id exists in the database 
        if (!exists) { // If the Id does not exists the following code block is executed
            throw new IllegalStateException("The following ID " + id + " does not exist"); // Throws an exception, signalling that the ID cannot be deleted because it doesn't exist in the database
        }

        iouRepository.deleteById(id); // If the ID exists, this line deletes it from the database
    }
    
}
