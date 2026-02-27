-- Friends list table
CREATE TABLE friends (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    character_id INTEGER NOT NULL,
    friend_name TEXT NOT NULL,
    friend_character_id INTEGER,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    UNIQUE (character_id, friend_name)
);

-- Index for character friend lookups
CREATE INDEX idx_friends_character_id ON friends(character_id);

-- Ignore list table
CREATE TABLE ignores (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    character_id INTEGER NOT NULL,
    ignore_name TEXT NOT NULL,
    ignore_character_id INTEGER,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    UNIQUE (character_id, ignore_name)
);

-- Index for character ignore lookups
CREATE INDEX idx_ignores_character_id ON ignores(character_id);
