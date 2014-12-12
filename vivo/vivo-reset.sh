
# Server connections
export VIVO_TOMCAT=/home/thomas/workspace/vitro/apache-tomcat-7.0.41
export VIVO_SESAME_REPOSITORY=http://localhost:8180/openrdf-sesame/repositories/sanctuaires

# DB connection
export DB_NAME=vivo
export DB_LOGIN=vivo
export DB_PASSWORD=vivo

# Home and home snapshot
export VIVO_HOME=/home/thomas/workspace/vitro/home-mae
export VIVO_HOME_SNAPSHOT=/home/thomas/workspace/vitro/home-mae-snapshot

# KB metadata file
export VIVO_KB_METADATA_FILE=/home/thomas/workspace/01-Projets/MAE/ontologie/sanct-vitro-groups.ttl


# stop VIVO
$VIVO_TOMCAT/bin/shutdown.sh

# reset the DB
./vivo-01-reset-db.sh $DB_NAME $DB_LOGIN $DB_PASSWORD

# reset the home
./vivo-02-reset-home.sh $VIVO_HOME $VIVO_HOME_SNAPSHOT

# reset sesame
./vivo-03-reset-sesame.sh $VIVO_SESAME_REPOSITORY

# reload kb-metadata
./vivo-04-reset-kb-metadata.sh $VIVO_SESAME_REPOSITORY $VIVO_KB_METADATA_FILE

# start VIVO
$VIVO_TOMCAT/bin/startup.sh