export VIVO_TOMCAT=/home/thomas/workspace/vitro/apache-tomcat-7.0.41

# stop VIVO
$VIVO_TOMCAT/bin/shutdown.sh

# reset the DB
./vivo-01-reset-db.sh

# reset the home
./vivo-02-reset-home.sh

# reset sesame
./vivo-03-reset-sesame.sh

# reload kb-metadata
./vivo-04-reset-kb-metadata.sh

# start VIVO
$VIVO_TOMCAT/bin/startup.sh