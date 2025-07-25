#!/bin/bash

PSQL="/usr/bin/psql"
PGRESTORE="/usr/bin/pg_restore"

if [ -z $1 ] ; then
  echo "Restore is only usable, if a Restore Medium is readable given"
  echo "call: pgrestore.sh /ptah/to/restore_file"
  exit 1
else
  RESTOREFILE=$1
fi

DIR=`dirname $RESTOREFILE`
if [ $DIR = "." ] ; then
  RESTOREFILE=$PWD/$RESTOREFILE
fi

if [ $DIR = ".." ] ; then
  echo this script does not handle relative Paths.
  exit 1
fi

if [ ! -r $RESTOREFILE ] ; then
  echo Restore is only usable, if a Restore File is readable given
  echo I can not read \"$RESTOREFILE\"
  exit 1
fi

if [ ! -f $RESOURCE ] ; then
  echo Sorry, there is no $RESOURCE Resource, no further Action 
  echo is possible
  exit 1
fi

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

echo "Stop on error? (Yes/No default Yes):"
read onerrorstop
if [ "$onerrorstop" != "No" ]; then
  PSQL="$PSQL --set ON_ERROR_STOP=on"
fi

clear
cat <<EOF
Restore of $db from $RESTOREFILE
============================================================================

Proceeding will destroy any living $db Database !

After that the Database will be reinitialized.

EOF
echo -n "Proceed ? "
read answer
test -z $answer && exit;
test  $answer = "yes" || exit;

export PGPASSWORD=$pass

# Drop all db connections
#psql -h $host -p $port -U $user -d postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_get_activity(NULL::integer) WHERE datid=(SELECT oid from pg_database where datname = '$db')"
$PSQL -h $host -p $port -U $user -d postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '$db';"
$PSQL -h $host -p $port -U $user -d postgres -c "CREATE ROLE $user WITH LOGIN PASSWORD '"$pass"'"
$PSQL -h $host -p $port -U $user -d postgres -c "DROP DATABASE IF EXISTS $db"

createDB() {
  $PSQL -h $host -p $port -U $user -d postgres -c "CREATE DATABASE $db"
  $PSQL -h $host -p $port -U $user -d postgres -c "ALTER DATABASE $db OWNER TO $user"
  $PSQL -h $host -p $port -U $user -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE $db to $user"
}

if gzip -t "$RESTOREFILE"; then
  gunzip -c "$RESTOREFILE" > "${dump_file%.sql}"
  RESTOREFILE="${dump_file%.sql}"
fi

# Check the file format using `file` command
file_format=$(file -b "$RESTOREFILE")

# Determine which tool to use based on the format
if [[ "$file_format" == *"ASCII text"* ]]; then
  # If the file is plain text, use psql to restore
  if grep -q 'CREATE DATABASE' "$RESTOREFILE"; then
    $PSQL -h $host -p $port -U $user -d template1 < $RESTOREFILE
  else
    createDB
    $PSQL -h $host -p $port -U $user < $RESTOREFILE $db
  fi
elif [[ "$file_format" == *"PostgreSQL custom database dump"* ]]; then
    # If the file is in custom format, use pg_restore to restore
    createDB
    $PGRESTORE -h $host -p $port -U $user -d $db -F c -1 -x -O -v "$RESTOREFILE"
elif [[ "$file_format" == *"directory"* ]]; then
    # If the file is a directory format, use pg_restore to restore
    createDB
    $PGRESTORE -h $host -p $port -U $user -d $db -F d -1 -x -O -v "$RESTOREFILE"
else
    # If the format is not recognized, show an error message
    echo "Unsupported dump file format: $file_format"
    exit 1
fi

unset PGPASSWORD

echo Database $db restored and up.

exit 0
