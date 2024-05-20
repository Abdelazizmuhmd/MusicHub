#!/bin/bash

# List of submodules
submodules=("resourceservice" "serviceregistry" "songservice")

# Loop through each submodule
for submodule in "${submodules[@]}"
do
  echo "deleting deployment for $submodule"
  kubectl delete -f ./$submodule/$submodule-deployment.yaml
  kubectl delete -f ./$submodule/$submodule-service.yaml

  echo "Building Docker image for $submodule"
  docker build -t zodic/mypublicdockerhub:${submodule} ./$submodule
  docker push zodic/mypublicdockerhub:${submodule}

  echo "Deploying $submodule"
  kubectl apply -f ./$submodule/$submodule-deployment.yaml
  kubectl apply -f ./$submodule/$submodule-service.yaml

done