package com.example.restfullservicetesttask.controller;

import com.example.restfullservicetesttask.controller.assembler.UserAssembler;
import com.example.restfullservicetesttask.entity.User;
import com.example.restfullservicetesttask.exception.NotAllowedAgeException;
import com.example.restfullservicetesttask.exception.NotCorrectDateException;
import com.example.restfullservicetesttask.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService service;
    @MockBean
    private UserAssembler assembler;
    @Autowired
    ObjectMapper mapper;

    @Test
    @SneakyThrows
    @DisplayName("should return user by id")
    public void givenId_thenReturnUserEntityModel() {
        long id = 1L;

        User user = new User();
        user.setId(id);
        user.setEmail("test@gmail.com");

        when(service.findById(id)).thenReturn(user);

        mvc.perform(get("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("should create new user and return object")
    public void post_createsNewUser_andReturnsObj() {
        User user = new User(1L, "test@gmail.com", "first", "last", new Date(1990-10-10),
                "address", "123456789");

        Mockito.when(service.createUser(Mockito.any(User.class))).thenReturn(user);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.mapper.writeValueAsBytes(user));

        mvc.perform(builder)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(this.mapper.writeValueAsString(user)));
    }

    @Test
    @SneakyThrows
    @DisplayName("should return 400 and error message because of invalid date")
    public void post_submitsInvalidUser_WithAgeLowerThan18_Returns400() {
        User user = new User(1L, "test@gmail.com", "first", "last", new Date(2010-10-10),
                "address", "123456789");

        String vehicleJsonString = this.mapper.writeValueAsString(user);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON).content(vehicleJsonString)).andExpect(status().isBadRequest());

        assertEquals(NotAllowedAgeException.class,
                Objects.requireNonNull(resultActions.andReturn().getResolvedException()).getClass());
        assertTrue(Objects.requireNonNull(resultActions.andReturn().getResolvedException()).getMessage()
                .contains("Age: 2010 , is not allowed to be registered"));
    }

    @Test
    @SneakyThrows
    @DisplayName("should return list of users")
    public void get_allUsers_returnsOkWithListOfUsers() {

        List<EntityModel<User>> userList = new ArrayList<>();
        User user = new User(1L, "test@gmail.com", "first", "last", new Date(1990 - 10 - 10),
                "address", "123456789");
        User user2 = new User(2L, "test2@gmail.com", "first2", "last2", new Date(1990 - 10 - 10),
                "address2", "123456789");
        userList.add(assembler.toModel(user));
        userList.add(assembler.toModel(user2));

        Mockito.when(service.findAll()).thenReturn(userList);

        mvc.perform(MockMvcRequestBuilders.get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("should update user and return object")
    public void put_updatesAndReturnsUpdatedObjWith202() {
        long id = 1L;
        User user = new User(id, "test@gmail.com", "updatedFirst", "last", new Date(1990 - 10 - 10),
                "address", "123456789");

        Mockito.when(service.updateUser(id, user)).thenReturn(user);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/users/{id}", id, user)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                .content(this.mapper.writeValueAsBytes(user));

        mvc.perform(builder)
                .andExpect(status().isAccepted())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("should delete user")
    public void delete_deleteVehicle_Returns204Status() {
        long id = 1L;

        UserService serviceSpy = Mockito.spy(service);
        Mockito.doNothing().when(serviceSpy).deleteById(id);

        mvc.perform(MockMvcRequestBuilders.delete("/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(id);
    }

    @Test
    @SneakyThrows
    @DisplayName("should update user address and return object")
    public void patch_updatesAndReturnsUpdatedObj() {
        long id = 1L;
        String newAddress = "newAddress";
        User user = new User(id, "test@gmail.com", "first", "last", new Date(1990 - 10 - 10),
                "address", "123456789");

        Mockito.when(service.replace(id, newAddress)).thenReturn(user);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/users/{id}", id, newAddress)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                .content(this.mapper.writeValueAsBytes(user));

        mvc.perform(builder)
                .andExpect(status().isAccepted())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("should return list of users with specific date")
    public void get_allUsersWithDate_returnsOkWithListOfUsers() {
        List<EntityModel<User>> userList = new ArrayList<>();
        List<EntityModel<User>> foundUserList = new ArrayList<>();
        User user = new User(1L, "test@gmail.com", "first", "last", new Date(1990 - 10 - 10),
                "address", "123456789");
        User user2 = new User(2L, "test2@gmail.com", "first2", "last2", new Date(2000 - 10 - 10),
                "address2", "123456789");
        userList.add(assembler.toModel(user));
        userList.add(assembler.toModel(user2));

        foundUserList.add(assembler.toModel(user));

        Mockito.when(service.findAllByBirthDateRange(new Date(1989 - 10 - 10), new Date(1991 - 10 - 10))).thenReturn(foundUserList);

        mvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("from", String.valueOf(new Date(1989 - 10 - 10)))
                        .param("to", String.valueOf(new Date(1991 - 10 - 10)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("should return 400 and error message because of invalid date params")
    public void get_submitsInvalidParams_WithIncorrectDate_Returns400() {
        List<EntityModel<User>> userList = new ArrayList<>();
        User user = new User(1L, "test@gmail.com", "first", "last", new Date(1990 - 10 - 10),
                "address", "123456789");
        User user2 = new User(2L, "test2@gmail.com", "first2", "last2", new Date(2000 - 10 - 10),
                "address2", "123456789");
        userList.add(assembler.toModel(user));
        userList.add(assembler.toModel(user2));

        String vehicleJsonString = this.mapper.writeValueAsString(userList);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/users")
                .param("to", String.valueOf(new Date(1989 - 10 - 10)))
                .param("from", String.valueOf(new Date(1991 - 10 - 10)))
                .contentType(MediaType.APPLICATION_JSON).content(vehicleJsonString)).andExpect(status().isBadRequest());

        assertEquals(NotCorrectDateException.class,
                Objects.requireNonNull(resultActions.andReturn().getResolvedException()).getClass());
        assertTrue(Objects.requireNonNull(resultActions.andReturn().getResolvedException()).getMessage()
                .contains("To date cant be lower than from date"));
    }
}
