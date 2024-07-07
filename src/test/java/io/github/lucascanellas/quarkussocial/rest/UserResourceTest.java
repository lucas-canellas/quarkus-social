package io.github.lucascanellas.quarkussocial.rest;

import io.github.lucascanellas.quarkussocial.rest.dto.CreateUserRequest;
import io.github.lucascanellas.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {


    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("should create an user successfully")
    @Order(1)
    public void createUserTest() {
        CreateUserRequest user = new CreateUserRequest();
        user.setName("John");
        user.setAge(33);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiURL)
                .then()
                .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("should return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest() {
        CreateUserRequest user = new CreateUserRequest();
        user.setName(null);
        user.setAge(null);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiURL)
                .then()
                .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));

    }

    @Test
    @DisplayName("should list all users")
    @Order(3)
    public void listAllUsersTest() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(apiURL)
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

}