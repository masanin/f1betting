CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(255) PRIMARY KEY,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 100.00
    );

CREATE TABLE IF NOT EXISTS event_outcomes (
    event_id VARCHAR(255) NOT NULL PRIMARY KEY,
    winner_driver_id VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS bets (
    bet_id VARCHAR(255) DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    event_id VARCHAR(255) NOT NULL,
    driver_id VARCHAR(255) NOT NULL,
    driver_name VARCHAR(255),
    amount DECIMAL(10, 2) NOT NULL,
    odds INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    potential_winnings DECIMAL(10, 2),
    placed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

CREATE INDEX IF NOT EXISTS idx_bets_user_id ON bets(user_id);
CREATE INDEX IF NOT EXISTS idx_bets_event_id ON bets(event_id);