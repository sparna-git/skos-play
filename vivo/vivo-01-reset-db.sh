export VIVO_DB_LOGIN=vivo
export VIVO_DB_PASSWORD=vivo
export VIVO_DB_NAME=vivo

echo "Resetting VIVO DB $VIVO_DB_NAME..."

# drop all database tables
mysql -u $VIVO_DB_LOGIN --password="$VIVO_DB_PASSWORD" -h localhost $VIVO_DB_NAME -e "SHOW TABLES FROM $VIVO_DB_NAME" | grep -v "Tables_in_$VIVO_DB_NAME" | while read a; do
mysql -u $VIVO_DB_LOGIN --password="$VIVO_DB_PASSWORD" -h localhost $VIVO_DB_NAME -e "DROP TABLE $VIVO_DB_NAME.$a"
done

# delete home dir
# rm -rf ./home-1.7/*

# copy config
#cp vivo-rel-1.7/config/runtime.properties ./home-1.7/

# delete webapps
#rm -rf apache-tomcat-7.0.41-vivo-1.7/webapps/vivo
#rm -rf apache-tomcat-7.0.41-vivo-1.7/webapps/vivosolr

# recompile
#cd vivo-rel-1.7 && ant all