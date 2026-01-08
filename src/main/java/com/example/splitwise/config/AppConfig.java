package com.example.splitwise.config;

import com.example.splitwise.Entity.Balances;
import com.example.splitwise.Entity.Transaction;
import com.example.splitwise.Entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public Map<Integer, User> userMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<Integer, Transaction> transactionMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<Integer, List<Balances>> balancesMap() {
        return new HashMap<>();
    }
}
