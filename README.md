# Sybase ASE Table CSV File Dumper

## Build

```
mvn clean package
```

## Run

```
java -classpath sybase-csv-dump.jar:jconn4-16.3.4.jar SybaseToCSVExporter \
                 --jdbc-url=JDBC_URL --username=USER --password=PASS \
                 --sql-file=user.sql \
                 --delimiter=',' --output=FILE.csv
```
