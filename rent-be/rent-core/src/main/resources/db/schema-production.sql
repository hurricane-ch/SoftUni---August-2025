CREATE SCHEMA IF NOT EXISTS rentch;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE VIEW rentch.users(
    id,
    created_by,
    created_date,
    last_modified_by,
    last_modified_date,
    version,
    email,
    enabled,
    full_name,
    identifier,
    password,
    username,
    branch_id,
    branch_decoded,
    directorate_code,
    directorate_decoded)
AS SELECT
    u.user_id AS id,
    u.user_reg::CHARACTER VARYING(255) AS created_by,
    (u.date_reg AT TIME ZONE 'Europe/Sofia'::TEXT) AS created_date,
    u.user_last_mod::CHARACTER VARYING(255) AS last_modified_by,
    (u.date_last_mod AT TIME ZONE 'Europe/Sofia'::TEXT) AS last_modified_date,
    1 AS version,
    u.email AS email,
    CASE WHEN u.status = 2 THEN true ELSE false END AS enabled,
    u.names AS full_name,
    NULL::CHARACTER VARYING(255) AS identifier,
    (('{bcrypt}'::TEXT || u.password::TEXT))::CHARACTER VARYING(255) AS password,
    u.username AS username,
    r.nomer + 1 AS branch_id,
    r.registratura AS branch_decoded,
    ar2.external_code AS directorate_code,
    ar2.ref_name AS directorate_decoded
FROM
    babhregs.adm_users u
        JOIN babhregs.adm_referents ar ON ar.code = u.user_id AND ar.date_do = '2999-12-31 00:00:00'::TIMESTAMP without TIME zone
        JOIN babhregs.adm_referents ar2 ON ar2.code = ar.code_parent AND ar2.date_do = '2999-12-31 00:00:00'::TIMESTAMP without TIME zone
        JOIN babhregs.registraturi r ON ar2.ref_registratura = r.registratura_id;

CREATE OR REPLACE VIEW rentch.users_roles(user_id, role_id) AS SELECT user_id, group_id AS role_id FROM babhregs.adm_user_group;