package com.example.splitwise.service;

import com.example.splitwise.DTO.UserCreationRequestDTO;
import com.example.splitwise.Entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    private Map<Integer, User> userMap;


    public UserService(Map<Integer, User>userMap){
        this.userMap =userMap;
    }
    public String createUser(UserCreationRequestDTO userCreationRequestDTO) {
        try {
            if (userCreationRequestDTO == null) {
                return "User creation request cannot be null";
            }
            
            Integer userId = userCreationRequestDTO.getId();
            if (userId == null) {
                return "User ID is required";
            }
            
            if (userMap.containsKey(userId)) {
                return "User with ID " + userId + " already exists";
            }
            
            User user = User.builder()
                    .email(userCreationRequestDTO.getEmail())
                    .id(userCreationRequestDTO.getId())
                    .name(userCreationRequestDTO.getName())
                    .phone(userCreationRequestDTO.getPhone())
                    .build();

            userMap.put(userId, user);
            return "User Added Successfully";
        } catch (Exception e) {
            return "Failed to create user: " + e.getMessage();
        }
    }


    public Map<Integer,User> getAllUsers(){

        return userMap;
    }



}
