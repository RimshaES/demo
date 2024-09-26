package com.example.demo.service;

import com.example.demo.exceptions.CustomExсeption;
import com.example.demo.model.db.entity.User;
import com.example.demo.model.db.repository.UserRepository;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.model.enums.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private ObjectMapper objectMapper;


    @Test
    public void createUser() {
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("test@test.com");

        User user = new User();
        user.setId(1L);
        user.setFirstName("Ivan");

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserInfoResponse result = userService.createUser(request);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getFirstName(), result.getFirstName());

    }

    @Test(expected = CustomExсeption.class)
    public void createUser_badEmail() {
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("test@_test.com");

        userService.createUser(request);

    }

    @Test(expected = CustomExсeption.class)
    public void createUser_userExists() {
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("test@test.com");

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user));

        userService.createUser(request);

    }

    @Test
    public void getUser() {
      }

    @Test
    public void getUserFromDb() {
      }

    @Test
    public void updateUser() {
      }

    @Test
    public void deleteUser() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(UserStatus.DELETED, user.getStatus());
    }

    @Test
    public void getAllUsers() {

        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Ivan");
        user1.setAge(20);
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Igor");
        user2.setAge(30);

        List<User> users = List.of(user1, user2);

        Page page = new PageImpl<>(users);

        when(userRepository.findAllByStatusNot(any(Pageable.class), eq(UserStatus.DELETED))).thenReturn(page);

        Page<UserInfoResponse> result = userService.getAllUsers(1, 10, "lastName", ASC, null);
        assertEquals(2, result.getTotalElements());
        assertEquals("Ivan", result.getContent().get(0).getFirstName());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(20, result.getContent().get(0).getAge());
        assertEquals("Igor", result.getContent().get(1).getFirstName());
        assertEquals(30, result.getContent().get(1).getAge());
        assertEquals(2L, result.getContent().get(1).getId());


      }

    @Test
    public void getAllUsersWithFilters() {

        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Ivan");
        user1.setAge(20);

        List<User> users = List.of(user1);

        Page page = new PageImpl<>(users);

        when(userRepository.findAllByStatusNotFiltered(any(Pageable.class), eq(UserStatus.DELETED), eq("ivan"))).thenReturn(page);

        Page<UserInfoResponse> result = userService.getAllUsers(1, 10, "lastName", ASC, "Ivan");
        assertEquals("Ivan", result.getContent().get(0).getFirstName());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(20, result.getContent().get(0).getAge());

    }

    @Test
    public void updateUserData() {
      }
}