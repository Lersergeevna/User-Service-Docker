create table if not exists notification_messages (
    id bigserial primary key,
    email varchar(150) not null,
    operation varchar(30) not null,
    status varchar(30) not null,
    retry_count integer not null default 0,
    created_at timestamp not null,
    sent_at timestamp,
    last_error text
);