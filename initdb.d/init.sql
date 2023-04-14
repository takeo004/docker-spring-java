CREATE SCHEMA line_secretary;

CREATE USER admin WITH PASSWORD 'admin@pass@1995';
GRANT ALL ON SCHEMA line_secretary TO admin;
GRANT ALL PRIVILEGES ON DATABASE line_secretary TO admin;