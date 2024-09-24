package com.example.demo.service;

import com.example.demo.exceptions.CustomExсeption;
import com.example.demo.model.db.entity.Car;
import com.example.demo.model.db.entity.User;
import com.example.demo.model.db.repository.CarRepository;
import com.example.demo.model.dto.request.CarInfoRequest;
import com.example.demo.model.dto.request.CarToUserRequest;
import com.example.demo.model.dto.response.CarInfoResponse;
import com.example.demo.model.enums.CarStatus;
import com.example.demo.utils.PaginationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarService {

  private final UserService userService;
  private final ObjectMapper mapper;
  private final CarRepository carRepository;

  public CarInfoResponse createCar(CarInfoRequest request) {

    Car car = mapper.convertValue(request, Car.class);
    car.setCreatedAt(LocalDateTime.now());
    car.setStatus(CarStatus.CREATED);

    Car save = carRepository.save(car);

    return mapper.convertValue(save, CarInfoResponse.class);
  }

  public CarInfoResponse getCar(Long id) {
    Car car = getCarFromDb(id);
    return mapper.convertValue(car, CarInfoResponse.class);
  }

  private Car getCarFromDb(Long id) {
    return carRepository.findById(id).orElseThrow(() -> new CustomExсeption("Car with ID: " + id + " not exist", HttpStatus.NOT_FOUND));
  }

  public CarInfoResponse updateCar(Long id, CarInfoRequest request) {
    Car car = getCarFromDb(id);
    car.setBrand(request.getBrand() == null ? car.getBrand() : request.getBrand());
    car.setModel(request.getModel() == null ? car.getModel() : request.getModel());
    car.setYear(request.getYear() == null ? car.getYear() : request.getYear());
    car.setPrice(request.getPrice() == null ? car.getPrice() : request.getPrice());
    car.setColor(request.getColor() == null ? car.getColor() : request.getColor());
    car.setIsNew(request.getIsNew() == null ? car.getIsNew() : request.getIsNew());
    car.setTransmission(
        request.getTransmission() == null ? car.getTransmission() : request.getTransmission());

    car.setUpdatedAt(LocalDateTime.now());
    car.setStatus(CarStatus.UPDATED);

    Car save = carRepository.save(car);
    return mapper.convertValue(save, CarInfoResponse.class);
  }

  public void deleteCar(Long id) {
    Car car = getCarFromDb(id);
    car.setUpdatedAt(LocalDateTime.now());
    car.setStatus(CarStatus.DELETED);
    carRepository.save(car);
  }

  public Page<CarInfoResponse> getAllCars(
      Integer page, Integer perPage, String sort, Sort.Direction order, String filter) {

    Pageable pageRequest = PaginationUtil.getPageRequest(page, perPage, sort, order);
    Page<Car> all;
    if (filter == null) {
      all = carRepository.findAllByStatusNot(pageRequest, CarStatus.DELETED);
    } else {
      all =
          carRepository.findAllByStatusNotFiltered(
              pageRequest, CarStatus.DELETED, filter.toLowerCase());
    }
    List<CarInfoResponse> content =
        all.getContent().stream()
            .map(car -> mapper.convertValue(car, CarInfoResponse.class))
            .collect(Collectors.toList());

    return new PageImpl<>(content, pageRequest, all.getTotalElements());
  }

  public void addCarToUser(CarToUserRequest request) {
    Car car =
        carRepository
            .findById(request.getCarId())
            .orElseThrow(
                () ->
                    new CustomExсeption(
                        "Car with ID:" + request.getCarId() + " not found", HttpStatus.NOT_FOUND));


    User userFromDb = userService.getUserFromDb(request.getUserId());

    userFromDb.getCars().add(car);

    userService.updateUserData(userFromDb);

    car.setUser(userFromDb);
    carRepository.save(car);
  }

  public List<CarInfoResponse> getAllUserCars(Long userId) {
    User user = userService.getUserFromDb(userId);

    List<Car> allUserCars = user.getCars();

    return allUserCars.stream()
        .map(car -> mapper.convertValue(car, CarInfoResponse.class))
        .collect(Collectors.toList());
  }
}
