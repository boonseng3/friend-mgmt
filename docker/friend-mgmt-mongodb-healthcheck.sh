#!/bin/bash
set -eo pipefail

host="$(hostname -i || echo '127.0.0.1')"

if ping="$(mongo --eval '{ ping: 1 }')" &&
    expected="$(echo -e 'MongoDB shell version v3.4.7\nconnecting to: mongodb://127.0.0.1:27017\nMongoDB server version: 3.4.7\n1')" &&
    [ "$ping" = "$expected" ]; then
    echo $ping
    echo $expected
	exit 0
fi
echo "Failed"
echo [ "$ping" = "$expected" ];
exit 1