# Transfer
scp -i /home/thomas/.ssh/sparna-keypair-francfort.pem fr.sparna/rdf/skos/skos-play/target/skos-play-0.9.2.war ubuntu@92.243.27.145:~

# Redeploy
ssh -i /home/thomas/.ssh/sparna-keypair-francfort.pem ubuntu@92.243.27.145 'sudo su -c "\
service tomcat10-instance-2 stop
rm -rf /var/lib/tomcat10-instance-2/webapps/play.war
rm -rf /var/lib/tomcat10-instance-2/webapps/play
rm -rf /var/lib/tomcat10-instance-2/logs/*
cp /home/ubuntu/skos-play-0.9.2.war /var/lib/tomcat10-instance-2/webapps/play.war
service tomcat10-instance-2 start"'