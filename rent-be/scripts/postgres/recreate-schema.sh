#!/bin/bash

host="localhost"
function show_hosts {
    echo "Choose database host:"
    echo "1) LOCAL (localhost)"
    echo "2) DEV (rent-db-dev.atechtrade.org)"
    echo "3) STAGE (rent-db-stage.atechtrade.org)"
    echo "4) Exit"
}
while true; do
    show_hosts
    read -p "Enter your choice [1-4] (default: 1 (localhost)): " choice

    # Set default choice to 1 if input is empty
    choice=${choice:-1}

    case $choice in
        1)
            echo "You selected option 1 (database host localhost)"
            host="localhost"
            break
            ;;
        2)
            echo "You selected Option 2 (database host rent-db-dev.atechtrade.org)"
            host="rent-db-dev.atechtrade.org"
            break
            ;;
        3)
            echo "You selected Option 3 (database host rent-db-stage.atechtrade.org)"
            host="rent-db-stage.atechtrade.org"
            break
            ;;
        4)
            echo "Exiting the script."
            exit 0
            ;;
        *)
            echo "Invalid choice. Please select a number between 1 and 4."
            ;;
    esac

    echo # Print a newline for better readability
done
#host="rent-db-dev.atechtrade.org"
#echo -n "Enter database host (default: $host): " && read host_read && [ -n "$host_read" ] && host=$host_read

port="5432"
echo -n "Enter database port (default: $port): " && read port_read && [ -n "$port_read" ] && port=$port_read

db="rent"
echo -n "Enter database name (default: $db): " && read db_read && [ -n "$db_read" ] && db=$db_read

schema=rentch
echo -n "Enter schema name (default: $schema): " && read schema_read && [ -n "$schema_read" ] && schema=$schema_read

user="atechadmin"
echo -n "Enter database user (default: $user): " && read user_read && [ -n "$user_read" ] && user=$user_read

pass="enter password"
echo -n "Enter database password (default: $pass): " && read pass_read && [ -n "$pass_read" ] && pass=$pass_read

while true; do

read -p "Do you want to proceed? (yes/no) " yn

case $yn in
  yes )
    export PGPASSWORD=$pass
    psql -h $host -p $port -U $user -d postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '$db';"
    psql -h $host -p $port -U $user -d $db -c "DROP SCHEMA $schema CASCADE"
    psql -h $host -p $port -U $user -d $db -c "DROP SCHEMA aud CASCADE"
    psql -h $host -p $port -U $user -d $db -c "CREATE SCHEMA $schema"
    psql -h $host -p $port -U $user -d $db -c "ALTER SCHEMA $schema OWNER TO $user"
    psql -h $host -p $port -U $user -d $db -c "GRANT ALL PRIVILEGES ON SCHEMA $schema to $user"
    unset PGPASSWORD
    break;;
  no ) echo exiting...;
    exit;;
  * ) echo invalid response;;
esac

done

echo done.
