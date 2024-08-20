package com.cbfacademy.restapiexercise.ious;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Description;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.cbfacademy.restapiexercise.RestApiExerciseApplication;
import com.cbfacademy.restapiexercise.IOUS.IOU;
import com.cbfacademy.restapiexercise.IOUS.IOUService;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = RestApiExerciseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // This configuration allows the test to run on a random port, ensuring that it doesn’t conflict with other tests or services.
public class IOUControllerTest {

	@LocalServerPort // annotation injects the random port number used by the test.
	private int port;

	private URI baseURI; // variable will store the base URI of the API endpoints.

	@Autowired
	private TestRestTemplate restTemplate; // This injects a TestRestTemplate instance, which is a utility class for testing RESTful services.

	private List<IOU> defaultIOUs = new ArrayList<>() { // A list of predefined IOU objects is created to be used as mock data for testing.
		{
			add(new IOU("John", "Alice", new BigDecimal("100.00"), getInstant(24)));
			add(new IOU("Bob", "Eve", new BigDecimal("50.00"), getInstant(48)));
			add(new IOU("Charlie", "Grace", new BigDecimal("200.00"), getInstant(72)));
		}
	}

	@MockBean // This creates a mock version of the IOUService so that you can simulate its behavior without actually calling the real service.
	private IOUService iouService;

    // This method initializes the base URI used to make requests to the API and sets up the mock behavior for iouService.getAllIOUs() to return predefined IOU objects.
	@BeforeEach // This method runs before each test, setting up common configurations.
	void setUp() throws RuntimeException {
        // Builds the base URI for the API endpoint by combining the scheme (http), host (localhost),
        // port (which is injected by Spring), and the path (api/ious).
		this.baseURI = UriComponentsBuilder.newInstance() // Constructs the base URI for the API endpoints
				.scheme("http")
				.host("localhost")
				.port(port)
				.path("api/ious")
				.build()
				.toUri();

        // Mock the behavior of the getAllIOUs() method in the IOUService to return the defaultIOUs list.
		when(iouService.getAllIOUs()).thenReturn(defaultIOUs);
	}

