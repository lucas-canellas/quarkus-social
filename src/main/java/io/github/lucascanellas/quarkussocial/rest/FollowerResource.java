package io.github.lucascanellas.quarkussocial.rest;

import io.github.lucascanellas.quarkussocial.domain.model.Follower;
import io.github.lucascanellas.quarkussocial.domain.model.User;
import io.github.lucascanellas.quarkussocial.domain.repository.FollowerRepository;
import io.github.lucascanellas.quarkussocial.domain.repository.UserRepository;
import io.github.lucascanellas.quarkussocial.rest.dto.*;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private UserRepository userRepository;
    private FollowerRepository followerRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
    }


    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest followerRequest) {

        if(userId.equals(followerRequest.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
        }

        User user = userRepository.findById(userId);

        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User follower = userRepository.findById(followerRequest.getFollowerId());

        boolean follows = followerRepository.follows(follower, user);

        if(!follows) {
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);
            followerRepository.persist(entity);
        }

        return Response.noContent().build();

    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {

        User user = userRepository.findById(userId);

        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> list = followerRepository.findByUser(userId);
        FollowersPerUserResponse followersPerUserResponse = new FollowersPerUserResponse();
        followersPerUserResponse.setFollowersCount(list.size());

        List<FollowerResponse> followerResponseList = list.stream().map(l -> new FollowerResponse(l)).collect(Collectors.toList());

        followersPerUserResponse.setContent(followerResponseList);
        return Response.ok(followersPerUserResponse).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);

        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(followerId, userId);


        return Response.status(Response.Status.NO_CONTENT).build();
    }





}

