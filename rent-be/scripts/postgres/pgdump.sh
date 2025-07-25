#!/bin/bash

PGDUMP="/usr/bin/pg_dump"

host="127.0.0.1"
#host="rent-db-dev.atechtrade.org"
echo -n "Enter database host (default: $host): " && read host_read && [ -n "$host_read" ] && host=$host_read

port="5432"
echo -n "Enter database port (default: $port): " && read port_read && [ -n "$port_read" ] && port=$port_read

db="rent"
echo -n "Enter database name (default: $db): " && read db_read && [ -n "$db_read" ] && db=$db_read

user="atechadmin"
echo -n "Enter database user (default: $user): " && read user_read && [ -n "$user_read" ] && user=$user_read

pass="enter password"
echo -n "Enter database password (default: $pass): " && read pass_read && [ -n "$pass_read" ] && pass=$pass_read

date=`date +%Y%m%d-%H%M%S`

export PGPASSWORD=$pass

$PGDUMP -U $user -h $host -p $port -d $db -F c -f $db-$date.dump

unset PGPASSWORD

echo done.

exit 0
