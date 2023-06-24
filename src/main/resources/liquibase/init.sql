--l liquibase formatted sql

-- changeset oleg:1

create table petowners
(
    id                   bigint                not null
        constraint petowners_pk
            primary key,
    first_name           varchar,
    last_name            varchar,
    username             varchar,
    registered_at        timestamp             not null,
    has_pets             boolean default false not null,
    contact_request_chat boolean default false not null,
    report_request_chat  boolean default false not null,
    volunteer_chat       boolean default false not null,
    volunteer_id         bigint,
    phone_number         varchar
);

alter table petowners
    owner to postgres;

create table cats
(
    nickname      varchar                                not null,
    is_chipped    boolean                                not null,
    registered_at timestamp                              not null,
    pet_owner_id  bigint                                 not null
        constraint petowner_id_fk_cats
            references petowners,
    id            bigserial
        constraint cats_pk
            primary key,
    reported      boolean                                not null,
    copied_id     bigint generated always as (id) stored not null
);

alter table cats
    owner to postgres;

create table dogs
(
    nickname      varchar                                not null,
    is_chipped    boolean                                not null,
    registered_at timestamp                              not null,
    pet_owner_id  bigint                                 not null
        constraint petowner_id_fk
            references petowners,
    id            bigserial
        constraint dogs_pk
            primary key,
    reported      boolean                                not null,
    copied_id     bigint generated always as (id) stored not null
);

alter table dogs
    owner to postgres;

create table dog_reports
(
    id              bigserial
        constraint dog_rep_pk
            primary key,
    date            date                                             not null,
    diet            varchar                                          not null,
    common_status   varchar                                          not null,
    behavior        varchar                                          not null,
    pet_owner_id    bigint
        constraint petowner_id_fk
            references petowners,
    dog_id          bigint
        constraint dog_id_fk
            references dogs,
    copied_owner_id bigint generated always as (pet_owner_id) stored not null
);

alter table dog_reports
    owner to postgres;

create table cat_reports
(
    id              bigserial
        constraint cat_rep_pk
            primary key,
    date            date                                             not null,
    diet            varchar                                          not null,
    common_status   varchar                                          not null,
    behavior        varchar                                          not null,
    pet_owner_id    bigint
        constraint petowner_fk
            references petowners,
    cat_id          bigint
        constraint cat_fk
            references cats,
    copied_owner_id bigint generated always as (pet_owner_id) stored not null
);

alter table cat_reports
    owner to postgres;

create table volunteers
(
    id          bigint               not null
        constraint volunteer_pk
            primary key,
    link        varchar,
    first_name  varchar,
    last_name   varchar,
    username    varchar,
    is_free     boolean default true not null,
    petowner_id bigint
        constraint volunteers_petowners_null_fk
            references petowners
);

alter table volunteers
    owner to postgres;

alter table petowners
    add constraint petowners_volunteer_fk
        foreign key (volunteer_id) references volunteers;

create table dog_images
(
    id            bigint not null
        constraint images_pk
            primary key,
    name          varchar,
    original_name varchar,
    size          bigint,
    content_type  varchar,
    is_preview    boolean,
    report_id     bigint
        constraint images_dog_reports_null_fk
            references dog_reports,
    bytes         oid,
    dog_id        bigint
        constraint dog_images_dogs_id_fk
            references dogs
);

alter table dog_images
    owner to postgres;

create table cat_images
(
    id            bigint not null
        constraint cat_images_pk
            primary key,
    name          varchar,
    original_name varchar,
    size          bigint,
    content_type  varchar,
    is_preview    boolean,
    report_id     bigint
        constraint cat_images_cat_reports_fk
            references cat_reports,
    bytes         oid,
    cat_id        bigint
        constraint cat_images_cats_id_fk
            references cats
);

alter table cat_images
    owner to postgres;





