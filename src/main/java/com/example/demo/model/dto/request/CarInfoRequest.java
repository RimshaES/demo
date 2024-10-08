package com.example.demo.model.dto.request;

import com.example.demo.model.enums.Color;
import com.example.demo.model.enums.Transmission;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarInfoRequest {
    String brand;
    String model;
    Color color;
    Integer year;
    Double price;
    Transmission transmission;
    Boolean isNew;
}
