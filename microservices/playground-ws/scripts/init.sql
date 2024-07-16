CREATE TABLE templates (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    program TEXT,
    result VARCHAR(255)
);

INSERT INTO templates VALUES('variableBinding', 'Variable binding', 'How variable binding works in Monkey language', '', '15');
UPDATE templates
SET program = (
    SELECT program
    FROM (
        SELECT *
        FROM pg_read_file('/data/variable-binding-template.txt')
    ) AS data(program)
)
WHERE id = 'variableBinding';

INSERT INTO templates VALUES('controlFlow', 'Control flow', 'Control the execution flow of a Monkey program', '', 'false');
UPDATE templates
SET program = (
    SELECT program
    FROM (
        SELECT *
        FROM pg_read_file('/data/control-flow-template.txt')
    ) AS data(program)
)
WHERE id = 'controlFlow';

INSERT INTO templates VALUES('arrayBuiltinFn', 'Arrays and built in functions', 'Arrays data structure as well as built in functions to work with arrays', '', '[4, false, 1, 4, [99, 98, 97], 4, 25]');
UPDATE templates
SET program = (
    SELECT program
    FROM (
        SELECT *
        FROM pg_read_file('/data/array-builtin-fn-template.txt')
    ) AS data(program)
)
WHERE id = 'arrayBuiltinFn';

INSERT INTO templates VALUES('dictionary', 'Dictionary data structure', 'Working with dictionaries', '', '20');
UPDATE templates
SET program = (
    SELECT program
    FROM (
        SELECT *
        FROM pg_read_file('/data/dictionary-template.txt')
    ) AS data(program)
)
WHERE id = 'dictionary';

INSERT INTO templates VALUES('closures', 'Closures', 'Closures in Monkey language', '', '29');
UPDATE templates
SET program = (
    SELECT program
    FROM (
        SELECT *
        FROM pg_read_file('/data/closures-template.txt')
    ) AS data(program)
)
WHERE id = 'closures';