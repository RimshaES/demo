package com.example.demo.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Color {
    WHITE("Белый"),
    BLACK("Черный"),
    GREEN("Зеленый"),
    RED("Красный"),
    BlUE("Синий");

    private final String description;
}
