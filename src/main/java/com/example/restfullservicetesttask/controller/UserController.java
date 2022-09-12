package com.example.restfullservicetesttask.controller;

import com.example.restfullservicetesttask.controller.assembler.UserAssembler;
import com.example.restfullservicetesttask.entity.User;
import com.example.restfullservicetesttask.exception.NotAllowedAgeException;
import com.example.restfullservicetesttask.exception.NotCorrectDateException;
import com.example.restfullservicetesttask.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    UserService userService;
    UserAssembler assembler;

    @Value("${allowed.age}")
    int allowedAge;

    @GetMapping("/users/{id}")
    public EntityModel<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);

        return assembler.toModel(user);
    }

    @PostMapping("/users")
    public EntityModel<User> saveUser(@Valid @RequestBody User user) {
        Date nowDate = new Date(System.currentTimeMillis());
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTime(nowDate);

        Date registeredDate = user.getDateOfBirth();
        Calendar calendarRegistered = Calendar.getInstance();
        calendarRegistered.setTime(registeredDate);

        if (calendarRegistered.get(Calendar.YEAR) > calendarRegistered.get(Calendar.YEAR) + allowedAge) {
            throw new NotAllowedAgeException("Age: " + calendarRegistered.get(Calendar.YEAR) + ", is not allowed to be registered");
        }

        return assembler.toModel(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable Long id) {
        User updatedUser = userService.updateUser(id, user);

        EntityModel<User> entityModel = assembler.toModel(updatedUser);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping ("/users/{id}")
    public ResponseEntity<?> replaceUser(@PathVariable("id") Long id, @RequestParam("address") String address) {
        User updatedUser = userService.replace(id, address);

        EntityModel<User> entityModel = assembler.toModel(updatedUser);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getAll() {
        List<EntityModel<User>> users = userService.findAll();

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).getAll()).withSelfRel());
    }

    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getAllByDate(@RequestParam("from") @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
                                         @RequestParam("to") @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {
        int res = fromDate.compareTo(toDate);

        if (res > 0) {
            throw new NotCorrectDateException("To date cant be lower than from date");
        }

        List<EntityModel<User>> users = userService.findAll();
        return CollectionModel.of(users, linkTo(methodOn(UserController.class).getAll()).withRel("users"));
    }
}
