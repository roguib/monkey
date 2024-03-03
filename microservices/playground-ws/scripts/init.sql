CREATE TABLE templates (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    program TEXT
);

INSERT INTO templates VALUES('variableBinding', 'Variable binding', 'How variable binding works in Monkey language', '');
UPDATE templates
SET program = (
    SELECT program
    FROM (
        SELECT *
        FROM pg_read_file('/data/variable-binding-template.txt')
    ) AS data(program)
)
WHERE id = 'variableBinding';

INSERT INTO templates VALUES('controlFlow', 'Control flow', 'Control the execution flow of a Monkey program', '');
UPDATE templates
SET program = (
    SELECT program
    FROM (
        SELECT *
        FROM pg_read_file('/data/control-flow-template.txt')
    ) AS data(program)
)
WHERE id = 'controlFlow';

INSERT INTO templates VALUES('closures', 'Closures', 'Closures in Monkey language', '');
UPDATE templates
SET program = (
    SELECT program
    FROM (
        SELECT *
        FROM pg_read_file('/data/closures-template.txt')
    ) AS data(program)
)
WHERE id = 'closures';