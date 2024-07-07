package io.github.lucascanellas.quarkussocial.rest;

import io.github.lucascanellas.quarkussocial.domain.model.Follower;
import io.github.lucascanellas.quarkussocial.domain.model.Post;
import io.github.lucascanellas.quarkussocial.domain.model.User;
import io.github.lucascanellas.quarkussocial.domain.repository.FollowerRepository;
import io.github.lucascanellas.quarkussocial.domain.repository.PostRepository;
import io.github.lucascanellas.quarkussocial.domain.repository.UserRepository;
import io.github.lucascanellas.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.*;
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
    public void setUp() {
        var user = new User();
        user.setName("Lucas");
        user.setAge(33);
        userRepository.persist(user);
        userId = user.getId();

        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        var userNotFollower = new User();
        userNotFollower.setName("Fulano");
        userNotFollower.setAge(32);
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        var userFollower = new User();
        userFollower.setName("Cicrano");
        userFollower.setAge(31);
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

    }

    @Test
    public void createPostTest() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var userId = 1;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
                .when()
                .post()
                .then()
                .statusCode(201);
    }

    @Test
    public void postForNonexistentUserTest() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var nonexistentUserId = 777;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", nonexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(404);
    }



    @Test
    public void listPostUserNotFoundTest() {

        var nonExistentUserId = 777;
        given()
                .pathParam("userId", nonExistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    public void listPostFollowerHeaderNotSendTest() {

        given()
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot header followerId"));

    }

    @Test
    public void listPostFollowerNotFoundTest() {

        var NonexistentFollowerId = 777;

        given()
                .pathParam("userId", userId)
                .header("followerId", NonexistentFollowerId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Nonexistent followerId"));

    }

    @Test
    public void listPostNotAFollowerTest() {
        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }

    @Test
    public void listPostTest() {
        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }


}