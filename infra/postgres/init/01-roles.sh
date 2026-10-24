#!/bin/bash
set -euo pipefail

psql -v ON_ERROR_STOP=1 \
  --username "$POSTGRES_USER" --dbname postgres \
  -v app_password="$IMOB_APP_PASSWORD" \
  -v provisioner_password="$IMOB_PROVISIONER_PASSWORD" <<'EOSQL'
-- Usado pela aplicação em execução: acessa os bancos de tenant, mas não cria nem apaga bancos
CREATE ROLE imob_app LOGIN PASSWORD :'app_password';

-- Usado apenas pelo comando de provisionamento de novos tenants
CREATE ROLE imob_provisioner LOGIN CREATEDB PASSWORD :'provisioner_password';
EOSQL
