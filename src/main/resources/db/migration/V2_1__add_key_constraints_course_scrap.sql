-- course to course_scrap
-- 코스가 삭제되면 저장된 코스 댓글도 사라져야 한다.
ALTER TABLE course_scrap
DROP CONSTRAINT fke6vjt9csulywxp4587twh0x69;

ALTER TABLE course_scrap
    ADD CONSTRAINT fke6vjt9csulywxp4587twh0x69
        FOREIGN KEY (course_id) REFERENCES course(id)
            ON DELETE CASCADE;