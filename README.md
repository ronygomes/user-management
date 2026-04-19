# AI Hackathon - User Management

This code was generated as part of **2 days** bootcamp **Distributed System with AI Hackathon**
conducted by **Shah Ali Newaj Topu**. I attended the first offering of this course on 17, 24 January 2026.

This repository contains code from first day, where we learn to create a 3-tier user-management system using
AntiGravity. Following are directory description of this project

* [prompts][1]: Contains text prompts used for generating code.
* [bruno-collections][6]: Bruno REST API Client collections.
* [user-management-common][2]: Common components like dto, exception, model etc.
* [user-management-repository-mongodb][4]: Contains code to access database.
* [user-management-service][5]: Business logic implementation.
* [user-managemet-api][3]: API endpoints

Contains 3 API endpoints

| Method | Endpoint     | Description              |
|--------|--------------|--------------------------|
| GET    | `/users/:id` | View user                |
| POST   | `/register`  | Register user            |
| PUT    | `/users/:id` | Update certain user data |

## Build & Run

Assuming SDKMan is installed, following command in project root will build the project:

```shell
$ sdk env install
$ ./mvnw clean package

```

Following commands will make the application available at http://localhost:8080.

```shell
# Run MongoDB in container
$ docker compose up -d
$ java -jar user-management-api/target/user-management-api-1.0-SNAPSHOT.jar
```

## Test

This project doesn't contain Unit Tests. But provides **Bruno collections** which can run using the following command:

```shell
$ cd bruno-collections

# Without --sandbox=developer custom scripts doesn't run, 
# so test fails because test data BDPhoneNumber not created.
$ bru run --env-var baseUrl="http://localhost:8080" --sandbox=developer
```

**Note:** Email is sent after successful registration. Update constants `DEFAULT_SMTP_USER` and
`DEFAULT_SMTP_PASSWORD` in ConfigurationManager.java file of `user-management-api` module.
Alternatively can provide with `smtp.username` and `smtp.password` JVM property.

[1]: ./prompts

[2]: ./user-management-common

[3]: ./user-management-api

[4]: ./user-management-repository-mongodb

[5]: ./user-management-service

[6]: ./bruno-collections
