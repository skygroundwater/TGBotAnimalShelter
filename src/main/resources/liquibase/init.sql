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

--changeset Ryabinin:2
create table cat_reports
(
    behavior              varchar not null,
    common_status         varchar not null,
    date                  date    not null,
    diet                  varchar not null,
    cat_id                bigint  not null
        constraint cat_id_fk
            references cats,
    pet_owner_petowner_id bigint  not null
        constraint petowner_id_fk
            references petowners,
    id                    bigserial
        constraint cat_reports_pk
            primary key
);

alter table cat_reports
    owner to postgres;

create table dog_reports
(
    behavior              varchar not null,
    common_status         varchar not null,
    date                  date    not null,
    diet                  varchar not null,
    dog_id                bigint  not null
        constraint dog_id_fk
            references dogs,
    pet_owner_petowner_id bigint  not null
        constraint petowner_id_fk
            references petowners,
    id                    bigserial
        constraint dog_reports_pk
            primary key
);

alter table dog_reports
    owner to postgres;