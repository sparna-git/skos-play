
# reload the ontology
./vivo-05-load.sh /home/thomas/workspace/01-Projets/MAE/ontologie/sanct.owl http://vitro.mannlib.cornell.edu/default/asserted-tbox

# reload the ontology parameters
./vivo-05-load.sh /home/thomas/workspace/01-Projets/MAE/ontologie/sanct-vitro-params.ttl http://vitro.mannlib.cornell.edu/default/asserted-tbox

# reload the sanctuaires 
./vivo-05-load.sh /home/thomas/workspace/01-Projets/MAE/reprise/output/1-sanctuaires.xml http://vitro.mannlib.cornell.edu/default/asserted-tbox