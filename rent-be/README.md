# RENT Restful API
Easily extendable RESTful API footing.

The goal of this project is to have a solid and structured foundation to build upon on.

- Create a postgres database and add all required variables for your database in the config accordingly if not using same as default
```bash
sudo su postgres

psql -U postgres -c "CREATE ROLE admin WITH LOGIN PASSWORD 'admin';"
psql -U postgres -c "ALTER ROLE admin WITH SUPERUSER;"
psql -U postgres -c "CREATE DATABASE rent;"
psql -U postgres -c "ALTER DATABASE rent OWNER TO admin;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE rent to admin;"
```
- Build the application: ```./mvnw clean package``` or ```./mvnw -DskipTests=true clean package``` (avoid tests)
- Run the application with postgres profile active: ``` java -jar -Dspring.profiles.active=postgres,logging-console rent-resource/target/rent-be.jar```
- Run the application with H2 profile active: ``` java -jar -Dspring.profiles.active=h2,logging-console rent-resource/target/rent-be.jar```