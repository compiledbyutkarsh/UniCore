# 🎓 UniCore — University Management System

A professional desktop-grade university management system built with Java Swing and JDBC. Features a clean dark UI with full database connectivity via MySQL.

## ✨ Features
- 🔐 Admin login system
- 👨‍🎓 Student management (add, edit, delete, search)
- 👨‍🏫 Faculty management
- 📚 Course management
- 🏛️ Department management
- 📋 Course enrollment system
- 📊 Grade management
- 📈 Dashboard with live stats

## 🛠️ Tech Stack
| Layer | Technology |
|---|---|
| Language | Java 17 |
| UI | Java Swing, AWT |
| Database | MySQL |
| Connectivity | JDBC |
| Build Tool | Apache Maven |

## 🚀 Setup & Run

### 1. Clone
```bash
git clone https://github.com/compiledbyutkarsh/UniCore.git
cd UniCore
```

### 2. Database Setup
```bash
mysql -u root -p
```
Then run the SQL from `database/schema.sql`

### 3. Configure DB
Edit `src/main/java/com/unicore/database/DBConnection.java`:
```java
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

### 4. Build & Run
```bash
mvn compile exec:java -Dexec.mainClass="com.unicore.Main"
```

## 📁 Structure
