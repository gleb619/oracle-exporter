# Oracle exporter

A simple tool for dump data and get ddl from oracle db. 

## Installation
To start, go to official oracle site and download ojdbc8.jar 
https://www.oracle.com/technetwork/database/features/jdbc/jdbc-ucp-122-3110062.html
```
1. git clone git@github.com:amemifra/Spring-Jython.git 
3. mvn install:install-file -Dfile=./ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=12.2.0.1 -Dpackaging=jar
4. mvn clean package
5. java -jar target/oracle-exporter-1.0.0-SNAPSHOT.jar --debug
```

### How to work
To dump with default settings
```
GET http://localhost:8080/api/dump/download/{filename}
```
*Note:* _Instead of {filename} - you must set awaited file name on exit. For example:_
`GET http://localhost:8080/api/dump/download/my_awesome_dump_from_oracle.sql`

To dump with specific settings
```
POST http://localhost:8080/api/dump/download/{filename}
{
 "host": "localhost",
 "port": "1521",
 "sid": "XE",
 "username": "scott",
 "password": "tiger"
}
```

To dump with service name instead of sid
```
{
 "host": "localhost",
 "port": "1521",
 "serviceName": "XE"
}
```

Or specify full url
```
{
 "url": "jdbc:oracle:thin:@localhost:1521:XE"
}
```

Or specify full url with custom role and username
```
{
  "url": "jdbc:oracle:thin:@localhost:1521:XE",
  "username": "system",
  "password": "oracle"
}
```

*Note:* _If some setting wont set, it will be replaced with default one._ It's mean - if you don't send password, default password will be used.  

### How to configure with environment variables
| Key  | Default Value |
| ------------- | ------------- |
| SERVER_PORT  | 8080  |
| DB_URL  | jdbc:oracle:thin:@localhost:1521:XE  |
| DB_USERNAME  | system  |
| DB_PASSWORD  | oracle  |
| LOG_LEVEL  | INFO  |
