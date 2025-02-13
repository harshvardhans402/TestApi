package org.example;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.pojos.User;
import org.example.utility.JSONUtility;
import org.testng.Assert;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import com.aventstack.extentreports.Status;

import java.util.List;
import java.lang.reflect.Method;

import static io.restassured.RestAssured.*;

public class ApiTests {

    List<User> users;

    // ExtentReports objects
    private static ExtentReports extent;
    private static ExtentTest test;
    private static ExtentSparkReporter htmlReporter;

    @BeforeClass
    public static void setupReport() {
        // Create an instance of the ExtentHtmlReporter and link it to ExtentReports
        htmlReporter = new ExtentSparkReporter("test-report.html");  // Save report to this file
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    @BeforeMethod
    public void beforeTest(Method method) {
        // Create an ExtentTest for each test method and start logging
        test = extent.createTest(method.getName());
    }

    @AfterMethod
    public void afterTest() {
        // Mark the test as passed or failed based on assertions in the test
        extent.flush();  // Ensure the test results are saved
    }

    @BeforeClass
    public void setup() {
        users = JSONUtility.readJSON("src/test/java/org/example/UserData.json", User.class);
    }

    @Test
    public void testGetRequestValidation() {
        test.log(Status.INFO, "Starting GET Request Validation Test");

        Response response = given()
                .when()
                .get("https://reqres.in/api/users?page=1")
                .then()
                .statusCode(200)
                .extract()
                .response();

        test.log(Status.INFO, "Received Response: " + response.jsonPath().prettyPrint());

        try {
            Assert.assertTrue(!response.jsonPath().getList("data").isEmpty());
            List<String> l = response.jsonPath().getList("data");
            int per_page = response.jsonPath().get("per_page");
            Assert.assertEquals(l.size(), per_page);
            test.pass("Test Passed");
        } catch (AssertionError e) {
            test.fail("Test Failed due to AssertionError: " + e.getMessage());
        }
    }

    @Test
    public void testPostRequestCreateUser() {
        test.log(Status.INFO, "Starting POST Request - Create User Test");

        Response response = given()
                .header("Content-Type", "application/json")
                .body(users.get(0))
                .when()
                .post("https://reqres.in/api/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String id = response.jsonPath().getString("id");
        String createdAt = response.jsonPath().getString("createdAt");
        System.out.println("id: " + id);
        System.out.println("createdAt: " + createdAt);

        try {
            Assert.assertNotNull(id);
            Assert.assertNotNull(createdAt);
            test.pass("User Created Successfully: ID - " + id);
        } catch (AssertionError e) {
            test.fail("Test Failed due to AssertionError: " + e.getMessage());
        }
    }

    @Test
    public void testPutRequestUpdateUser() {
        test.log(Status.INFO, "Starting PUT Request - Update User Test");

        User updateUser = users.get(0);
        updateUser.setFirst_name("Rajeev");

        Response response = given()
                .header("Content-Type", "application/json")
                .body(updateUser)
                .when()
                .put("https://reqres.in/api/users/2")
                .then()
                .statusCode(200)
                .extract()
                .response();

        test.log(Status.INFO, "Received Response: " + response.jsonPath().prettyPrint());
        String first_name = response.jsonPath().getString("first_name");
        String last_name = response.jsonPath().getString("last_name");

        try {
            Assert.assertEquals(first_name, updateUser.getFirst_name());
            Assert.assertEquals(last_name, updateUser.getLast_name());
            test.pass("User updated successfully with first name: " + first_name);
        } catch (AssertionError e) {
            test.fail("Test Failed due to AssertionError: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteRequestDeleteUser() {
        test.log(Status.INFO, "Starting DELETE Request - Delete User Test");

        Response response = given()
                .when()
                .delete("https://reqres.in/api/users/2")
                .then()
                .statusCode(204)
                .extract()
                .response();

        try {
            Assert.assertEquals(response.getBody().asString(), "");
            test.pass("User Deleted Successfully");
        } catch (AssertionError e) {
            test.fail("Test Failed due to AssertionError: " + e.getMessage());
        }
    }

    @Test
    public void testGetRequestValidateJsonSchema() {
        test.log(Status.INFO, "Starting GET Request to Validate JSON Schema");

        Response response = given()
                .when()
                .get("https://reqres.in/api/users/2")
                .then()
                .statusCode(200)
                .extract()
                .response();

        test.log(Status.INFO, "Received Response: " + response.jsonPath().prettyPrint());

        try {
            Assert.assertNotNull(response.jsonPath().get("data.id"));
            Assert.assertNotNull(response.jsonPath().get("data.email"));
            Assert.assertNotNull(response.jsonPath().get("data.first_name"));
            Assert.assertNotNull(response.jsonPath().get("data.last_name"));
            test.pass("JSON schema validated successfully.");
        } catch (AssertionError e) {
            test.fail("Test Failed due to AssertionError: " + e.getMessage());
        }
    }

    @Test
    public void testLoginRequest() {
        test.log(Status.INFO, "Starting POST Request - Login User Test");

        String loginBody = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = response.jsonPath().getString("token");

        try {
            Assert.assertNotNull(token);
            test.pass("Login successful, token: " + token);
        } catch (AssertionError e) {
            test.fail("Test Failed due to AssertionError: " + e.getMessage());
        }
    }

    @Test
    public void testUnauthorizedRequest() {
        test.log(Status.INFO, "Starting Unauthorized Request Test");

        // Simulating an unauthorized API request
        String loginBody = "{ \"email\": \"wrong.email@reqres.in\", \"password\": \"wrongpassword\" }";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .statusCode(400)  // Expecting a 400 Bad Request error due to incorrect credentials
                .extract()
                .response();

        try {
            Assert.assertEquals(response.statusCode(), 400);
            test.pass("Unauthorized request returned status 400 as expected.");
        } catch (AssertionError e) {
            test.fail("Test Failed due to AssertionError: " + e.getMessage());
        }
    }

    @AfterClass
    public static void teardownReport() {
        // Flush the ExtentReports instance to save the report
        extent.flush();
    }
}
