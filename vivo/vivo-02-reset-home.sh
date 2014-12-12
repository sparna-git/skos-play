
# check script arguments
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <vivo_home> <vivo_home_snapshot>" >&2
    exit 1
fi
# check if second argument exist
if ! [ -e "$2" ]; then
  echo "$2 not found" >&2
  exit 1
fi
# check if second argument is a directory
if ! [ -d "$2" ]; then
  echo "$2 not a directory" >&2
  exit 1
fi

export VIVO_HOME=$1
export VIVO_HOME_SNAPSHOT=$2

echo "Resetting VIVO Home $VIVO_HOME with snapshot in $VIVO_HOME_SNAPSHOT..."

# delete home dir
rm -rf $VIVO_HOME

# restore with home dir snapshot
cp -r $VIVO_HOME_SNAPSHOT $VIVO_HOME