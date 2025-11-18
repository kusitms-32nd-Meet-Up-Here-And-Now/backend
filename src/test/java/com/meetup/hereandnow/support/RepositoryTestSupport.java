package com.meetup.hereandnow.support;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
@Testcontainers
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class RepositoryTestSupport {

    private static final String POSTGIS_IMAGE = "postgis/postgis:16-3.4";

    private static final DockerImageName postgisImage = DockerImageName.parse(POSTGIS_IMAGE)
            .asCompatibleSubstituteFor("postgres");

    @Container
    public static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(postgisImage)
                    .withDatabaseName("testdb")
                    .withUsername("sa")
                    .withPassword("sa");

    @DynamicPropertySource
    public static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("POSTGRES_HOST", postgreSQLContainer::getHost);
        registry.add("POSTGRES_PORT", postgreSQLContainer::getFirstMappedPort);
        registry.add("POSTGRES_DB", postgreSQLContainer::getDatabaseName);
        registry.add("POSTGRES_USER", postgreSQLContainer::getUsername);
        registry.add("POSTGRES_PASSWORD", postgreSQLContainer::getPassword);
    }
}
