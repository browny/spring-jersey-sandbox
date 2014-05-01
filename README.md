spring-jersey-sandbox
==============

A starter java web application based on Jersey/Spring/Hibernate framework


0. Prepare the database

	```bash
	mysql -u root -proot -e "CREATE DATABASE sandbox";
	```

1. Apply database patch

	```bash
	$ mvn liquibase:update;
	```

3. Run the test

	```bash
	$ mvn test -Dtest=UserDaoTest
	```

3. Start the web application in embedded tomcat7

	```bash
	$ mvn tomcat7:run

	$ curl 'http://localhost:8080/service/sandbox/users/' --data 'name=joe'
	```

	```json
	{
	  "id" : 1,
	  "name" : "joe"
	}
	```

