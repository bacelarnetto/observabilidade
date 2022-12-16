#!/usr/bin/env bash

# Set namespace for elk
NAMESPACE=kube-logging

kubectl create -f fluent-bit-namespace.yaml


kctl() {
    kubectl --namespace "$NAMESPACE" "$@"
}
# alias kctl='kubectl --namespace logging'

#
#Fluent Bit must be deployed as a DaemonSet
# Create service account and roles
kctl apply -f fluent-bit-svc-account.yaml
kctl apply -f fluent-bit-role.yaml
kctl apply -f fluent-bit-role-binding.yaml

#Create config map
kctl apply -f fluent-bit-cm.yaml

#Create Daemon Set
kctl apply -f fluent-bit-ds.yaml

#Create Service
kctl apply -f fluent-bit-svc.yaml

echo ">> Deploy done!"