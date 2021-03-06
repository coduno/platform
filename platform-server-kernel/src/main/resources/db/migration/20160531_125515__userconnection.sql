create table user_connection (
    user_id varchar(255) not null,
    provider_id varchar(255) not null,
    provider_user_id varchar(255),
    rank int not null,
    display_name varchar(255),
    profile_url varchar(512),
    image_url varchar(512),
    access_token varchar(512) not null,
    secret varchar(512),
    refresh_token varchar(512),
    expire_time bigint,
    primary key (user_id, provider_id, provider_user_id));
create unique index user_connection_rank on user_connection(user_id, provider_id, rank);