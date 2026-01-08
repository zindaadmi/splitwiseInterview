package com.example.splitwise.controller;


//Design and implement a simplified version of Splitwise that supports:
//User Management: Add users to the system
//Expense Management: Add expenses and split them among users
//Balance Calculation: Show balances for a user (who owes whom)
//Settlement: Record when someone pays back
//Balance Simplification: Minimize the number of transactions needed
//Requirements
//Functional Requirements:
//Add a user with userId, name, email, phone
//Add an expense with:
//Paid by user
//        Amount
//Split type: EQUAL, EXACT, PERCENT
//List of users involved in the split
//Show balance for a specific user
//Show all balances in the system
//Record a settlement between two users
//        (Bonus) Simplify debts - show minimum transactions needed to settle all debts
//

//
//1 hashmap for storing users with their details
//2 hashmap for storing user with their transactions list of transaction


import com.example.splitwise.DTO.UserCreationRequestDTO;
import com.example.splitwise.Entity.User;
import com.example.splitwise.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public String createUser(@RequestBody UserCreationRequestDTO userCreationRequestDTO) {
        return userService.createUser(userCreationRequestDTO);
    }
    
    @GetMapping
    public ResponseEntity<Map<Integer, User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
