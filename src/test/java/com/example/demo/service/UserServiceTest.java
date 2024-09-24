package com.example.demo.service;

import com.example.demo.model.db.entity.User;
import com.example.demo.model.db.repository.UserRepository;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class UserServiceTest {

  ObjectMapper mapper = new ObjectMapper();

  @Mock
  UserRepository userRepository = Mockito.mock(UserRepository.class);

  UserService userService;

  @BeforeEach
  void setUp() {
    userService = new UserService(mapper, userRepository);
  }

  @Test
  void createUser() {
    UserInfoRequest request = new UserInfoRequest();
    request.setEmail("test@test.com");
    request.setPassword("password");

    User userAfterSave = new User();
    userAfterSave.setEmail("test@test.com");
    userAfterSave.setPassword("password");
    userAfterSave.setId(1L);

    Mockito.when(userRepository.save(Mockito.any())).thenReturn(userAfterSave);

    UserInfoResponse response = userService.createUser(request);
    Assertions.assertEquals(1L, response.getId());
  }

  @Test
  void getUser() {}

  @Test
  void getUserFromDb() {}

  @Test
  void updateUser() {}

  @Test
  void deleteUser() {}

  @Test
  void getAllUsers() {}

  @Test
  void updateUserData() {}
}
