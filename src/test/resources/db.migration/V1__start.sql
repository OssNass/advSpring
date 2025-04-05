CREATE TABLE authors
(
    id   INTEGER      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(255) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE books(id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
                  name varchar(255) NOT NULL
    ,
                   deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE book_author(book_id INTEGER NOT NULL ,author_id INTEGER NOT NULL,
                         deleted BOOLEAN DEFAULT FALSE);
ALTER TABLE book_author ADD CONSTRAINT ba_PK PRIMARY KEY (book_id,author_id);
ALTER TABLE book_author ADD CONSTRAINT ba_b_FK FOREIGN KEY (book_id) REFERENCES books(id);
ALTER TABLE book_author ADD CONSTRAINT ba_a_FK FOREIGN KEY (author_id) REFERENCES authors(id);
