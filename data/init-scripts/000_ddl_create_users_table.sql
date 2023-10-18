create table users
(
    id       bigserial    not null unique,
    username varchar(255) not null unique,
    password varchar(255) not null,
    primary key (id)
)