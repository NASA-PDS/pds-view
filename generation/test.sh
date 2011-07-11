#!/bin/bash

cd target
tar -xvzf generation-tool-0.1.0-dev-bin.tar.gz
cd generation-tool-0.1.0-dev/bin
./PDS4Generate -d -p ../examples/1p216067135edn76pop2102l2m1.img -t ../examples/mer_template.vm

exit 0