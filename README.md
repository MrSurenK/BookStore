# BookStore

Helping a tech savvy bookstore owner categorize all his books with RESTFUL API. POC

Requirements:

1. Build a relational DB with books and authors:
   1. MySQL
      1. Book table -> primary key: ISBN
      2. Author table -> primary key -> id (running number)
      3. User table (to store roles) -> primary key -> id

2. CRUD functionality on books:
   1. Add a new book:
      - POST API /api/add
   2. Update a book:
      - PATCH API /api/update/{book_id}
   3. Find a book:
      - GET API /api/{book_id}
   4. Delete a book:(soft delete)
      - DELETE API /api/{book_id}

3. Authentication: HTTP Role based (No JWT)
   1. Role based authentication: User, Manager, Owner
   2. Endpoints are protected based on roles
      1. User -> Find a book
      2. Manager -> Add a book, Update a book, find a book
      3. Owner -> Access all APIs + Delete a book

4. Table Relationship: Many to Many relationship between Books and Authors
   Tables:
5. User
6. Book
7. Author
8. Enum Roles

Refer to setup guide document to get started.
