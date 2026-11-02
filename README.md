# Ninewoods - Luxury Furniture E-Commerce Platform

[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-F47C20?style=for-the-badge&logo=jakarta&logoColor=white)](https://jakarta.ee/)
[![Hibernate](https://img.shields.io/badge/Hibernate-ORM%206-59666C?style=for-the-badge&logo=hibernate&logoColor=white)](https://hibernate.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Tomcat](https://img.shields.io/badge/Apache%20Tomcat-10.1-F8DC75?style=for-the-badge&logo=apache-tomcat&logoColor=black)](https://tomcat.apache.org/)

A full-stack, enterprise-grade e-commerce web platform designed for luxury furniture retail. Built with a modular Java backend powered by **Embedded Apache Tomcat**, **Jakarta REST (Jersey)**, and **Hibernate ORM** with an ACID-compliant normalized (3NF) relational database architecture.

---

## ✨ Features

- **Storefront & Catalog**
  - Dynamic product catalog with filtering (by category, brand, model, and quality).
  - Detailed single product views with multi-image gallery and stock availability.
- **Customer Authentication & Profiles**
  - User registration with secure email verification.
  - Session-based authentication, password management, and address book.
- **Cart & Order Management**
  - Persistent shopping cart and cart-to-checkout pipeline.
  - Multi-step checkout with delivery type selection and address validation.
- **Payment Processing**
  - Support for multiple payment workflows: Cash on Delivery (COD) and Online Card Payments (Stripe / PayHere).
- **Automated Email Notifications**
  - Responsive HTML transactional emails (order confirmations, verification links) powered by Jakarta Mail & RocketBase Template Builder.
- **Admin Management Portal**
  - Dedicated admin dashboard for inventory control, stock adjustments, and order tracking.
  - Comprehensive reporting suite (Product reports, Customer summaries, and Sales logs).

---

## 🛠️ Tech Stack

| Layer | Technologies |
|---|---|
| **Backend** | Java 21+, Jakarta EE 10 (Servlet 6.1, JAX-RS / Jersey 3.1), Hibernate ORM 6.1 |
| **Server** | Embedded Apache Tomcat 10.1 |
| **Database** | MySQL 8.0+ / 9.0 (via `mysql-connector-j`) |
| **Frontend** | HTML5, CSS3, JavaScript, Bootstrap |
| **Integrations** | Jakarta Mail, RocketBase Email Templates, Stripe Java SDK, Gson |
| **Build Tool** | Apache Maven |

---

## 📁 Project Structure

```text
ninewood2/
├── src/
│   ├── main/
│   │   ├── java/lk/dropti/ninewood/
│   │   │   ├── annotation/       # Custom annotations & filters
│   │   │   ├── config/           # Jersey ResourceConfig & app configurations
│   │   │   ├── controller/api/   # REST API Controllers (User, Product, Cart, Checkout, Admin)
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # Hibernate JPA Entities (User, Product, Orders, Stock, etc.)
│   │   │   ├── service/          # Business logic & services
│   │   │   ├── util/             # Utility classes (HibernateUtil, Env, Mail, AppUtil)
│   │   │   └── Main.java         # Embedded Tomcat entry point
│   │   ├── resources/
│   │   │   ├── env.properties            # Environment credentials (SMTP, Gateways, URLs)
│   │   │   └── hibernate.cfg.xml         # Database connection & entity mapping configuration
│   │   └── webapp/                       # Static UI pages, assets, uploads & web.xml
└── pom.xml                               # Maven project dependencies & build configuration
```

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java Development Kit (JDK)**: Version 21 or higher
- **Apache Maven**: Version 3.8+
- **MySQL Server**: Version 8.0 or higher

---

### 2. Database Configuration
1. Create a MySQL database (e.g., `ninewood_db`):
   ```sql
   CREATE DATABASE ninewood_db;
   ```
2. Configure database credentials in `src/main/resources/hibernate.cfg.xml`:
   ```xml
   <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/ninewood_db?useSSL=false&amp;allowPublicKeyRetrieval=true</property>
   <property name="hibernate.connection.username">YOUR_DB_USERNAME</property>
   <property name="hibernate.connection.password">YOUR_DB_PASSWORD</property>
   ```

---

### 3. Environment Configuration
Create or update `src/main/resources/env.properties` (refer to `env_sample.properties`):
```properties
app.name=Ninewoods Luxury Furniture
app.url=http://localhost:8081/ninewood

# Mail Configurations
mail.host=smtp.gmail.com
mail.port=587
mail.username=your-email@gmail.com
mail.password=your-app-specific-password

# Payment Gateway (Stripe / PayHere)
stripe.secret.key=your_stripe_secret_key
payhere.merchant.id=your_merchant_id
payhere.merchant.secret=your_merchant_secret
```

---

### 4. Build and Run

1. Build the project with Maven:
   ```bash
   mvn clean install
   ```

2. Run the application:
   - Run `lk.dropti.ninewood.Main` directly from your IDE (e.g., IntelliJ IDEA / Eclipse).
   - Or execute via Maven / Command Line:
     ```bash
     mvn exec:java -Dexec.mainClass="lk.dropti.ninewood.Main"
     ```

3. Open your browser and navigate to:
   ```text
   http://localhost:8081/ninewood
   ```

---

## 🌐 API Overview

| Endpoint | Method | Description |
|---|---|---|
| `/api/users/login` | `POST` | Authenticate user & start session |
| `/api/users` | `POST` | Register a new user |
| `/api/users/logout` | `GET` | Terminate active user session |
| `/api/products` | `GET` | Retrieve and filter product list |
| `/api/cart` | `GET` / `POST` | Manage user shopping cart items |
| `/api/checkout/place-cod` | `POST` | Place Cash on Delivery order |
| `/api/checkout/initiate-card-payment` | `POST` | Initiate online card payment |
| `/api/admin/*` | Multiple | Inventory management & reporting endpoints |

---

## 📄 License
This project is developed for educational and portfolio purposes. All rights reserved.
