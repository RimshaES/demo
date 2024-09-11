package com.example.demo.service;

import com.example.demo.model.db.entity.User;
import com.example.demo.model.db.repository.UserRepository;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.example.demo.model.dto.response.UserInfoResponse;
import com.example.demo.model.enums.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ObjectMapper mapper;
    private final UserRepository userRepository;


    public UserInfoResponse createUser(UserInfoRequest request) {
        if (!EmailValidator.getInstance().isValid(request.getEmail())) {
            return null;
        }

        User user = mapper.convertValue(request, User.class);
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(UserStatus.CREATED);

        User save = userRepository.save(user);

        return mapper.convertValue(save, UserInfoResponse.class);
    }

    public UserInfoResponse getUser(Long id) {
//        Optional<User> optionalUser = userRepository.findById(id);
//        User user;
//        if (optionalUser.isPresent()) {
//            user = optionalUser.get();
//        } else {
//            user = null;
//        }
        User user = getUserFromDb(id);
        return mapper.convertValue(user, UserInfoResponse.class);
    }

    private User getUserFromDb(Long id) {
        return userRepository.findById(id).orElse(new User());
    }

    public UserInfoResponse updateUser(Long id, UserInfoRequest request) {
        if (!EmailValidator.getInstance().isValid(request.getEmail())) {
            return null;
        }

        User user = getUserFromDb(id);
        user.setEmail(request.getEmail());
        user.setGender(request.getGender() == null ? user.getGender() : request.getGender());
        user.setAge(request.getAge() == null ? user.getAge() : request.getAge());
        user.setFirstName(request.getFirstName() == null ? user.getFirstName() : request.getFirstName());
        user.setLastName(request.getLastName() == null ? user.getLastName() : request.getLastName());
        user.setMiddleName(request.getMiddleName() == null ? user.getMiddleName() : request.getMiddleName());
        user.setPassword(request.getPassword() == null ? user.getPassword() : request.getPassword());

        user.setUpdatedAt(LocalDateTime.now());
        user.setStatus(UserStatus.UPDATED);

        User save = userRepository.save(user);
        return mapper.convertValue(save, UserInfoResponse.class);

    }

    public void deleteUser(Long id) {
//        userRepository.deleteById(id); //физическое удаление
        User user = getUserFromDb(id);
        user.setUpdatedAt(LocalDateTime.now());
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

    }

    public List<UserInfoResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> mapper.convertValue(user, UserInfoResponse.class))
                .collect(Collectors.toList());
    }
}
