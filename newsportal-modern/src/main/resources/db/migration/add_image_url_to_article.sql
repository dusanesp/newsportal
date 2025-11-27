-- Database migration script to add image_url column to Article table
-- Execute this script on your database

ALTER TABLE Article ADD COLUMN image_url VARCHAR(500);
