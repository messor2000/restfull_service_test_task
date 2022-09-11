package com.example.restfullservicetesttask.service;

import com.example.restfullservicetesttask.entity.User;
import org.springframework.hateoas.EntityModel;

import java.util.Date;
import java.util.List;

public interface UserService {

    User findById(Long id);

    User createUser(User user);

    User updateUser(Long id, User user);

    User replace(Long id, String newAddress);

    void deleteById(Long id);

    List<EntityModel<User>> findAll();

    List<EntityModel<User>> findAllByBirthDateRange(Date from, Date to);
}
