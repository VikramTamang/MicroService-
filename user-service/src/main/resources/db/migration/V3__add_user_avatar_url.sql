-- Migration V3: Add user profile picture avatar URL
ALTER TABLE users ADD COLUMN avatar_url TEXT;
