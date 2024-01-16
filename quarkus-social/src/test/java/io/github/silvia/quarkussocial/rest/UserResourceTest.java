package io.github.silvia.quarkussocial.rest;

import io.github.silvia.quarkussocial.rest.dto.CreateUserRequest;
import io.github.silvia.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.mutiny.ext.web.handler.ResponseContentTypeHandler;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserResourceTest {


    @Test
    @DisplayName("Should create an user successfully")
    public void createUserTest() {

        CreateUserRequest user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(30);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(201)
                .body("name", is("Fulano"))
                .body("age", is(30));
    }

    @Test
    @DisplayName("Should return error when json is not valid")
    public void createUserValidationErrorTest(){
        CreateUserRequest user = new CreateUserRequest();
        user.setName(null);
        user.setAge(null);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS)
                .body("message", is("Validation Error"))
                .body("errors.field", hasItems("name", "age"))
                .body("errors.message", hasItems("Name is Required", "Age is Required"));
    }



}