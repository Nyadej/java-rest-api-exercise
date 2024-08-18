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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // Indicates that this class is a Spring REST controller, meaning it handles HTTP requests.
@RequestMapping(path = "/api/ious") // (Sets the base URL path for this controller)
public class IOUController { 

    private final IOUService iouService; // This is a reference to the service layer that handles the business logic

    @Autowired 
    public IOUController(IOUService iouService) { // dependency injection - Saying that the above IOUService iouService should be autowired/instansiated by Spring and injected into the below constructor when the application starts
        this.iouService = iouService; // **constructor for the iouController class. It accepts a IOUService object and assigns it to the iouService variable. This allows the controller to use the service to perform business logic.
    }

    // Mapped to the base path (/api/ious) and handles requests without any specific ID. It retrieves all IOUs.
    @GetMapping // maps HTTP GET requests to this method. When someone sends a GET request to /api/ious, this method will be called. Retrieves and returns a list of all IOU objects.***
    public List<IOU> getAllIOUs(@RequestParam(required = false) String borrower) { // method returns a list of all IOUs. It calls the getAllIOUs() method from IOUService to retrieve the list.
        if (borrower !=null) { // If the borrower is provided (not null)
            return iouService.getIOUsByBorrower(borrower); // return only IOUs that belong to this specific borrower
        } else { // If the borrower is not provided (null)
        return iouService.getAllIOUs(); // retrieves the list of ALL IOUs from the service and sends it back as the response in JSON format.
        }
    }

    // Mapped to a path that includes an ID (/api/ious/{id}). It retrieves a specific IOU by its ID.
    @GetMapping(path = "/{id}") // maps HTTP GET requests to this method. When someone sends a GET request to /api/ious/{id}, this method will be called. Retrieves and returns a specific IOU object by its unique ID.***
    public IOU getIOU(@PathVariable UUID id) { // @PathVariable UUID id annotation tells Spring to extract the id from the URL path and pass it as a parameter to the method. The method then calls iouService.getIOU(id) to fetch the IOU with the specified ID.
        return iouService.getIOU(id).orElseThrow(NoSuchElementException::new); // The specific IOU object is returned as the HTTP response in JSON format. Or an exception is thrown if the IOU cannot be found by its ID.
    }

    @GetMapping("/high") // Mapped to a path (/high). This then calls the getHighValueIOUs() method in the service layer to call the method in the repository and get the high value IOUs 
    public List<IOU> getHighValueIOUs() {
        return iouService.getHighValueIOUs();
    }

    @GetMapping("/low") // Mapped to a path (/low). This then calls the getBelowOrEqualValueIOUs() method in the service layer to call the method in the repository and get the below value IOUs or equal to the average value
    public List<IOU> getBelowOrEqualValueIOUs() {
        return iouService.getBelowOrEqualValueIOUs();
    }

    // TO ADD A NEW IOU
    @PostMapping // maps HTTP POST requests to this method. When someone sends a POST request to /api/ious, this method will be called.
    // method registers a new IOU
    public void registerNewIOU (@RequestBody IOU iou) { // @RequestBody annotation tells Spring to take the JSON data sent in the request body and convert it into a IOU object.
        iouService.createIOU(iou);
    }

    @PutMapping(path = "/{id}") // maps HTTP PUT requests for this method - for updating an existing IOU with a specified ID. 
    public IOU updateIOU(@PathVariable UUID id, @RequestBody IOU updatedIOU) { // @PathVariable UUID id annotation tells Spring to extract the id from the URL path and @ResponseBody tells the controller that the object/id returned is automatically serialised into JSON and then passed back as a parameter to the method. 
        return iouService.updateIOU(id, updatedIOU); // The method then calls iouService.updateIOU to fetch the IOU with the updated specified ID.
    }

    @DeleteMapping(path = "/{id}") // maps HTTP DELETE requests to this method. The path = "/api/ious/{id}" part indicates that this method will respond to a URL that contains a Id (like /api/ious/{id}).
    public void deleteIOU(@PathVariable("id") UUID id) { // method deletes a student based on their ID. The @PathVariable annotation tells Spring to take the Id from the URL and pass it to this method.
        iouService.deleteIOU(id); // calls the deleteIOU() method from IOUService to delete the student from the database.
    }

}

