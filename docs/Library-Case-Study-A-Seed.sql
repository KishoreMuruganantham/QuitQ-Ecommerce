-- Run this script after starting the Spring Boot application once.
-- Hibernate creates the author and book tables automatically.

INSERT INTO author (id, name, country) VALUES
(1, 'R. K. Narayan', 'India'),
(2, 'George Orwell', 'United Kingdom'),
(3, 'Paulo Coelho', 'Brazil')
ON DUPLICATE KEY UPDATE name = VALUES(name), country = VALUES(country);

INSERT INTO book (id, title, genre, published_year, price, author_id) VALUES
(1, 'Malgudi Days', 'Short Stories', 1943, 250.00, 1),
(2, 'The Guide', 'Fiction', 1958, 300.00, 1),
(3, 'Animal Farm', 'Political Satire', 1945, 350.00, 2),
(4, '1984', 'Dystopian Fiction', 1949, 400.00, 2),
(5, 'The Alchemist', 'Adventure', 1988, 299.00, 3),
(6, 'The Spy', 'Thriller', 2016, 450.00, 3)
ON DUPLICATE KEY UPDATE
title = VALUES(title),
genre = VALUES(genre),
published_year = VALUES(published_year),
price = VALUES(price),
author_id = VALUES(author_id);
