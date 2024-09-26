package com.example.demo.service;

import com.example.demo.exceptions.CustomExсeption;
import com.example.demo.model.db.entity.Car;
import com.example.demo.model.db.entity.User;
import com.example.demo.model.db.repository.CarRepository;
import com.example.demo.model.dto.request.CarInfoRequest;
import com.example.demo.model.dto.request.CarToUserRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.enums.CarStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceTest {

  @InjectMocks private CarService carService;

  @Mock private UserService userService;

  @Spy private ObjectMapper mapper;

  @Mock private CarRepository carRepository;

  @Test
  public void createCarTest() {

    CarInfoRequest carInfoRequest = CarInfoRequest.builder().brand("Audi").model("A8").build();

    Car car = new Car();
    car.setId(1L);
    car.setBrand("Audi");
    car.setModel("A8");

    when(carRepository.save(any(Car.class))).thenReturn(car);

    CarInfoResponse response = carService.createCar(carInfoRequest);

    verify(carRepository, times(1)).save(any(Car.class));
    assertEquals(car.getId(), response.getId());
    assertEquals(car.getBrand(), response.getBrand());
    assertEquals(car.getModel(), response.getModel());
  }

  @Test
  public void getCarTest() {
    Car car = new Car();
    car.setId(1L);

    when(carRepository.findById(1L)).thenReturn(Optional.of(car));
    CarInfoResponse response = carService.getCar(1L);
    assertEquals(car.getId(), response.getId());
  }

    @Test
    public void getCarWhenNoCarTest() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());
        CustomExсeption exсeption = Assertions.assertThrows(CustomExсeption.class, () -> carService.getCar(1L));
        Assertions.assertEquals("Car with ID: 1 not exist", exсeption.getMessage());
    }

  @Test
  public void updateCar() {

      CarInfoRequest carInfoRequest = CarInfoRequest.builder().brand("Audi").model("A8").build();
      Car car = new Car();

      when(carRepository.findById(1L)).thenReturn(Optional.of(car));

      Car savedCar = new Car();
      savedCar.setId(1L);
      when(carRepository.save(any(Car.class))).thenReturn(savedCar);

      CarInfoResponse response = carService.updateCar(1L, carInfoRequest);
      verify(carRepository, times(1)).save(any(Car.class));
      verify(carRepository, times(1)).findById(1L);
      Assertions.assertEquals(1L, response.getId());

  }

  @Test
  public void deleteCarTest() {
      Car car = new Car();
      car.setId(1L);
      when(carRepository.findById(1L)).thenReturn(Optional.of(car));
      carService.deleteCar(1L);

      verify(carRepository, times(1)).save(any(Car.class));

      assertEquals(CarStatus.DELETED, car.getStatus());
  }

  @Test
  public void getAllCarsTest() {
      Car car1 = new Car();
      car1.setId(1L);
      Car car2 = new Car();
      car2.setId(2L);

      List<Car> cars = List.of(car1, car2);

      Page page = new PageImpl<>(cars);

      when(carRepository.findAllByStatusNot(any(Pageable.class), eq(CarStatus.DELETED))).thenReturn(page);
      Page<CarInfoResponse> result = carService.getAllCars(1, 10, "brand", ASC, null);
      assertEquals(2, result.getTotalElements());
      assertEquals(car1.getId(), result.getContent().get(0).getId());
  }

  @Test
  public void addCarToUser() {

    Car car = new Car();
    car.setId(1L);

    when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

    User user = new User();
    user.setId(1l);
    user.setCars(new ArrayList<>());

    when(userService.getUserFromDb(user.getId())).thenReturn(user);
    when(userService.updateUserData(any(User.class))).thenReturn(user);

    CarToUserRequest request = new CarToUserRequest();
    request.setCarId(car.getId());
    request.setUserId(user.getId());

    carService.addCarToUser(request);

    verify(carRepository, times(1)).save(any(Car.class));
    assertEquals(user.getId(), car.getUser().getId());
  }

  @Test
  public void getAllUserCars() {}
}
