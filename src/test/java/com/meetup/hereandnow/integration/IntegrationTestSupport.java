package com.meetup.hereandnow.integration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("resource")
public abstract class IntegrationTestSupport {

    private static final String POSTGIS_IMAGE = "postgis/postgis:16-3.4";

    private static final DockerImageName postgisImage = DockerImageName.parse(POSTGIS_IMAGE)
            .asCompatibleSubstituteFor("postgres");

    private static final String REDIS_IMAGE_NAME = "redis:7.0.8-alpine";

    @Container
    public static final GenericContainer<?> redisContainer =
            new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE_NAME)).withExposedPorts(6379);

    @Container
    public static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(postgisImage)
                    .withDatabaseName("testdb")
                    .withUsername("sa")
                    .withPassword("sa");

    @BeforeAll
    public static void startContainers() {
        if (!postgreSQLContainer.isRunning()) {
            postgreSQLContainer.start();
        }
        if (!redisContainer.isRunning()) {
            redisContainer.start();
        }
    }

    /*
    yml 시크릿 값 덮어쓰기 위한 메서드 (전부 임의 값 설정) 
     */
    @DynamicPropertySource
    public static void setDatasourceProperties(DynamicPropertyRegistry registry) {

        registry.add("POSTGRES_HOST", postgreSQLContainer::getHost);
        registry.add("POSTGRES_PORT", postgreSQLContainer::getFirstMappedPort);
        registry.add("POSTGRES_DB", postgreSQLContainer::getDatabaseName);
        registry.add("POSTGRES_USER", postgreSQLContainer::getUsername);
        registry.add("POSTGRES_PASSWORD", postgreSQLContainer::getPassword);

        registry.add("REDIS_HOST", redisContainer::getHost);
        registry.add("REDIS_PORT", () -> redisContainer.getMappedPort(6379).toString());
        registry.add("REDIS_USERNAME", () -> "default");
        registry.add("REDIS_PASSWORD", () -> "test");
        registry.add("REDIS_DB_NO", () -> "0");

        registry.add("GOOGLE_CLIENT_ID", () -> "test-google-client-id");
        registry.add("GOOGLE_CLIENT_SECRET", () -> "test-google-client-secret");
        registry.add("GOOGLE_OAUTH_CALLBACK", () -> "http://test-server.com/callback");
        registry.add("KAKAO_CLIENT_ID", () -> "test-kakao-client-id");
        registry.add("KAKAO_CLIENT_SECRET", () -> "test-kakao-client-secret");
        registry.add("KAKAO_OAUTH_CALLBACK", () -> "http://test-server.com/callback");
        registry.add("REDIRECT_URI", () -> "http://test-client.com/redirect");

        registry.add("SERVER_URL", () -> "http://test-server.com");

        registry.add("JWT_SECRET", () -> "ZHVtbXktdGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtYmVyeS12ZXJ5LWxvbmctZm9yLXRlc3RpbmctcHVycG9zZXM=");
        registry.add("JWT_ACCESS_EXP", () -> "3600000");
        registry.add("JWT_REFRESH_EXP", () -> "86400000");

        registry.add("CORS_ALLOWED_ORIGINS", () -> "http://test-client.com,http://localhost:3000");

        registry.add("NCP_STORAGE_ACCESS_KEY", () -> "test-ncp-access-key");
        registry.add("NCP_STORAGE_SECRET_KEY", () -> "test-ncp-secret-key");
        registry.add("NCP_STORAGE_REGION", () -> "kr-standard");
        registry.add("NCP_BUCKET_NAME", () -> "test-bucket");
    }
}
