package com.example.restfullservicetesttask.controller;

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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Controller {

    UserService userService;

    @Value("${allowed.age}")
    int allowedAge;

    @PostMapping("/create")
    public User saveUser(@Valid @RequestBody User user) {
        Date nowDate = new Date(System.currentTimeMillis());
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTime(nowDate);

        Date registeredDate = user.getDateOfBirth();
        Calendar calendarRegistered = Calendar.getInstance();
        calendarRegistered.setTime(registeredDate);

        if (calendarRegistered.get(Calendar.YEAR) > calendarRegistered.get(Calendar.YEAR) + allowedAge) {
            throw new NotAllowedAgeException("Age: " + calendarRegistered.get(Calendar.YEAR) + ", is not allowed to be registered");
        }

        return userService.create(user);
    }

    @PutMapping("/update")
    public User updateUser(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@Valid @RequestBody User user) {
        userService.delete(user);
    }

    @PutMapping("/replace")
    public User replace(@RequestParam("id") Long id, @RequestParam("address") String address) {
        return userService.replace(id, address);
    }

    @GetMapping("/users")
    public List<User> findAllUsersByDate(@RequestParam("from") @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
                                         @RequestParam("to") @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {
        int res = fromDate.compareTo(toDate);

        if (res > 0) {
            throw new NotCorrectDateException("To date cant be lower than from date");
        }

        return userService.findAllByBirthDateRange(fromDate, toDate);
    }
}
