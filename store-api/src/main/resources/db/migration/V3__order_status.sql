-- Add status column to orders
ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'CREATED';
