# Splitwise Expense Sharing Application

A simplified version of Splitwise built with Spring Boot that allows users to split expenses and track balances between each other.

## 🚀 Features

### User Management
- Add users with userId, name, email, and phone
- View all users in the system

### Expense Management
- Create expenses with split types:
  - **EQUAL**: Divide amount equally among all users
  - **EXACT**: Specify exact amounts for each user
  - **PERCENT**: Split based on percentage shares
- Track who paid and who owes what

### Balance Tracking
- View balances for a specific user (who owes whom)
- View all balances in the system
- Automatic balance calculation and updates

### Settlement
- Record settlements between users
- Update balances when someone pays back

### Debt Simplification (Bonus)
- Show minimum transactions needed to settle all debts

## 📋 Prerequisites

- Java 17 or higher
- Gradle 7.x or higher
- Spring Boot 4.0.1

## 🛠️ Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/zindaadmi/splitwiseInterview.git
   cd splitwiseInterview
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

   The application will start on `http://localhost:8080`

## 📡 API Endpoints

### User Endpoints

#### Create User
```http
POST /users
Content-Type: application/json

{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890"
}
```

#### Get All Users
```http
GET /users
```

### Expense Endpoints

#### Create Expense
```http
POST /splitwise/expense
Content-Type: application/json

{
  "transactionId": 1,
  "paidBy": 1,
  "amount": 1000.0,
  "splitType": "EQUAL",
  "betweenUsers": [
    {"id": 1, "name": "John", "email": "john@example.com", "phone": "123"},
    {"id": 2, "name": "Jane", "email": "jane@example.com", "phone": "456"},
    {"id": 3, "name": "Bob", "email": "bob@example.com", "phone": "789"}
  ]
}
```

**For EXACT split type:**
```json
{
  "transactionId": 2,
  "paidBy": 1,
  "amount": 1000.0,
  "splitType": "EXACT",
  "shares": "1-300,2-400,3-300",
  "betweenUsers": [...]
}
```

**For PERCENT split type:**
```json
{
  "transactionId": 3,
  "paidBy": 1,
  "amount": 1000.0,
  "splitType": "PERCENT",
  "percentageShare": "1-50,2-30,3-20",
  "betweenUsers": [...]
}
```

#### Get Balances for a User
```http
GET /splitwise/balance?id=1
```

#### Get All Balances
```http
GET /splitwise/balances
```

#### Record Settlement
```http
POST /splitwise/settle?fromUserId=2&toUserId=1&amount=333.33
```

#### Simplify Debts
```http
GET /splitwise/simplify
```

## 📁 Project Structure

```
src/main/java/com/example/splitwise/
├── SplitwiseApplication.java      # Main Spring Boot application
├── config/
│   └── AppConfig.java             # Configuration for Map beans
├── controller/
│   ├── ExpenseController.java     # Expense management endpoints
│   └── UserController.java        # User management endpoints
├── service/
│   ├── ExpenseService.java        # Expense business logic
│   └── UserService.java           # User business logic
├── Entity/
│   ├── User.java                  # User entity
│   ├── Transaction.java           # Transaction entity
│   └── Balances.java              # Balance entity
└── DTO/
    ├── SplitTypes.java            # Enum: EQUAL, EXACT, PERCENT
    └── UserCreationRequestDTO.java # User creation DTO
```

## 🗄️ Data Storage

The application uses **in-memory HashMaps** for data storage:
- `Map<Integer, User>` - Stores user information
- `Map<Integer, Transaction>` - Stores all transactions
- `Map<Integer, List<Balances>>` - Stores calculated balances
- `Map<Integer, Map<Integer, Double>>` - Internal balance matrix for tracking

**Note:** Data is lost when the application restarts as it's stored in memory.

## 💡 Example Usage

### 1. Create Users
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "Alice",
    "email": "alice@example.com",
    "phone": "1111111111"
  }'
```

### 2. Create an Expense (EQUAL Split)
```bash
curl -X POST http://localhost:8080/splitwise/expense \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": 1,
    "paidBy": 1,
    "amount": 300.0,
    "splitType": "EQUAL",
    "betweenUsers": [
      {"id": 1, "name": "Alice", "email": "alice@example.com", "phone": "111"},
      {"id": 2, "name": "Bob", "email": "bob@example.com", "phone": "222"},
      {"id": 3, "name": "Charlie", "email": "charlie@example.com", "phone": "333"}
    ]
  }'
```

### 3. Check Balances
```bash
curl http://localhost:8080/splitwise/balance?id=2
```

### 4. Record Settlement
```bash
curl -X POST "http://localhost:8080/splitwise/settle?fromUserId=2&toUserId=1&amount=100.0"
```

## 🧪 Testing

Run tests using:
```bash
./gradlew test
```

## 🛠️ Technologies Used

- **Spring Boot 4.0.1** - Framework
- **Java 17** - Programming language
- **Gradle** - Build tool
- **Lombok** - Reduces boilerplate code
- **Spring Web MVC** - REST API

## 📝 Split Type Formats

### EQUAL
- No additional fields required
- Amount is divided equally among all users

### EXACT
- Format: `"shares": "userId-amount,userId-amount"`
- Example: `"shares": "1-300,2-400,3-300"`

### PERCENT
- Format: `"percentageShare": "userId-percentage,userId-percentage"`
- Example: `"percentageShare": "1-50,2-30,3-20"`
- Percentages should add up to 100

## 🎯 Features Implemented

✅ Add user with userId, name, email, phone  
✅ Add expense with paid by user, amount, split type, and list of users  
✅ Support for EQUAL, EXACT, and PERCENT split types  
✅ Show balance for a specific user  
✅ Show all balances in the system  
✅ Record settlement between two users  
✅ Simplify debts (bonus feature)  

## 📄 License

This project is part of an interview coding challenge.

## 👤 Author

Created as part of a Splitwise interview coding challenge.

---

**Note:** This is a simplified implementation using in-memory storage. For production use, consider implementing a database layer with JPA/Hibernate.
