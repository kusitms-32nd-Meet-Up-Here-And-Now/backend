-- course to course_comment
-- 코스가 삭제되면 코스 댓글도 함꼐 사라져야한다.
ALTER TABLE course_comment
    DROP CONSTRAINT fki2x4qwjfqowv1gxbt2ybxo9il;


ALTER TABLE course_comment
    ADD CONSTRAINT fki2x4qwjfqowv1gxbt2ybxo9il
        FOREIGN KEY (course_id) REFERENCES course(id)
            ON DELETE CASCADE;

-- course to pin
-- 코스가 삭제되면 핀도 같이 삭제되어야한다.
ALTER TABLE pin
    DROP CONSTRAINT fkk10of7nblwu0u7pi8c1rwm7ob;

ALTER TABLE pin
    ADD CONSTRAINT fkk10of7nblwu0u7pi8c1rwm7ob
        FOREIGN KEY (course_id) REFERENCES course(id)
            ON DELETE CASCADE;