
# check script arguments
if [ "$#" -ne 3 ]; then
    echo "Usage: $0 <db_name> <db_login> <db_password>" >&2
    exit 1
fi

export VIVO_DB_NAME=$1
export VIVO_DB_LOGIN=$2
export VIVO_DB_PASSWORD=$3

echo "Resetting VIVO DB $VIVO_DB_NAME..."

# drop all database tables
mysql -u $VIVO_DB_LOGIN --password="$VIVO_DB_PASSWORD" -h localhost $VIVO_DB_NAME -e "SHOW TABLES FROM $VIVO_DB_NAME" | grep -v "Tables_in_$VIVO_DB_NAME" | while read a; do
mysql -u $VIVO_DB_LOGIN --password="$VIVO_DB_PASSWORD" -h localhost $VIVO_DB_NAME -e "DROP TABLE $VIVO_DB_NAME.$a"
done