package com.recipes.dto;

import lombok.Data;

@Data
public class CookingStepDTO {
    private Long id;
    private Integer stepOrder;
    private String content;
    private String image;
}
