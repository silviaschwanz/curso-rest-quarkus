package io.github.silvia.quarkussocial.rest;

import io.github.silvia.quarkussocial.domain.model.Follower;
import io.github.silvia.quarkussocial.domain.model.User;
import io.github.silvia.quarkussocial.domain.repository.FollowerRepository;
import io.github.silvia.quarkussocial.domain.repository.UserRepository;
import io.github.silvia.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;


@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long userFollowerId;


    @BeforeEach
    @Transactional
    void setup(){
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        var userFollower = new User();
        userFollower.setAge(25);
        userFollower.setName("Juca Chaves");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        var follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should return 409 when followerId is equal to userId ")
    public void sameUserAsFollowerTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("should return 404 on follow a user when userId doesn't exist")
    public void userNotFoundWhenTryingFollowTest(){

        var nonexistentUserId = 999;

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", nonexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userFollowerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 on list a user followers when userId doesn't exist")
    public void userNotFoundWhenListFollowersTest(){

        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", nonexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should list a user followers")
    public void listFollowersTest(){

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("followersCount", is(1))
                .body("content.name", hasItems("Juca Chaves"));
    }

    @Test
    @DisplayName("should return 404 on unfollow a user and User doesn't exist")
    public void userNotFoundWhenUnfollowingUserTest(){

        var nonexistentUserId = 999;

        given()
                .pathParam("userId", nonexistentUserId)
                .queryParam("followerId", userFollowerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should unfollow a user")
    public void unfollowingUserTest(){

        given()
                .pathParam("userId", userId)
                .queryParam("followerId", userFollowerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }


}