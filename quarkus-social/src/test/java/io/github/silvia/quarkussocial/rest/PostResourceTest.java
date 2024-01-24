package io.github.silvia.quarkussocial.rest;

import io.github.silvia.quarkussocial.domain.model.Follower;
import io.github.silvia.quarkussocial.domain.model.Post;
import io.github.silvia.quarkussocial.domain.model.User;
import io.github.silvia.quarkussocial.domain.repository.FollowerRepository;
import io.github.silvia.quarkussocial.domain.repository.PostRepository;
import io.github.silvia.quarkussocial.domain.repository.UserRepository;
import io.github.silvia.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long userId;

    Long userNotFollowerId;

    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setup(){
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        var post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        var userNotFollower = new User();
        userNotFollower.setAge(25);
        userNotFollower.setName("Ciclano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

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
    @DisplayName("should create a post for a user")
    public void createPostTest(){

        var postRequest = new CreatePostRequest();
        postRequest.setText("Some Text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
                .when().post()
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an nonexistent user")
    public void postForInexistentUserTest(){

        var postRequest = new CreatePostRequest();
        postRequest.setText("Some Text");

        var nonexistentUserId = 99;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", nonexistentUserId)
                .when().post()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest(){

        var nonexistentUserId = 99;

        given()
                .pathParam("userId", nonexistentUserId)
                .when().get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){

        given()
                .pathParam("userId", userId)
                .when().get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    public void listPostFollowerNotFoundTest(){

        var nonexistentUserId = 999;

        given()
                .pathParam("userId", userId)
                .header("followerId", nonexistentUserId)
                .when().get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Nonexistent followerId"));
    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    public void listPostFollowerNotAFollowerTest(){

        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when().get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("should return posts")
    public void listPostTest(){
        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when().get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));

    }


}