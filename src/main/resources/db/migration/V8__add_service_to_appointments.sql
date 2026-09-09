ALTER TABLE appointments
    ADD COLUMN service VARCHAR(150);

UPDATE appointments
SET service = 'Ατομική Συνεδρία'
WHERE service IS NULL;

ALTER TABLE appointments
    ALTER COLUMN service SET NOT NULL;