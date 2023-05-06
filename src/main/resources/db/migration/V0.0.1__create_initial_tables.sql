CREATE TABLE location (
  id SERIAL PRIMARY KEY,
  location_type VARCHAR(50) NOT NULL,
  location_metadata VARCHAR(255) NOT NULL,
  dat_creation TIMESTAMP NOT NULL,
  dat_update TIMESTAMP NOT NULL
);

CREATE TABLE event (
  id SERIAL PRIMARY KEY,
  event_name VARCHAR(100) NOT NULL,
  event_code VARCHAR(36) NOT NULL,
  event_owner_code VARCHAR(36) NOT NULL,
  event_description TEXT NOT NULL,
  dat_start TIMESTAMP,
  dat_finishing TIMESTAMP,
  dat_creation TIMESTAMP NOT NULL,
  dat_update TIMESTAMP NOT NULL,
  location_id INTEGER,
  FOREIGN KEY (location_id) REFERENCES location (id)
);

CREATE TABLE "user" (
  id SERIAL PRIMARY KEY,
  user_code VARCHAR(36) NOT NULL,
  user_email VARCHAR(100) NOT NULL,
  user_password VARCHAR(100) NOT NULL,
  dat_creation TIMESTAMP NOT NULL,
  dat_update TIMESTAMP NOT NULL
);

CREATE TABLE user_event (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  event_id INTEGER NOT NULL,
  dat_check_in TIMESTAMP NOT NULL,
  dat_check_out TIMESTAMP NOT NULL,
  FOREIGN KEY (user_id) REFERENCES "user" (id),
  FOREIGN KEY (event_id) REFERENCES event (id)
);