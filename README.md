# Authentication sample with several providers
This is an authentication sample what use two providers in inMemoryAuthentication and jdbcAuthentication

# Pre requisites
* JDK 11

# Configuration in application.properties
* Modify the `spring.datasource.url=jdbc:h2:/home/jllort/git/spring-security-test/test` value with the location of your database test file ( the database it comes with user credentials )
* Enable or disable authentication based in the parameters:
```
okm.authentication.supervisor=true
okm.authentication.database=true
```

## Compilation and running
```
mvn clean package
cd target
java -jar security-test-0.0.1-SNAPSHOT.war
```

## Application URL
* Public: http://localhost:8080/
* Protected:  http://localhost:8080/management.html

### Users
* inMemoryAuthentication ***user***: admin with ***password***: test
* jdbcAuthentication ***user***: okmAdmin with ***password***: admin


