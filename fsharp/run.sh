# Copyright 2018 John Hurst
# John Hurst (john.b.hurst@gmail.com)
# 2018-12-25

DIR=$(dirname $0)
NAME=$1
FROM=$2
TO=$3

cd $DIR
dotnet run --project ${NAME}.fsproj $FROM $TO