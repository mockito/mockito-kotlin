#!/bin/bash

set -ev

if [ ${MOCK_MAKER} = "mock-maker-inline" ]; then
    mkdir -p tests/src/test/resources/mockito-extensions
    cp ops/org.mockito.plugins.MockMaker tests/src/test/resources/mockito-extensions/
fi

exit 0;

