# Copyright 2018 John Hurst
# John Hurst (john.b.hurst@gmail.com)
# 2018-12-26

DIR=$(dirname $0)
NAME=$1
FROM=$2
TO=$3

cd $DIR
perl ${NAME}.pl $FROM $TO