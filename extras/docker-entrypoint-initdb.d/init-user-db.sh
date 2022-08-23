#!/bin/bash
set -e

/usr/local/bin/psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER tombola;
	CREATE DATABASE tombola;
	GRANT ALL PRIVILEGES ON DATABASE tombola TO tombola;
        ALTER USER tombola PASSWORD 'tombolaadmin';
EOSQL
