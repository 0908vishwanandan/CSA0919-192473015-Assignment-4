CREATE DATABASE IF NOT EXISTS campus_events;

USE campus_events;

CREATE TABLE IF NOT EXISTS events (

    event_id INT AUTO_INCREMENT PRIMARY KEY,

    event_name VARCHAR(120) NOT NULL UNIQUE,

    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS registrations (

    registration_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    student_id VARCHAR(30) NOT NULL,

    student_name VARCHAR(100) NOT NULL,

    email VARCHAR(150) NOT NULL,

    event_id INT NOT NULL,

    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_student_event
        UNIQUE (student_id, event_id),

    CONSTRAINT fk_registration_event
        FOREIGN KEY (event_id)
        REFERENCES events(event_id),

    INDEX idx_event_registration
        (event_id, registration_id)
);

-- Insert sample campus events

INSERT IGNORE INTO events (event_name) VALUES

('Hackathon'),

('Cultural Fest'),

('Technical Workshop'),

('Sports Meet'),

('Photography Contest'),

('Coding Competition');

-- Display available events

SELECT * FROM events;

-- Display registrations

SELECT * FROM registrations;
