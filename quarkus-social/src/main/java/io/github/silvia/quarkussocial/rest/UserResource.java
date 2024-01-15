package io.github.silvia.quarkussocial.rest;

import io.github.silvia.quarkussocial.domain.model.User;
import io.github.silvia.quarkussocial.domain.repository.UserRepository;
import io.github.silvia.quarkussocial.rest.dto.CreateUserRequest;
import io.github.silvia.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;
import java.util.stream.Collectors;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserRepository userRepository;

    private final Validator validator;

    @Inject
    public UserResource(UserRepository userRepository, Validator validator){
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest){
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

        if(!violations.isEmpty()){
            return ResponseError
                    .createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        userRepository.persist(user);

        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    public Response listAllUsers(){
        PanacheQuery<User> query = userRepository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        User user= userRepository.findById(id);

        if(user != null){
            userRepository.delete(user);
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData){
        User user = userRepository.findById(id);

        if(user != null){
            user.setName(userData.getName());
            user.setAge(userData.getAge());
            return Response.ok(user).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
