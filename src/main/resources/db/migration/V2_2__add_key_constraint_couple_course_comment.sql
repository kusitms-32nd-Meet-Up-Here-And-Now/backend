-- course to couple_course_comment
-- 코스가 삭제되면 저장된 커플 댓글도 사라져야 한다.
ALTER TABLE couple_course_comment
    DROP CONSTRAINT fkmilb71ogv4w6cxya4bhw1geco;

ALTER TABLE couple_course_comment
    ADD CONSTRAINT fkmilb71ogv4w6cxya4bhw1geco
        FOREIGN KEY (course_id) REFERENCES course(id)
            ON DELETE CASCADE;