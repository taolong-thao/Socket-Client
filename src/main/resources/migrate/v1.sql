create table Account
(
    email    varchar(255) not null
        primary key,
    password varchar(255) null
);

create table Email
(
    email_id     varchar(100)                   not null
        primary key,
    is_new       tinyint default 1              not null,
    body_mail    varchar(10000) charset utf8mb3 null,
    from_mail    varchar(255)                   not null,
    to_mail      varchar(255)                   not null,
    cc_mail      varchar(255)                   null,
    bcc_mail     varchar(255)                   null,
    account_id   varchar(255)                   null,
    type_mail    varchar(10)                    null,
    subject_mail varchar(255) charset utf8mb3   null
);

create table FileData
(
    file_id   varchar(255) not null
        primary key,
    file_name varchar(255) null,
    base64    longtext     null,
    email_id  varchar(255) null
);

