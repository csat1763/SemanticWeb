#!/bin/bash
cd scripts/startApplication/
start bash "1-startFuseki.sh"
sleep 5
start bash "2-loadDataToFuseki.sh"
start bash "3-runBackEnd.sh"
start bash "4-runFrontEnd.sh"

