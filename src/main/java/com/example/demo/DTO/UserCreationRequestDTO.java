package com.example.splitwise.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCreationRequestDTO {
    private Integer id;
    private String name;
    private String email;
    private String phone;
}
