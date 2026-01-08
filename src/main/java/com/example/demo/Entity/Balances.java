package com.example.splitwise.Entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Balances {

    private Integer userId;
    private String withUser;
    private Double amount;
}
