-- +goose Up
-- +goose StatementBegin
create table server
(
    id       integer primary key on conflict replace,
    name     text not null,
    country  text not null,
    location text not null
);
-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
drop table server;
-- +goose StatementEnd
