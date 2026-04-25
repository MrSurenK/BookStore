# Bookstore API – Developer Guide

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Overview](#overview)
3. [Getting Started – First Steps](#getting-started--first-steps)
4. [Setting Up Postman](#setting-up-postman)
5. [Endpoints Reference](#endpoints-reference)
   - [Auth Endpoints](#auth-endpoints)
   - [Book Endpoints](#book-endpoints)
6. [Role Permissions Summary](#role-permissions-summary)
7. [Service Layer – Business Logic & Checks](#service-layer--business-logic--checks)
8. [Error Responses](#error-responses)

---

## Overview

The Bookstore API is a RESTful service built with Spring Boot. It manages a catalogue of books and their authors, exposes CRUD operations, and enforces role-based access control using Spring Security with HTTP Basic authentication.

**Base URL**
```
http://localhost:8080
```

**Authentication**  
All protected endpoints require HTTP Basic authentication. Each request must carry the credentials of the logged-in user via the `Authorization: Basic <base64(username:password)>` header. Postman handles this encoding automatically when you use its built-in Basic Auth helper (see [Setting Up Postman](#setting-up-postman)).

---

## Prerequisites

Before running the application, ensure your local environment meets the following requirements.

### Docker

The application's database (MySQL) runs inside a Docker container managed automatically by Spring Boot's Docker Compose integration. **Docker must be installed and running on your machine before you start the application.**

- Download and install Docker Desktop from [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop).
- Verify Docker is running by opening a terminal and executing:

  ```bash
  docker --version
  ```

  You should see output similar to `Docker version 26.x.x`.

When the application starts, Spring Boot reads `compose.yaml` in the project root and automatically spins up a MySQL container with the following configuration:

| Setting          | Value       |
|------------------|-------------|
| Database name    | `bookstore` |
| User             | `myuser`    |
| Password         | `secret`    |
| Port (container) | `3306`      |

You do not need to install MySQL locally or configure a datasource manually — Docker handles everything.

### Java 21

The application targets Java 21. Ensure a JDK 21+ is installed and `JAVA_HOME` is set correctly.

```bash
java --version
```

---

## Getting Started – First Steps

Before calling any protected endpoint, you need at least a **MANAGER** and an **OWNER** account in the database.

### Step 1 – Start the application

From the project root, run:

```bash
./mvnw spring-boot:run
```

Spring Boot will detect `compose.yaml`, start the MySQL Docker container automatically, apply the database schema, and seed the initial book and author data from `data.sql`. Wait until you see a log line similar to:

```
Started BookstoreApplication in X.XXX seconds
```

The API is then available at `http://localhost:8080`.

> **Ensure Docker Desktop is running before executing this command.** If Docker is not running, the application will fail to start because it cannot bring up the MySQL container.

#### Seed Data

On every startup, the application automatically loads the contents of `src/main/resources/data.sql`. This file seeds the database with an initial set of authors and books so the catalogue is ready to browse and test immediately without any manual data entry.

Refer to `data.sql` to see exactly which records are pre-loaded and to understand the data structure expected by the API (ISBN formats, author name and birthday combinations, etc.). These values are also useful as-is when constructing test requests in Postman — for example, when adding a new book you must reference authors that already exist in the database, and the seeded authors are a convenient starting point.

#### ⚠️ Data is not persistent between restarts

The application is configured with `spring.jpa.hibernate.ddl-auto=create-drop`. This means:

- The database schema is **created fresh** every time the application starts.
- The schema and all data are **dropped** when the application shuts down.

Any user accounts, books, or other records created at runtime will be lost when the server stops. To start testing from a clean state each time, re-run the registration requests in Step 2 after restarting the application.

### Step 2 – Create user accounts

Send a `POST` request to `/api/auth/register` for each role you need. Three demo accounts are recommended:

**Create a USER**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "user_demo",
  "password": "password_user",
  "role": "USER"
}
```

**Create a MANAGER**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "manager_demo",
  "password": "password_manager",
  "role": "MANAGER"
}
```

**Create an OWNER**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "owner_demo",
  "password": "password_owner",
  "role": "OWNER"
}
```

> **Note:** Role values are case-insensitive when sent in the request body but are stored in uppercase. Valid values are `USER`, `MANAGER`, and `OWNER`.
>
> Passwords are stored as BCrypt hashes. Never share or log raw passwords.

---

## Setting Up Postman

### Create a Collection

Grouping requests into a Postman Collection lets you set one Authorization header that all child requests inherit automatically — you change credentials in one place instead of every request.

1. In Postman click **New → Collection** and name it (e.g., *Bookstore API*).
2. Click the collection name → **Authorization** tab.
3. Set **Type** to `Basic Auth`.
4. Enter the **Username** and **Password** of the account you want to test with (e.g., `owner_demo` / `password_owner`).
5. Click **Save**.

### Add Requests to the Collection

For every request inside the collection:

1. Open the request.
2. Go to the **Authorization** tab.
3. Set **Type** to `Inherit auth from parent`.

Postman will automatically attach the Basic Auth header from the collection to each request. To test a different role, update the credentials on the **collection** level — all requests pick up the change immediately.

---

## Endpoints Reference

### Auth Endpoints

These endpoints are **public** – no credentials required.

---

#### `POST /api/auth/register`

Creates a new user account with the specified role.

**Access:** Public

**Request Body:**
```json
{
  "username": "manager_demo",
  "password": "password_manager",
  "role": "MANAGER"
}
```

| Field      | Type   | Required | Notes                              |
|------------|--------|----------|------------------------------------|
| `username` | String | Yes      | Must be unique                     |
| `password` | String | Yes      | Stored as BCrypt hash              |
| `role`     | String | No       | Defaults to `USER` if omitted. Allowed values: `USER`, `MANAGER`, `OWNER` |

**Success Response – 201 Created:**
```json
{
  "success": true,
  "message": "user created"
}
```

**Error Responses:**
- `400 Bad Request` – username or password missing
- `400 Bad Request` – username already exists
- `400 Bad Request` – invalid role value

---

### Book Endpoints

#### `GET /api/book/allBooks`

Returns all books that are currently active (not soft-deleted).

**Access:** Public — no credentials required

**Request:** No body, no parameters.

**Success Response – 200 OK:**
```json
{
  "success": true,
  "message": "All books available in store",
  "data": [
    {
      "isbn": "9780306406157",
      "title": "Distributed Systems Basics",
      "authors": [
        { "name": "Avery Brooks", "birthday": "1975-03-14" }
      ],
      "year": 2020,
      "price": 39.99,
      "genre": "Technology",
      "deleted": false
    }
  ]
}
```

---

#### `GET /api/book/searchStore`

Searches books by title and/or author name. Both parameters are optional and can be combined.

**Access:** Public — no credentials required

**Query Parameters:**

| Parameter     | Type            | Required | Notes                                   |
|---------------|-----------------|----------|-----------------------------------------|
| `title`       | String          | No       | Partial, case-insensitive match         |
| `authorNames` | List of strings | No       | Comma-separated or repeated params      |

**Example Requests:**
```
GET /api/book/searchStore?title=distributed
GET /api/book/searchStore?authorNames=avery+brooks
GET /api/book/searchStore?title=api&authorNames=nina+patel&authorNames=carlos+mendes
```

**Success Response – 200 OK:**
```json
{
  "success": true,
  "message": "Search completed",
  "data": [...]
}
```

---

#### `POST /api/book/addNew`

Adds a new book to the catalogue. If a book with the same ISBN was previously soft-deleted, it is reactivated and updated with the new details instead of creating a duplicate record.

**Access:** `MANAGER`, `OWNER`

**Request Body:**
```json
{
  "isbn": "9780134685991",
  "title": "Modern API Patterns",
  "authors": [
    { "name": "Nina Patel",    "birthday": "1982-11-02" },
    { "name": "Carlos Mendes", "birthday": "1990-07-21" }
  ],
  "year": 2022,
  "price": 49.50,
  "genre": "Technology"
}
```

| Field     | Type            | Required | Validation                                   |
|-----------|-----------------|----------|----------------------------------------------|
| `isbn`    | String          | Yes      | Must be a valid ISBN-10 or ISBN-13            |
| `title`   | String          | Yes      | Must not be blank                            |
| `authors` | Array of objects| Yes      | At least one. Each must match an existing author record by exact name and birthday |
| `year`    | Integer         | Yes      | Must be a 4-digit year, not in the future    |
| `price`   | Double          | Yes      | Must be a positive number                    |
| `genre`   | String          | Yes      | Must not be blank                            |

**Author object fields:**

| Field      | Type       | Required |
|------------|------------|----------|
| `name`     | String     | Yes      |
| `birthday` | LocalDate (`YYYY-MM-DD`) | Yes |

**Success Response – 201 Created:**
```json
{
  "success": true,
  "message": "Book created successfully"
}
```

**Error Responses:**
- `400 Bad Request` – ISBN already in use by an active book
- `400 Bad Request` – validation failure (invalid ISBN, future year, etc.)
- `404 Not Found` – one or more authors not found in the database
- `401 Unauthorized` – no credentials provided
- `403 Forbidden` – insufficient role

---

#### `PATCH /api/book/updateBook`

Updates one or more fields on an existing active book. Only the fields included in the request body are changed; omitted fields are left unchanged.

**Access:** `MANAGER`, `OWNER`

**Request Body:**
```json
{
  "isbn": "9780134685991",
  "title": "Modern API Patterns – Second Edition",
  "authors": [
    { "name": "Nina Patel", "birthday": "1982-11-02" }
  ],
  "year": 2024,
  "price": 55.00,
  "genre": "Technology"
}
```

| Field     | Type            | Required | Notes                                          |
|-----------|-----------------|----------|------------------------------------------------|
| `isbn`    | String          | Yes      | Used to locate the book; the value itself is not updated |
| `title`   | String          | No       | Skipped if null or blank                       |
| `authors` | Array of objects| No       | Replaces the full author list if provided      |
| `year`    | Integer         | No       | Must not be in the future if provided          |
| `price`   | Double          | No       | Must be positive with at most 2 decimal places |
| `genre`   | String          | No       | Skipped if null or blank                       |

**Success Response – 200 OK:**
```json
{
  "success": true,
  "message": "Book updated successfully"
}
```

**Error Responses:**
- `404 Not Found` – no active book found with the given ISBN
- `404 Not Found` – one or more replacement authors not found
- `400 Bad Request` – validation failure
- `401 Unauthorized` / `403 Forbidden` – missing or insufficient credentials

---

#### `DELETE /api/book/{isbn}`

Soft-deletes a book by setting its `isDeleted` flag to `true`. The record is retained in the database and can be reactivated by calling `POST /api/book/addNew` with the same ISBN.

**Access:** `OWNER` only

**Path Variable:**

| Variable | Type   | Example          |
|----------|--------|------------------|
| `isbn`   | String | `9780134685991`  |

**Example Request:**
```
DELETE http://localhost:8080/api/book/9780134685991
Authorization: Basic (owner credentials)
```

**Success Response – 200 OK:**
```json
{
  "success": true,
  "message": "Book deleted"
}
```

**Error Responses:**
- `400 Bad Request` – ISBN format is invalid
- `404 Not Found` – no active book found with that ISBN
- `401 Unauthorized` / `403 Forbidden` – missing or insufficient credentials

---

## Role Permissions Summary

| Endpoint                          | USER | MANAGER | OWNER |
|-----------------------------------|:----:|:-------:|:-----:|
| `POST   /api/auth/register`       | ✅ Public | ✅ Public | ✅ Public |
| `GET    /api/book/allBooks`       | ✅ Public | ✅ Public | ✅ Public |
| `GET    /api/book/searchStore`    | ✅ Public | ✅ Public | ✅ Public |
| `POST   /api/book/addNew`         | ❌   | ✅       | ✅     |
| `PATCH  /api/book/updateBook`     | ❌   | ✅       | ✅     |
| `DELETE /api/book/{isbn}`         | ❌   | ❌       | ✅     |

---

## Service Layer – Business Logic & Checks

The service layer is the single source of truth for business rules. Controllers delegate all logic here; they do not make decisions beyond routing the request.

---

### `addNewBook(AddNewBookDTO)`

**What it does:** Persists a new book or reactivates a previously deleted one.

**Checks performed:**

1. **Duplicate check** – queries the database by ISBN (regardless of soft-delete status).
   - If a book exists and `isDeleted = false` → throws `BadRequestException` (duplicate active book).
   - If a book exists and `isDeleted = true` → reactivates the record by setting `isDeleted = false` and updating all fields with the incoming data. No new row is created.
   - If no book exists → proceeds to create a new record.

2. **Author validation** – for each author supplied in the request, the service performs an exact lookup by **name and birthday** (`AuthorRepo.findByNameAndBirthday`).
   - All authors must exist in the database beforehand. If any author is not found, an `EntityNotFoundException` is thrown and no book is saved.

3. **Field mapping** – once the authors are resolved, the book is constructed via its builder and saved.

---

### `updateBook(UpdateBookDTO)`

**What it does:** Applies a partial update to an existing active book.

**Checks performed:**

1. **Existence check** – looks up a non-deleted book by ISBN (`findBookByIsbnAndIsDeletedFalse`). Throws `EntityNotFoundException` if not found.

2. **Selective field update** – each nullable field in the DTO is only applied if it is not `null` (and not blank for strings):
   - `title` – updated if present and not blank.
   - `authors` – if the set is present and not empty, author entities are resolved the same way as in `addNewBook`, then the book's author list is replaced in full.
   - `year` – updated if not `null`.
   - `price` – updated if not `null`.
   - `genre` – updated if present and not blank.

3. **Transaction** – the method is annotated with `@Transactional`. Because the entity is loaded within the same transaction, Hibernate's dirty-checking mechanism persists field changes automatically at the end of the transaction without an explicit `save()` call.

---

### `deleteBook(String isbn)`

**What it does:** Soft-deletes a book so it no longer appears in any search results or listings while keeping the record in the database for potential reactivation.

**Checks performed:**

1. **Existence check** – looks up a non-deleted book by ISBN. Throws `EntityNotFoundException` if not found.
2. **Soft-delete** – sets `isDeleted = true` and explicitly calls `save()` to persist the change.

---

### `findBook(String title, List<String> authorNames)`

**What it does:** Searches the active book catalogue by title, author name(s), or a combination of both.

**Pre-processing:**

- Blank title strings are normalised to `null` so the query treats them as "no filter".
- Each author name in the list is trimmed, filtered for blanks, and lowercased for case-insensitive comparison. If all names are blank after filtering, the list is set to `null`.

**Query behaviour** (defined in `BookRepo.searchBooks`):

- Returns only non-deleted books.
- When both `title` and `authorNames` are provided, both conditions must be satisfied.
- Either parameter can be omitted (left null) to search by only one criterion.
- An empty result set is a valid outcome; the service logs it but does not throw an exception.

Results are mapped to `BookResponseDTO` via `BookMapper.toDTO`.

---

### `getAllBooks()`

**What it does:** Returns the full list of active books with their authors, year, price, and genre.

No business-logic checks are performed beyond executing the repository query (`getAllNonDeletedBooks`) and mapping each result to `BookResponseDTO`.

---

## Error Responses

All errors follow the same response envelope:

```json
{
  "success": false,
  "message": "<high-level description>",
  "error": "<detailed message>"
}
```

| HTTP Status | When it is returned |
|-------------|---------------------|
| `400`       | Validation failure, duplicate book, invalid role, missing required fields |
| `401`       | No credentials supplied for a protected endpoint |
| `403`       | Credentials valid but the user's role is not permitted |
| `404`       | Book or author not found |
| `500`       | Unexpected server-side error |





