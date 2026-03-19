package com.recipes.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
