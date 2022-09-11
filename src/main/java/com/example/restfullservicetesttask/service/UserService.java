package com.example.restfullservicetesttask.service;

import com.example.restfullservicetesttask.entity.User;

import java.util.Date;
import java.util.List;

public interface UserService {

    User findById(Long id);

    User create(User user);

    User update(User user);

    User replace(Long id, String newAddress);

    void delete(User user);

    List<User> findAllByBirthDateRange(Date from, Date to);
}