    // This test checks if the API correctly creates a new IOU when a POST request is made.
	@Test
	@Description("POST /api/ious creates new IOU")
	void createIOU() {
		// Arrange: Create a new IOU object for the test.
		IOU iou = createNewIOU();

        // Mock the behavior of the createIOU method to return the IOU passed to it.
		when(iouService.createIOU(any(IOU.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act: Send a POST request to create a new IOU and store the response.
		ResponseEntity<IOU> response = restTemplate.postForEntity(baseURI.toString(), iou, IOU.class);

		// Assert: Check if the HTTP status is 201 (Created) and if the response body is not null.
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getId());

        // Verify that the createIOU method was called.
		verify(iouService).createIOU(any(IOU.class));
	}

    // This test checks if the API correctly returns all IOU objects when a GET request is made.
	@Test
	@Description("GET /api/ious returns all IOUs")
	void getAllIOUs() throws URISyntaxException {
		// Act: Send a GET request to retrieve all IOUs.
		ResponseEntity<List<IOU>> response = restTemplate.exchange(baseURI, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<IOU>>() {
				});

        // Extract the list of IOUs from the response.
		List<IOU> responseIOUs = response.getBody();

		// Assert: Check if the status is 200 (OK) and the response list matches the default list.
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(responseIOUs);
		assertEquals(defaultIOUs.size(), responseIOUs.size());

        // Verify that the getAllIOUs method was called.
		verify(iouService).getAllIOUs();
	}

    // This test checks if the API correctly returns a specific IOU when a GET request is made using an ID.
	@Test
	@Description("GET /api/ious/{id} returns matching IOU")
	void getIOUById() {
		// Arrange: Select a random IOU from the default list.
		IOU iou = selectRandomIOU();
		URI endpoint = getEndpoint(iou);

        // Mock the behavior of the getIOU method to return the selected IOU.
		when(iouService.getIOU(any(UUID.class))).thenReturn(iou);

		// Act: Send a GET request to retrieve the IOU by its ID.
		ResponseEntity<IOU> response = restTemplate.getForEntity(endpoint, IOU.class);

		// Assert: Check if the status is 200 (OK) and the ID of the returned IOU matches the expected one.
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(iou.getId(), response.getBody().getId());

        // Verify that the getIOU method was called with the correct ID.
		verify(iouService).getIOU(iou.getId());
	}

    // This test checks if the API returns a 404 status when trying to retrieve an IOU that doesn’t exist.
	@Test
	@Description("GET /api/ious/{id} returns 404 for invalid IOU")
	void getInvalidIOU() {
		// Arrange: Create a new IOU and build the endpoint URI.
		IOU iou = createNewIOU();
		URI endpoint = getEndpoint(iou);

        // Mock the behavior of the getIOU method to throw a NoSuchElementException for an invalid ID.
		when(iouService.getIOU(any(UUID.class))).thenThrow(NoSuchElementException.class);

		// Act: Send a GET request to retrieve the IOU, expecting it to fail.
		ResponseEntity<IOU> response = restTemplate.getForEntity(endpoint, IOU.class);

		// Assert: Check if the status is 404 (Not Found).
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Verify that the getIOU method was called with the correct ID.
		verify(iouService).getIOU(iou.getId());
	}

    // This test checks if the API correctly updates an IOU when a PUT request is made.
	@Test
    @Description("PUT /api/ious/{id} updates matching IOU")
    void updateIOU() {
    // Arrange: Select a random IOU and build the endpoint URI.
    IOU iou = selectRandomIOU();
    URI endpoint = getEndpoint(iou);

    // Mock the behavior of getIOU and updateIOU methods.
    when(iouService.getIOU(any(UUID.class))).thenReturn(iou);
    when(iouService.updateIOU(any(UUID.class), any(IOU.class))).thenReturn(iou);

    // Act: Update the IOU's lender and send a PUT request to update the IOU.
    iou.setLender("UpdatedLender");
    restTemplate.put(endpoint, iou);

    // Retrieve the updated IOU to verify the changes.
    ResponseEntity<IOU> response = restTemplate.getForEntity(endpoint, IOU.class);
    IOU updatedIOU = response.getBody();

    // Assert: Check if the update was successful and the lender field was updated.
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(iou.getId(), updatedIOU.getId());
    assertEquals("UpdatedLender", updatedIOU.getLender());

    // Verify that the getIOU and updateIOU methods were called.
    verify(iouService).getIOU(iou.getId());
    verify(iouService).updateIOU(any(UUID.class), any(IOU.class));
}

    // This test checks if the API returns a 404 status when trying to update an IOU that doesn’t exist.
	@Test
    @Description("PUT /api/ious/{id} returns 404 for invalid IOU")
    void updateInvalidIOU() {
    // Arrange: Create a new IOU and build the endpoint URI.
    IOU iou = createNewIOU();
    URI endpoint = getEndpoint(iou);

    // Mock the behavior of the updateIOU method to throw a NoSuchElementException for an invalid ID.
    when(iouService.updateIOU(any(UUID.class), any(IOU.class)))
            .thenThrow(new NoSuchElementException("IOU not found"));

    // Act: Send a PUT request to update the IOU, expecting it to fail.
    RequestEntity<IOU> request = RequestEntity.put(endpoint).accept(MediaType.APPLICATION_JSON).body(iou);
    ResponseEntity<IOU> response = restTemplate.exchange(request, IOU.class);

    // Assert: Check if the status is 404 (Not Found).
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    // Verify that the updateIOU method was called.
    verify(iouService).updateIOU(any(UUID.class), any(IOU.class));
}

    // This test checks if the API correctly deletes an IOU when a DELETE request is made, and verifies that attempting to retrieve the deleted IOU returns a 404 status.
	@Test
    @Description("DELETE /api/ious/{id} deletes matching IOU")
    void deleteIOU() {
    // Arrange: Select a random IOU and build the endpoint URI.
    IOU iou = selectRandomIOU();
    URI endpoint = getEndpoint(iou);

    // Mock the behavior of getIOU and deleteIOU methods.
    when(iouService.getIOU(any(UUID.class))).thenReturn(iou);
    doAnswer(invocation -> null).when(iouService).deleteIOU(any(UUID.class));
    when(iouService.getIOU(any(UUID.class))).thenThrow(NoSuchElementException.class);

    // Act: Send a DELETE request to delete the IOU.
    RequestEntity<?> request = RequestEntity.delete(endpoint).accept(MediaType.APPLICATION_JSON).build();
    ResponseEntity<?> deletionResponse = restTemplate.exchange(request, Object.class);
    
    // Try to retrieve the IOU again to check if it was successfully deleted.
    ResponseEntity<IOU> deletedResponse = restTemplate.getForEntity(endpoint, IOU.class);

    // Assert: Check that the initial GET request returned OK, and the DELETE request returned OK or NO_CONTENT.
    assertEquals(HttpStatus.OK, deletionResponse.getStatusCode());
    // Check that trying to retrieve the IOU after deletion returns NOT_FOUND.
    assertEquals(HttpStatus.NOT_FOUND, deletedResponse.getStatusCode());
    
    // Verify that the deleteIOU method was called with the correct ID.
    verify(iouService).deleteIOU(iou.getId());
}

    // This test checks if the API returns a 404 status when attempting to delete an IOU that doesn’t exist.
	@Test
    @Description("DELETE /api/ious/{id} returns 404 for invalid IOU")
    void deleteInvalidIOU() {
    // Arrange: Create a new IOU and build the endpoint URI.
    IOU iou = createNewIOU();
    URI endpoint = getEndpoint(iou);

    // Mock the behavior of deleteIOU to throw NoSuchElementException for an invalid ID.
    doThrow(new NoSuchElementException("IOU not found")).when(iouService).deleteIOU(any(UUID.class));

    // Act: Send a DELETE request to delete the IOU, expecting it to fail.
    RequestEntity<?> request = RequestEntity.delete(endpoint).accept(MediaType.APPLICATION_JSON).build();
    ResponseEntity<IOU> response = restTemplate.exchange(request, IOU.class);

    // Assert: Check that the status is 404 (Not Found).
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    // Verify that the deleteIOU method was called with the correct ID.
    verify(iouService).deleteIOU(iou.getId());
}

    // This method selects a random IOU from the predefined list of IOU objects.
    private IOU selectRandomIOU() {
    // Randomly selects an IOU from the defaultIOUs list.
    int randomIndex = new Random().nextInt(defaultIOUs.size());
    return defaultIOUs.get(randomIndex);
}

    // This method creates a new IOU object with predefined values.
    private IOU createNewIOU() {
    // Creates a new IOU with a lender "John", borrower "Alice", amount 100.00, and current time.
    return new IOU("John", "Alice", new BigDecimal("100.00"), getInstant(0));
}

    // This method constructs the URI for accessing a specific IOU by its ID.
    private URI getEndpoint(IOU iou) {
    // Constructs the URI for a specific IOU by appending the IOU's ID to the base URI.
    return appendPath(baseURI, iou.getId().toString());
}

    // This method returns the current time with a specified number of hours subtracted.
    private Instant getInstant(int hoursToSubtract) {
    // Gets the current date and time in the system's default time zone.
    ZoneId systemTimeZone = ZoneId.systemDefault();
    ZonedDateTime currentDateTime = ZonedDateTime.now(systemTimeZone);

    // Subtracts the specified number of hours from the current time.
    Duration duration = Duration.ofHours(hoursToSubtract);
    ZonedDateTime resultDateTime = currentDateTime.minus(duration);

    // Converts the result to an Instant object.
    return resultDateTime.toInstant();
}

    // This method appends a path segment to the base URI and returns the complete URI.
    private URI appendPath(URI uri, String path) {
    // Appends a path segment (such as an IOU ID) to a base URI.
    return UriComponentsBuilder.fromUri(uri).pathSegment(path).build().encode().toUri();
}
}
