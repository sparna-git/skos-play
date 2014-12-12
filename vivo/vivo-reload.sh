
export DATA_DIR=/home/thomas/workspace/01-Projets/MAE

# VIVO URL and connection
export VIVO_URL=http://localhost:8080/sanctuaires
export VIVO_LOGIN=root@myDomain.com
export VIVO_PASSWORD=password
export VIVO_CONNECT="$VIVO_URL $VIVO_LOGIN $VIVO_PASSWORD"

# Target graphs
export GRAPH_ASSERTED_TBOX=http://vitro.mannlib.cornell.edu/default/asserted-tbox
export GRAPH_VITRO_KB_2=http://vitro.mannlib.cornell.edu/default/vitro-kb-2
# export GRAPH_ONTOLOGIE=http://www.mae.u-paris10.fr/sanct/ontologie
# export GRAPH_DATA=http://www.mae.u-paris10.fr/sanct/data
export GRAPH_ONTOLOGIE=$GRAPH_ASSERTED_TBOX
export GRAPH_DATA=$GRAPH_VITRO_KB_2

# Server, for restart
export VIVO_TOMCAT=/home/thomas/workspace/vitro/apache-tomcat-7.0.41


# reload the ontology
./vivo-05-load.sh $DATA_DIR/ontologie/sanct.owl $GRAPH_ONTOLOGIE $VIVO_CONNECT

# reload the ontology parameters
./vivo-05-load.sh $DATA_DIR/ontologie/sanct-vitro-params.ttl $GRAPH_ONTOLOGIE $VIVO_CONNECT

# reload the sanctuaires 
./vivo-05-load.sh $DATA_DIR/reprise/output/1-sanctuaires.xml $GRAPH_DATA $VIVO_CONNECT

# reload the bibliographie 
./vivo-05-load.sh $DATA_DIR/reprise/output/7-bibliographie.xml $GRAPH_DATA $VIVO_CONNECT

# restart server
$VIVO_TOMCAT/bin/shutdown.sh
$VIVO_TOMCAT/bin/startup.sh