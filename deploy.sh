#!/bin/bash
set -e

ECR_REGISTRY=558975907408.dkr.ecr.ap-southeast-2.amazonaws.com

aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin "$ECR_REGISTRY"

cd /opt/quitq
docker compose -f docker-compose.ec2.yml pull
docker compose -f docker-compose.ec2.yml up -d --remove-orphans
