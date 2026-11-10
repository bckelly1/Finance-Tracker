#!/bin/bash

set -e

docker build ./ui -t transaction_importer_ui:latest

docker build . -t transaction_importer_spring:latest

docker compose up -d

