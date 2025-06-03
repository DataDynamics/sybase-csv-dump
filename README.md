# Sybase ASE Table CSV File Dumper

## Build

```
mvn clean package
```

## Run

```
java -classpath sybase-csv-dump --jdbc-url=<URL> --username="" --password="" \
                                --database="" --table="" \
                                --delimiter="," --output=user_20250501.csv \
                                SybaseToCSVExporter
```
