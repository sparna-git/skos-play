export VIVO_HOME=/home/thomas/workspace/vitro/home-mae
export VIVO_HOME_SNAPSHOT=/home/thomas/workspace/vitro/home-mae-snapshot

echo "Resetting VIVO Home $VIVO_HOME with snapshot in $VIVO_HOME_SNAPSHOT..."

# delete home dir
rm -rf $VIVO_HOME

# restore with home dir snapshot
cp -r $VIVO_HOME_SNAPSHOT $VIVO_HOME