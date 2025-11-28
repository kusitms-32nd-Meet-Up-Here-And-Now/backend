-- pin to pin_tag
-- 핀이 삭제되면 핀 태그도 같이 삭제되어야 한다.
alter table pin_tag
    drop constraint fkf6270xy9g8kby20ngjptttneg;

alter table pin_tag
    add constraint fkf6270xy9g8kby20ngjptttneg
        foreign key (pin_id) references pin (id)
            on delete cascade;