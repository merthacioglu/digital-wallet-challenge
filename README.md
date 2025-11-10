# Digital Wallet Challenge

A Spring Boot REST API for managing digital wallets with transaction support, authentication, and admin capabilities.

## Features

- **User Authentication & Authorization**
    - JWT-based authentication
    - Role-based access control (BASIC and ADMIN roles)
    - Secure password encryption

- **Wallet Management**
    - Create multiple wallets per customer
    - Support for multiple currencies (USD, EUR, TRY)
    - Configure wallet settings (shopping, withdrawal activation)
    - View wallet balance and transaction history

- **Transaction Processing**
    - Deposit and withdrawal operations
    - IBAN and payment-based transactions
    - Automatic approval for transactions under 1000 units
    - Manual approval required for larger transactions
    - Transaction status management (PENDING, APPROVED, DENIED)

- **Admin Features**
    - Manage wallets for any customer
    - View all customer transactions
    - Approve/deny pending transactions

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with JWT
- **Database**:
    - H2 (Development)
    - PostgreSQL (Production)
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Build Tool**: Gradle
- **Java Version**: 17+

## Getting Started

### Prerequisites

- Java SE Development Kit 17+
- Gradle 
- PostgreSQL (for production)
- Git (if version control is needed)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/merthacioglu/digital-wallet-challenge.git
cd digital-wallet-challenge
```

2. Build the project:
```
./gradlew clean build
```

3. Run the application
- For development: (recommended to test the functionalities)
```
./gradlew bootRun --args='--spring.profiles.active=dev'
```
- For production:
```
./gradlew bootRun --args='--spring.profiles.active=prod'
```
## API Documentation

After starting the application, Access the Swagger UI at: http://localhost:YOUR_PORT_VALUE/swagger-ui.html

### Authentication Endpoints

- Register
```
POST /api/v1/register
Content-Type: application/json

{
  "name": "John",
  "surname": "Doe",
  "trIdentityNo": "12345678901",
  "email": "john.doe@example.com",
  "password": "SecurePass123!"
}

```
- Login

```
  POST /api/v1/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "SecurePass123!"
}

```
- Create Wallet
```
POST /api/v1/addWallet
Authorization: Bearer <token>
Content-Type: application/json

{
  "walletName": "My Savings",
  "currency": "TRY",
  "activeForShopping": true,
  "activeForWithdraw": true
}
```
- List Wallets
```
GET /api/v1/listWallets
Authorization: Bearer <token>
```
- Deposit
```
POST /api/v1/deposit
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 500.00,
  "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
  "sourceType": "IBAN",
  "source": "TR330006100519786457841326"
}
```
- Withrdraw
```
POST /api/v1/withdraw
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 500.00,
  "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
  "destinationType": "IBAN",
  "destination": "TR330006100519786457841326"
}
```
- Get Transactions
```
GET /api/v1/transactions?walletId=ebed7406-0593-4e01-bd7b-7f5abee2315f
Authorization: Bearer <token>
```
- Change Transaction Status
```
POST /api/v1/changeTransactionStatus
Authorization: Bearer <token>
Content-Type: application/json

{
  "transactionId": "ABCDEFGHIJ02",
  "status": "APPROVED"
}
```
## Test Data
The application includes test data in **data.sql** inside `/main/resources`
### Customers
- **Basic User 1:**  john.doe@example.com / SecurePassword123!
- **Basic User 2:** jane.smith@example.com / SecurePassword123!
- **Basic User 3:** admin@example.com / SecurePassword123!

### Wallets
- **John's Main Wallet:** (ID: ebed7406-0593-4e01-bd7b-7f5abee2315f)
- **John's Savings:** (ID: a1b2c3d4-e5f6-7890-abcd-ef1234567890)
- **Jane's Wallet:** (ID: f7e8d9c0-b1a2-3456-7890-1234567890ab)
- **Admin Wallet:** (ID: 12345678-1234-1234-1234-123456789012)

### Transactions
- **Transaction 1:** (ID: ABCDEFGHIJ00) - Deposit of 500.00 to John's Main Wallet (APPROVED)
- **Transaction 2:** (ID: ABCDEFGHIJ01) - Withdrawal of 1500.00 from John's Main Wallet (PENDING)
- **Transaction 3:** (ID: ABCDEFGHIJ02) - Deposit of 750.00 to John's Savings via Payment (APPROVED)
- **Transaction 4:** (ID: ABCDEFGHIJ03) - Deposit of 2500.00 to Jane's Wallet (PENDING)

## Security
- Passwords are encrypted using BCrypt
- JWT tokens expire after configured time (default in application.yml)
- Refresh tokens valid for 7 days
- Role-based access control for admin endpoints

## Database Schema
### Customer
- id (Primary Key)
- name
- surname
- trIdentityNo (Turkish Identity Number - unique)
- email (unique)
- password (encrypted)
- role (BASIC/ADMIN)
### Wallet
- id (Primary Key)
- walletId (UUID - unique)
- walletName (unique)
- customerId (Foreign Key)
- currency (USD/EUR/TRY)
- balance
- usableBalance
- activeForShopping
- activeForWithdraw
### Transaction
- id (Primary Key)
- transactionId (unique, auto-generated)
- walletId (Foreign Key)
- type (DEPOSIT/WITHDRAW)
- oppositeParty, oppositePartyType (IBAN/PAYMENT)
- status (PENDING/APPROVED/DENIED)
- amount

### Business Rules
## Transaction Approval:
- Transactions < 1000: Auto-approved
- Transactions ≥ 1000: Require manual approval
## Balance Management:
- Deposits increase balance immediately
- Large deposits pending approval update usable balance after approval
- Withdrawals deduct from usable balance immediately
- Large withdrawals deduct from total balance after approval
## Wallet Restrictions:
- Shopping/withdrawal operations respect wallet activation flags
- Insufficient usable balance prevents withdrawals

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
