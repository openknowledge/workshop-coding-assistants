CREATE SEQUENCE seq_c_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE tab_counter (
    c_id BIGINT NOT NULL,
    c_name VARCHAR(255) NOT NULL,
    c_value INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT pk_c_id PRIMARY KEY (c_id),
    CONSTRAINT uq_c_name UNIQUE (c_name)
);

INSERT INTO tab_counter (c_id, c_name, c_value) VALUES (nextval('seq_c_id'), 'example', '0');
