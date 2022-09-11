package com.example.restfullservicetesttask.controller.assembler;

import com.example.restfullservicetesttask.controller.Controller;
import com.example.restfullservicetesttask.entity.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override
    public EntityModel<User> toModel(User user) {

        return EntityModel.of(user,
                linkTo(methodOn(Controller.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(Controller.class).getAll()).withRel("users"));
    }
}
