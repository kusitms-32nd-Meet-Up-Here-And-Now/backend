package com.meetup.hereandnow.integration.support;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
public abstract class RepositoryTestSupport extends TestContainerSupport {

}
