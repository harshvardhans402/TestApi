package org.example;



import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.pojos.User;
import org.example.utility.JSONUtility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;

    public class ApiTests {

        List<User> users;

        @BeforeClass
        public void setup(){
            users= JSONUtility.readJSON("src/test/java/org/example/UserData.json", User.class);

        }


        @Test
        public void testGetRequestValidation() {
            Response response = given()
                    .when()
                    .get("https://reqres.in/api/users?page=1")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            response.jsonPath().prettyPrint();

            Assert.assertTrue(!response.jsonPath().getList("data").isEmpty());
            List<String> l=response.jsonPath().getList("data");
            int per_page=response.jsonPath().get("per_page");
            Assert.assertEquals(l.size(),per_page);
//            Assert.assertNotNull(response.jsonPath().get("total"));
        }

        @Test
        public void testPostRequestCreateUser() {

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
            System.out.println("createdAt: "+createdAt);
            Assert.assertNotNull(id);
            Assert.assertNotNull(createdAt);
        }

        @Test
        public void testPutRequestUpdateUser() {

            User updateUser=users.get(0);
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

            response.jsonPath().prettyPrint();
            String first_name = response.jsonPath().getString("first_name");
            String last_name = response.jsonPath().getString("last_name");

            Assert.assertEquals(first_name, updateUser.getFirst_name());
            Assert.assertEquals(last_name, updateUser.getLast_name());
        }

        @Test
        public void testDeleteRequestDeleteUser() {
            Response response = given()
                    .when()
                    .delete("https://reqres.in/api/users/2")
                    .then()
                    .statusCode(204)
                    .extract()
                    .response();

            Assert.assertEquals(response.getBody().asString(), "");
        }

        @Test
        public void testGetRequestValidateJsonSchema() {
            Response response = given()
                    .when()
                    .get("https://reqres.in/api/users/2")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

//            response.jsonPath().prettyPrint();
            Assert.assertNotNull(response.jsonPath().get("data.id"));
            Assert.assertNotNull(response.jsonPath().get("data.email"));
            Assert.assertNotNull(response.jsonPath().get("data.first_name"));
            Assert.assertNotNull(response.jsonPath().get("data.last_name"));
        }

        @Test
        public void testLoginRequest() {
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

            System.out.println("Login and token");
            response.jsonPath().prettyPrint();

            Assert.assertNotNull(token);
        }

        @Test
        public void testUnauthorizedRequest() {


        }
    }


