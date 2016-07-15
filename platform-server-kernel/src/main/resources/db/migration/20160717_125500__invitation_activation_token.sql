DROP TABLE IF EXISTS activation_token;

ALTER TABLE invitation RENAME TO activation_token;

ALTER TABLE activation_token ADD username VARCHAR(255) NOT NULL;
ALTER TABLE activation_token ADD password VARCHAR(255) NOT NULL;

ALTER TABLE activation_token ADD id BINARY(16) NOT NULL;
ALTER TABLE activation_token MODIFY challenge_id BINARY(16) DEFAULT NULL;

UPDATE activation_token
INNER JOIN (SELECT token, unhex(replace(uuid(),'-','')) as abc FROM activation_token) new_data
ON (new_data.token = activation_token.token)
SET id = new_data.abc;

ALTER TABLE activation_token DROP PRIMARY KEY, ADD PRIMARY KEY (id);

CREATE UNIQUE INDEX activation_token_email_uindex ON activation_token (email);