package com.example.splitwise.service;

import com.example.splitwise.DTO.SplitTypes;
import com.example.splitwise.Entity.Balances;
import com.example.splitwise.Entity.Transaction;
import com.example.splitwise.Entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpenseService {

    private Map<Integer, Transaction> transactionMap;
    private Map<Integer, List<Balances>> balancesMap;
    private Map<Integer, Map<Integer, Double>> balanceMatrix; // userId -> (otherUserId -> amount)

    public ExpenseService(Map<Integer, Transaction> transactionMap, Map<Integer, List<Balances>> balancesMap) {
        this.transactionMap = transactionMap;
        this.balancesMap = balancesMap;
        this.balanceMatrix = new HashMap<>();
    }

    public ResponseEntity<List<Balances>> getAllBalancesUser(Integer id) {
        List<Balances> balances = balancesMap.getOrDefault(id, new ArrayList<>());
        return ResponseEntity.ok(balances);
    }

    public ResponseEntity<Map<Integer, List<Balances>>> getAllBalances() {
        return ResponseEntity.ok(balancesMap);
    }

    public String createTransaction(Transaction transaction) {
        try {
            Integer tranId = transaction.getTransactionId();
            Double amount = transaction.getAmount();
            Integer paidBy = transaction.getPaidBy();
            List<User> betweenUsers = transaction.getBetweenUsers();
            SplitTypes splitType = transaction.getSplitType();
            String shares = transaction.getShares();
            String percentageShare = transaction.getPercentageShare();

            if (paidBy == null || amount == null || betweenUsers == null || splitType == null) {
                return "Missing required fields: paidBy, amount, betweenUsers, or splitType";
            }

            // Calculate and update balances
            updateBalances(amount, paidBy, betweenUsers, splitType, shares, percentageShare);
            
            transactionMap.put(tranId, transaction);
            return "Successfully created a transaction";
        } catch (Exception e) {
            return "storing of transaction failed: " + e.getMessage();
        }
    }

    private void updateBalances(Double amount, Integer paidBy, List<User> betweenUsers, 
                                SplitTypes splitType, String shares, String percentageShare) {
        
        Map<Integer, Double> userShares = calculateShares(amount, betweenUsers, splitType, shares, percentageShare);
        
        // Initialize balance matrix if needed
        if (!balanceMatrix.containsKey(paidBy)) {
            balanceMatrix.put(paidBy, new HashMap<>());
        }
        
        // Update balances for each user
        for (User user : betweenUsers) {
            Integer userId = user.getId();
            if (userId.equals(paidBy)) continue; // Skip the person who paid
            
            Double share = userShares.get(userId);
            if (share == null) continue;
            
            // Initialize balance matrix for this user if needed
            if (!balanceMatrix.containsKey(userId)) {
                balanceMatrix.put(userId, new HashMap<>());
            }
            
            // Update balance: user owes paidBy
            balanceMatrix.get(userId).put(paidBy, 
                balanceMatrix.get(userId).getOrDefault(paidBy, 0.0) + share);
        }
        
        // Rebuild balances map from balance matrix
        rebuildBalancesMap();
    }

    private Map<Integer, Double> calculateShares(Double amount, List<User> betweenUsers, 
                                                 SplitTypes splitType, String shares, String percentageShare) {
        Map<Integer, Double> userShares = new HashMap<>();
        
        if (splitType == SplitTypes.EQUAL) {
            double share = amount / betweenUsers.size();
            for (User user : betweenUsers) {
                userShares.put(user.getId(), share);
            }
        } 
        else if (splitType == SplitTypes.EXACT) {
            if (shares == null || shares.isEmpty()) {
                throw new IllegalArgumentException("Shares string is required for EXACT split type");
            }
            String[] shareArr = shares.split(",");
            for (String userShareStr : shareArr) {
                String[] parts = userShareStr.split("-");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid share format. Expected: userId-amount");
                }
                Integer userId = Integer.parseInt(parts[0].trim());
                Double share = Double.parseDouble(parts[1].trim());
                userShares.put(userId, share);
            }
        } 
        else if (splitType == SplitTypes.PERCENT) {
            if (percentageShare == null || percentageShare.isEmpty()) {
                throw new IllegalArgumentException("Percentage share string is required for PERCENT split type");
            }
            String[] percentArr = percentageShare.split(",");
            for (String userPercentStr : percentArr) {
                String[] parts = userPercentStr.split("-");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid percentage format. Expected: userId-percentage");
                }
                Integer userId = Integer.parseInt(parts[0].trim());
                Double percentage = Double.parseDouble(parts[1].trim());
                Double share = (amount * percentage) / 100.0;
                userShares.put(userId, share);
            }
        }
        
        return userShares;
    }

    private void rebuildBalancesMap() {
        balancesMap.clear();
        
        for (Map.Entry<Integer, Map<Integer, Double>> userEntry : balanceMatrix.entrySet()) {
            Integer userId = userEntry.getKey();
            List<Balances> balances = new ArrayList<>();
            
            for (Map.Entry<Integer, Double> balanceEntry : userEntry.getValue().entrySet()) {
                Integer otherUserId = balanceEntry.getKey();
                Double amount = balanceEntry.getValue();
                
                if (amount > 0.01) { // Only include significant amounts
                    balances.add(Balances.builder()
                        .userId(userId)
                        .withUser("User " + otherUserId)
                        .amount(amount)
                        .build());
                }
            }
            
            if (!balances.isEmpty()) {
                balancesMap.put(userId, balances);
            }
        }
    }

    public String recordSettlement(Integer fromUserId, Integer toUserId, Double amount) {
        try {
            if (fromUserId == null || toUserId == null || amount == null) {
                return "Missing required fields: fromUserId, toUserId, or amount";
            }
            
            if (!balanceMatrix.containsKey(fromUserId)) {
                balanceMatrix.put(fromUserId, new HashMap<>());
            }
            if (!balanceMatrix.containsKey(toUserId)) {
                balanceMatrix.put(toUserId, new HashMap<>());
            }
            
            // Reduce the debt from fromUserId to toUserId
            Double currentDebt = balanceMatrix.get(fromUserId).getOrDefault(toUserId, 0.0);
            if (currentDebt < amount) {
                return "Settlement amount exceeds the debt. Current debt: " + currentDebt;
            }
            
            balanceMatrix.get(fromUserId).put(toUserId, currentDebt - amount);
            
            // If debt is fully settled, remove it
            if (balanceMatrix.get(fromUserId).get(toUserId) <= 0.0001) {
                balanceMatrix.get(fromUserId).remove(toUserId);
            }
            
            rebuildBalancesMap();
            return "Settlement recorded successfully";
        } catch (Exception e) {
            return "Settlement failed: " + e.getMessage();
        }
    }

    // Bonus: Simplify debts - show minimum transactions needed to settle all debts
    public List<Map<String, Object>> simplifyDebts() {
        List<Map<String, Object>> simplifiedTransactions = new ArrayList<>();
        
        // This is a simplified version - in production, you'd use a more sophisticated algorithm
        // like minimum cash flow or graph-based approach
        for (Map.Entry<Integer, Map<Integer, Double>> userEntry : balanceMatrix.entrySet()) {
            Integer fromUserId = userEntry.getKey();
            for (Map.Entry<Integer, Double> balanceEntry : userEntry.getValue().entrySet()) {
                Integer toUserId = balanceEntry.getKey();
                Double amount = balanceEntry.getValue();
                
                if (amount > 0.01) { // Only include significant amounts
                    Map<String, Object> transaction = new HashMap<>();
                    transaction.put("fromUserId", fromUserId);
                    transaction.put("toUserId", toUserId);
                    transaction.put("amount", amount);
                    simplifiedTransactions.add(transaction);
                }
            }
        }
        
        return simplifiedTransactions;
    }
}
