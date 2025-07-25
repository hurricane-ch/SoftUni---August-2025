# 🏠 Rent Project

A full-stack rental management application built with Angular (frontend) and Spring Boot (backend), using PostgreSQL for the database.

---

## 📦 Tech Stack

- **Frontend**: Angular
- **Backend**: Spring Boot, Java  
- **Database**: PostgreSQL  
- **Build Tools**: Maven

---

## 🚀 Getting Started

Follow these steps to set up and run the project locally.

---

### ✅ Prerequisites

Make sure you have the following installed:

- [Node.js](https://nodejs.org/) (v16 or higher)  
- [npm](https://www.npmjs.com/) (v6 or higher)  
- [Java JDK](https://adoptium.net/) (17 or higher)  
- [PostgreSQL](https://www.postgresql.org/) (v12 or higher)  

---

### 🖥️ Frontend Setup

1. Open the frontend folder:
   ```bash
   cd rent-fe
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the frontend:
   ```bash
   npm run start:local
   ```

---

### 🗄️ Database Setup (PostgreSQL)

Create the database and user:

```bash
# Switch to postgres user
sudo su postgres

# Create role and database
psql -U postgres -c "CREATE ROLE admin WITH LOGIN PASSWORD 'admin';"
psql -U postgres -c "ALTER ROLE admin WITH SUPERUSER;"
psql -U postgres -c "CREATE DATABASE rent;"
psql -U postgres -c "ALTER DATABASE rent OWNER TO admin;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE rent TO admin;"
```

---

### 🔧 Backend Setup

1. **Build the application:**

   With tests:
   ```bash
   ./mvnw clean package
   ```

   Skip tests:
   ```bash
   ./mvnw -DskipTests=true clean package
   ```

2. **Start the backend with Spring profiles:**

   ```bash
   -Dspring.profiles.active=postgres,development,logging-console
   ```

## ✅ All Systems Go!

With frontend and backend running, the app should be fully functional in your local environment.

---

## 🛠️ Troubleshooting

- Check Java version:
  ```bash
  java -version
  ```

- Ensure PostgreSQL is running:
  ```bash
  sudo service postgresql status
  ```

- Verify DB settings in `application.yml` or `application.postres` if connection issues occur.

---

## 📁 Project Structure

```
rent-project/
├── rent-fe/              # React frontend
├── rent-be/              # Spring Boot backend
├── README.md
└── ...
```