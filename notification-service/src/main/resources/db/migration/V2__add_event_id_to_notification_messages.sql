alter table notification_messages
    add column event_id varchar(36);

update notification_messages
set event_id = 'legacy-event-' || id
where event_id is null;

alter table notification_messages
    alter column event_id set not null;

create unique index if not exists ux_notification_messages_event_id
    on notification_messages (event_id);