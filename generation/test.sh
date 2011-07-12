#!/bin/bash

cd target
tar -xvzf generation-tool-0.1.0-dev-bin.tar.gz
cd generation-tool-0.1.0-dev/bin
./PDS4Generate -d -p ../examples/i985135l.img -t ../examples/MPF_IMP_EDR7.vm

exit 0