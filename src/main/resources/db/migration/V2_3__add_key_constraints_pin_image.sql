-- pin to pin_image
-- 핀이 삭제되면 이미지도 같이 삭제되어야한다.
ALTER TABLE pin_image
    DROP CONSTRAINT fk9euf7l8r3dqhjh77nqs3yilh0;

ALTER TABLE pin_image
    ADD CONSTRAINT fk9euf7l8r3dqhjh77nqs3yilh0
        FOREIGN KEY (pin_id) REFERENCES pin(id)
            ON DELETE CASCADE;