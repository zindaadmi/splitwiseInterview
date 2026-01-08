package com.example.splitwise.Entity;

import com.example.splitwise.DTO.SplitTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Transaction {

    private Integer transactionId;
    private List<User> betweenUsers;
    private SplitTypes splitType;
    private Integer paidBy;
    private Double amount;
    private Double share;
    private String shares;
    private String percentageShare;


}
