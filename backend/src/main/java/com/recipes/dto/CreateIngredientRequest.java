package com.recipes.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateIngredientRequest {
    private String name;
    private BigDecimal quantity;
    private String unit;
    private LocalDate expiryDate;
    private Long categoryId;
}
