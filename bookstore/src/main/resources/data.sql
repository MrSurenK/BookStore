-- Seed data loaded at startup
-- Keep deletes first so this script can be re-run safely if ddl-auto changes later.
DELETE FROM book_author;
DELETE FROM book;
DELETE FROM author;
DELETE FROM `user`;

-- Authors
INSERT INTO author (id, name, birthday) VALUES
  (1, 'Avery Brooks', '1975-03-14'),
  (2, 'Nina Patel', '1982-11-02'),
  (3, 'Carlos Mendes', '1990-07-21'),
  (4, 'Mei Tanaka', '1987-01-09');

-- Books
INSERT INTO book (isbn, title, `year`, price, genre, is_deleted) VALUES
  ('9780306406157', 'Distributed Systems Basics', 2020, 39.99, 'Technology', 0),
  ('9780134685991', 'Modern API Patterns', 2022, 49.50, 'Technology', 0),
  ('0306406152', 'Unassigned Manuscript', 2024, 19.99, 'Fiction', 0),
  ('9780262033848', 'Data Stories', 2019, 29.00, 'Non-Fiction', 0);

-- Many-to-many mappings (book -> author)
-- Ensure every book has at least one author so seed is valid with application validation enabled.
INSERT INTO book_author (book_isbn, author_id) VALUES
  ('9780306406157', 1),         -- one-author book
  ('9780134685991', 2),
  ('9780134685991', 3),         -- multi-author book
  ('0306406152', 3),            -- previously unassigned book, now assigned to author 3
  ('9780262033848', 4);

-- One user per role
-- NOTE: user rows commented out to avoid storing plaintext passwords in source.
-- Use the /api/auth/register endpoint to create users (recommended). Example:
-- POST /api/auth/register
-- {
--   "username": "owner_demo",
--   "password": "password_owner",
--   "role": "OWNER"
-- }

