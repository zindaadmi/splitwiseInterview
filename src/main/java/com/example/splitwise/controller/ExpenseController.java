package com.example.splitwise.controller;

import com.example.splitwise.Entity.Balances;
import com.example.splitwise.Entity.Transaction;
import com.example.splitwise.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

@RestController
@RequestMapping("/splitwise")
public class ExpenseController {

    private ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/balance")
    public ResponseEntity<List<Balances>> getAllBalancesUser(@RequestParam Integer id) {
        return expenseService.getAllBalancesUser(id);
    }

    @PostMapping("/expense")
    public String createTransaction(@RequestBody Transaction transaction) {
        return expenseService.createTransaction(transaction);
    }

    @GetMapping("/balances")
    public ResponseEntity<Map<Integer, List<Balances>>> getAllBalances() {
        return expenseService.getAllBalances();
    }

    @PostMapping("/settle")
    public String recordSettlement(
            @RequestParam Integer fromUserId,
            @RequestParam Integer toUserId,
            @RequestParam Double amount) {
        return expenseService.recordSettlement(fromUserId, toUserId, amount);
    }

    @GetMapping("/simplify")
    public ResponseEntity<List<Map<String, Object>>> simplifyDebts() {
        return ResponseEntity.ok(expenseService.simplifyDebts());
    }
}
