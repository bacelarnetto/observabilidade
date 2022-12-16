#!/usr/bin/env bash

# Delete namespace
kctl() {
    kubectl "$@"
}

kctl delete -f fluent-bit-namespace.yaml

# Delete roles
kctl delete -f fluent-bit-role.yaml
kctl delete -f fluent-bit-role-binding.yaml


echo ">> Undeploy done!"