#!/bin/bash

for i in {1..8}
do
	curl -silent http://organizer$i.picsorganizer.com/ClearCache.action > /dev/null 2>&1
done
echo "Done"
