package io.github.silvia.quarkussocial.rest;

import io.github.silvia.quarkussocial.domain.model.User;
import io.github.silvia.quarkussocial.domain.repository.UserRepository;
import io.github.silvia.quarkussocial.rest.dto.CreateUserRequest;
import io.github.silvia.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @Inject
    UserRepository userRepository;

    @TestHTTPResource("/users")
    URL apiURL;


    @Test
    @DisplayName("Should create an user successfully")
    @Order(1)
    public void createUserTest() {

        CreateUserRequest user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(30);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post(apiURL)
                .then()
                .statusCode(201)
                .body("name", is("Fulano"))
                .body("age", is(30));
    }

    @Test
    @DisplayName("Should return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest(){
        CreateUserRequest user = new CreateUserRequest();
        user.setName(null);
        user.setAge(null);

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post(apiURL)
                .then()
                .statusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS)
                .body("message", is("Validation Error"))
                .body("errors.field", hasItems("name", "age"))
                .body("errors.message", hasItems("Name is Required", "Age is Required"));
    }

    @Test
    @DisplayName("Should list all users")
    @Transactional
    @Order(3)
    public void listAllUsersTest(){

/*        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);*/

        given()
                .contentType(ContentType.JSON)
                .when().get(apiURL)
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }


}