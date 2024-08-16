package com.cbfacademy.restapiexercise.IOUS;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Indicates that this class is a Spring REST controller, meaning it handles HTTP requests.
@RequestMapping(path = "api/ious") // (Sets the base URL path for this controller)
public class IOUController { 

    private final IOUService iouService; // This is a reference to the service layer that handles the business logic

    @Autowired 
    public IOUController(IOUService iouService) { // dependency injection - Saying that the above IOUService iouService should be autowired/instansiated by Spring and injected into the below constructor when the application starts
        this.iouService = iouService; // **constructor for the iouController class. It accepts a IOUService object and assigns it to the iouService variable. This allows the controller to use the service to perform business logic.
    }

    @GetMapping // maps HTTP GET requests to this method. When someone sends a GET request to /api/ious/{id}, this method will be called.
    public List<IOU> getAllIOUs() { // method returns a list of all IOUs. It calls the getAllIOUs() method from IOUService to retrieve the list.
        return iouService.getAllIOUs(); // retrieves the list of IOUs from the service and sends it back as the response.
    }

    @GetMapping(path = "/{id}") // to retrieve speciific IOU by ID. mentor - different to demo, ensure you understand why
    public IOU getIOU(@PathVariable UUID id) {
        return iouService.getIOU(id);
    }

    // TO ADD A NEW IOU
    @PostMapping // maps HTTP POST requests to this method. When someone sends a POST request to /api/ious, this method will be called.
    // method registers a new IOU
    public void registerNewIOU (@RequestBody IOU iou) { // @RequestBody annotation tells Spring to take the JSON data sent in the request body and convert it into a IOU object.
        iouService.createIOU(iou);
    }

    @PutMapping(path = "/{id}") // for updating an existing IOU. mentor - different to demo, ensure you understand why
    public IOU updateIOU(@PathVariable UUID id, @RequestBody IOU updatedIOU) {
        return iouService.updateIOU(id, updatedIOU);
    }

    @DeleteMapping(path = "/api/ious/{id}") // maps HTTP DELETE requests to this method. The path = "/api/ious/{id}" part indicates that this method will respond to a URL that contains a Id (like /api/ious/{id}).
    public void deleteIOU(@PathVariable("id") UUID id) { // method deletes a student based on their ID. The @PathVariable annotation tells Spring to take the Id from the URL and pass it to this method.
        iouService.deleteIOU(id); // calls the deleteIOU() method from IOUService to delete the student from the database.
    }
}

