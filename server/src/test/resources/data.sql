INSERT INTO users (name, email)
VALUES ('name1', 'email1'),
       ('name2', 'email2'),
       ('name3', 'email3'),
       ('name4', 'email4');

-- Insert requests
INSERT INTO requests (description, requestor_id, created_date)
VALUES ('description1', 1, {ts '2000-01-01 12:00:00'}),
       ('description2', 2, {ts '2000-01-01 12:00:00'}),
       ('description3', 3, {ts '2000-01-01 12:00:00'}),
       ('description4', 4, {ts '2000-01-01 12:00:00'});

-- Insert items
INSERT INTO items (name, description, is_available, owner_id, request_id)
VALUES ('name1', 'description1', TRUE, 1, 1),
       ('name2', 'description2', TRUE, 2, 2),
       ('name3', 'description3', TRUE, 3, 3),
       ('name4', 'description4', FALSE, 4, 4);

-- Insert bookings
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ({ts '2000-01-01 12:00:00'}, {ts '3000-01-01 12:00:00'}, 1, 1, 'WAITING'),
       ({ts '3000-01-01 12:00:00'}, {ts '2000-01-01 12:00:00'}, 1, 1, 'APPROVED'),
       ({ts '2000-01-01 12:00:00'}, {ts '2000-01-01 12:00:00'}, 1, 1, 'REJECTED'),
       ({ts '2000-01-01 12:00:00'}, {ts '3000-01-01 12:00:00'}, 1, 1, 'CANCELLED');

-- Insert comments
INSERT INTO comments (text, item_id, author_id, created)
VALUES ('text1', 1, 1, {ts '2000-01-01 12:00:00'}),
       ('text2', 2, 2, {ts '2000-01-01 12:00:00'}),
       ('text3', 3, 3, {ts '2000-01-01 12:00:00'}),
       ('text4', 4, 4, {ts '2000-01-01 12:00:00'});