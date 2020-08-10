#!/bin/bash

set -ex

curl -i --location --request POST 'http://localhost:8080/report' \
     --data '../../../service/src/integration-test/resources/xml/ITK_Report_request.xml' \
     -H "Content-Type: application/xml"