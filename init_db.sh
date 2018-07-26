set -x
port=5432
db="pomodoro"
user="postgres"
host="localhost"
pw="dev"
psql_command="psql -h $host -p $port -U $user -c"

export PGPASSWORD=$pw

$psql_command 'create user postgres createdb'
$psql_command 'create database pomodoro;'
$psql_command '\i conf/db/migration/V1.0__CreateSchema.sql' -d $db


# psql -h localhost -p 5432  -d postgres -U postgres --password
# docker run --name devpostgres --rm  -e POSTGRES_PASSWORD=$pw  -p5432:5432 postgres