--liquibase formatted sql
--changeset oleg:1
create schema animals;

create schema images;

create schema reports;

create table petowners
(
    id                   bigint                not null
        constraint petowners_pk
            primary key,
    first_name           varchar(255),
    last_name            varchar(255),
    username             varchar(255),
    registered_at        timestamp(6)          not null,
    has_pets             boolean default false not null,
    contact_request_chat boolean default false not null,
    report_request_chat  boolean default false not null,
    volunteer_chat       boolean default false not null,
    volunteer_id         bigint,
    phone_number         varchar(255)
);

alter table petowners
    owner to postgres;

create table volunteers
(
    id               bigint               not null
        constraint volunteer_pk
            primary key,
    link             varchar(255),
    first_name       varchar(255),
    last_name        varchar(255),
    username         varchar(255),
    is_free          boolean default true not null,
    petowner_id      bigint
        constraint volunteers_petowners_null_fk
            references petowners
            on update set null on delete set null,
    checking_reports boolean default false,
    in_office        boolean default false
);

alter table volunteers
    owner to postgres;

alter table petowners
    add constraint petowners_volunteer_fk
        foreign key (volunteer_id) references volunteers
            on update set null on delete set null;

create sequence animals.cats_id_seq;

alter sequence animals.cats_id_seq owner to postgres;

create sequence animals.dog_id_seq;

alter sequence animals.dog_id_seq owner to postgres;

create table animals.cats
(
    nickname      varchar(255),
    is_chipped    boolean,
    registered_at timestamp(6),
    pet_owner_id  bigint
        constraint cats_petowners_null_fk
            references petowners
            on update set null on delete set null,
    id            bigserial
        constraint cat_pk
            primary key,
    reported      boolean
);

alter table animals.cats
    owner to postgres;

create table animals.dogs
(
    nickname      varchar(255),
    is_chipped    boolean,
    registered_at timestamp(6),
    pet_owner_id  bigint
        constraint foreign_key_name
            references petowners
            on update set null on delete set null,
    id            bigint default nextval('animals.dog_id_seq'::regclass) not null
        constraint dog_pk
            primary key,
    reported      boolean
);

alter table animals.dogs
    owner to postgres;

create sequence reports.cat_report_id_seq;

alter sequence reports.cat_report_id_seq owner to postgres;

create sequence reports.dog_report_id_seq;

alter sequence reports.dog_report_id_seq owner to postgres;

create table reports.dog_reports
(
    date                 date,
    diet                 varchar(255),
    common_status        varchar(255),
    behavior             varchar(255),
    pet_owner_id         bigint
        constraint dog_reports_petowners_null_fk
            references petowners
            on update set null on delete set null,
    copied_owner_id      bigint,
    id                   bigint  default nextval('reports.dog_report_id_seq'::regclass) not null
        constraint dog_reports_pk
            primary key,
    dog_id               bigint
        constraint dog_reports_dogs_fk
            references animals.dogs
            on update set null on delete set null,
    checked_by_volunteer boolean default false,
    copied_animal_id     bigint
);

alter table reports.dog_reports
    owner to postgres;

create table reports.cat_reports
(
    id                   bigint  default nextval('reports.cat_report_id_seq'::regclass) not null
        constraint cat_reports_pk
            primary key,
    date                 date,
    diet                 varchar(255),
    common_status        varchar(255),
    behavior             varchar(255),
    pet_owner_id         bigint                                                         not null
        constraint cat_reports_petowners_fk
            references petowners
            on update set null on delete set null,
    cat_id               bigint                                                         not null
        constraint cat_reports_cats_fk
            references animals.cats
            on update set null on delete set null,
    copied_owner_id      bigint,
    checked_by_volunteer boolean default false,
    copied_animal_id     bigint
);


alter table reports.cat_reports
    owner to postgres;

create sequence images.cat_images_id_seq;

alter sequence images.cat_images_id_seq owner to postgres;

create sequence images.dog_images_id_seq;

alter sequence images.dog_images_id_seq owner to postgres;

create table images.cat_images
(
    id                     bigint default nextval('images.cat_images_id_seq'::regclass) not null
        constraint cat_images_pk
            primary key,
    file_size              bigint,
    is_preview             boolean,
    report_id              bigint
        constraint cat_images_cat_reports_fk
            references reports.cat_reports
            on update cascade on delete cascade,
    cat_id                 bigint
        constraint cat_images_cats_fk
            references animals.cats
            on update cascade on delete cascade,
    file_as_array_of_bytes bytea,
    telegram_file_id       varchar(255),
    copied_report_id       bigint
);

alter table images.cat_images
    owner to postgres;

create table images.dog_images
(
    id                     bigint default nextval('images.dog_images_id_seq'::regclass) not null
        constraint dog_images_pk
            primary key,
    file_size              bigint,
    is_preview             boolean,
    report_id              bigint
        constraint dog_images_dog_reports_null_fk
            references reports.dog_reports
            on update cascade on delete cascade,
    dog_id                 bigint
        constraint dog_images_dogs_null_fk
            references animals.dogs
            on update cascade on delete cascade,
    file_as_array_of_bytes bytea,
    telegram_file_id       varchar(255),
    copied_report_id       bigint
);

alter table images.dog_images
    owner to postgres;

