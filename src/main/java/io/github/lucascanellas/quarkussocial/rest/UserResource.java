package io.github.lucascanellas.quarkussocial.rest;

import io.github.lucascanellas.quarkussocial.domain.model.User;
import io.github.lucascanellas.quarkussocial.domain.repository.UserRepository;
import io.github.lucascanellas.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository repository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

        if(!violations.isEmpty()) {
            ConstraintViolation<CreateUserRequest> erro = violations.stream().findAny().get();
            String errorMessage = erro.getMessage();
            return Response.status(400).entity(errorMessage).build();
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        repository.persist(user);

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers() {
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {

        User user = repository.findById(id);

        if(user != null) {
            repository.delete(user);
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        User user = repository.findById(id);

        if(user != null) {
            user.setName(userData.getName());
            user.setAge(userData.getAge());
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

}

/**
 *  Sempre que eu fizer uma alteração do BD é preciso ser adicionada a anotação @Transactional
 *  Quando eu tenho um contexto Transacional e altero uma entidade desse contexto, quando o metodo terminal as
 *  alterações serão commitadas sem a necessidade de persistir o dado
 */
