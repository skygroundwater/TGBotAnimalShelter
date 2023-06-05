--liquibase formatted sql
--changeset oleg:1

create table petowners
(
    petowner_id   bigint    not null
        constraint petowners_pk
            primary key,
    first_name    varchar   not null,
    last_name     varchar   not null,
    username      varchar   not null,
    registered_at timestamp not null,
    has_pets      boolean
);

alter table petowners
    owner to postgres;

create table cats
(
    nickname              varchar   not null,
    is_chipped            boolean   not null,
    registered_at         timestamp not null,
    shelter               varchar   not null,
    pet_owner_petowner_id bigint    not null
        constraint petowner_id_fk_cats
            references petowners,
    id                    bigserial
        constraint cats_pk
            primary key
);

alter table cats
    owner to postgres;

create table dogs
(
    nickname              varchar   not null,
    is_chipped            boolean   not null,
    registered_at         timestamp not null,
    shelter               varchar   not null,
    pet_owner_petowner_id bigint    not null
        constraint petowner_id_fk
            references petowners,
    id                    bigserial
        constraint dogs_pk
            primary key
);

alter table dogs
    owner to postgres;




