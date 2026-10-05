#!/usr/bin/env bash
set -euo pipefail

readonly NETWORKS_VERSION="11.0.0-drake.6"
readonly NETWORKS_SHA256="2029ceeab23adcea30a9f9201f2d11e3803b277f252d15efef6f2d6f2dc11d77"
readonly NETWORKS_URL="https://github.com/SlimefunNewHorizons/NetworksV6-drake/releases/download/v${NETWORKS_VERSION}/NetworksV6-Drake-v${NETWORKS_VERSION}.jar"
readonly TARGET_DIR="${RUNNER_TEMP:-${TMPDIR:-/tmp}}/drakes-networks-api"
readonly TARGET_JAR="${TARGET_DIR}/NetworksV6-Drake-v${NETWORKS_VERSION}.jar"

mkdir -p "${TARGET_DIR}"
echo "[INFO] Descargando NetworksV6-drake ${NETWORKS_VERSION}"
curl --fail --silent --show-error --location --retry 3 --output "${TARGET_JAR}" "${NETWORKS_URL}"
echo "${NETWORKS_SHA256}  ${TARGET_JAR}" | sha256sum --check --status
echo "[INFO] Instalando API Networks verificada en el repositorio Maven local"
mvn -B -ntp install:install-file \
  -Dfile="${TARGET_JAR}" \
  -DgroupId=com.github.drakescraft_labs \
  -DartifactId=NetworksV6-drake \
  -Dversion="${NETWORKS_VERSION}" \
  -Dpackaging=jar \
  -DgeneratePom=true
echo "[SUCCESS] NetworksV6-drake ${NETWORKS_VERSION} lista para compilar integraciones."
