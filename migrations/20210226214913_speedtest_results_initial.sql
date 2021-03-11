-- +goose Up
-- +goose StatementBegin
create table result
(
    id                       text primary key,
    timestamp                integer not null,
    url                      text    not null,
    download_bandwidth_bytes integer not null,
    upload_bandwidth_bytes   integer not null,
    latency_ms               NUMBER  not null,
    jitter                   NUMBER  not null,
    server_id                integer not null
);
create
index result_timestamp_index on result(timestamp);
-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
drop
index if exists result_timestamp_index;
drop table result;
-- +goose StatementEnd
