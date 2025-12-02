package com.meetup.hereandnow.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetup.hereandnow.auth.application.jwt.AccessTokenService;
import com.meetup.hereandnow.auth.application.jwt.RefreshTokenService;
import com.meetup.hereandnow.auth.dto.request.ReIssueTokenRequest;
import com.meetup.hereandnow.auth.dto.request.TokenIssueRequest;
import com.meetup.hereandnow.auth.infrastructure.jwt.TokenProvider;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
import com.meetup.hereandnow.core.config.TestSecurityConfiguration;
import com.meetup.hereandnow.member.domain.Member;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfiguration.class)
class AuthControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AccessTokenService accessTokenService;


    @Autowired
    private ObjectMapper objectMapper;

    private final Long MEMBER_ID = 1L;
    private final String AUTH_KEY = "auth-key";

    private MockedStatic<SecurityUtils> mockedSecurity;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        accessTokenService.deleteToken(AUTH_KEY);
        refreshTokenService.deleteToken(MEMBER_ID);

        mockMember = Member.builder().id(MEMBER_ID).build();
        mockedSecurity = mockStatic(SecurityUtils.class);
        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }


    @Nested
    @DisplayName("POST /auth/token")
    class GetToken {

        @Test
        @DisplayName("authKey로 AccessToken, RefreshToken 발급 성공")
        void success_issue_token() throws Exception {

            // given
            String accessToken = tokenProvider.createAccessToken(MEMBER_ID);
            String refreshToken = tokenProvider.createRefreshToken(MEMBER_ID);

            TokenIssueRequest request = new TokenIssueRequest(AUTH_KEY);

            accessTokenService.saveToken(
                    AUTH_KEY,
                    accessToken,
                    Duration.ofHours(1)
            );

            refreshTokenService.saveToken(
                    MEMBER_ID,
                    refreshToken,
                    Duration.ofHours(1)
            );

            // when & then
            mockMvc.perform(post("/auth/token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists());
        }
    }

    @Nested
    @DisplayName("POST /auth/logout")
    class Logout {

        @Test
        @DisplayName("로그아웃에 성공한다.")
        void success_logout() throws Exception {
            // given
            String refreshToken = tokenProvider.createRefreshToken(MEMBER_ID);
            refreshTokenService.saveToken(
                    MEMBER_ID,
                    refreshToken,
                    Duration.ofHours(1)
            );

            // when & then
            mockMvc.perform(post("/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.data.isSuccess").value(true))
                    .andExpect(jsonPath("$.data.message").value("성공적으로 로그아웃 되었습니다."));
        }
    }

    @Nested
    @DisplayName("POST /token/re-issue")
    class ReissueToken {

        @Test
        @DisplayName("토큰 재발행에 성공한다.")
        void success_token_re_issue() throws Exception {
            // given
            String refreshToken = tokenProvider.createRefreshToken(MEMBER_ID);

            refreshTokenService.saveToken(
                    MEMBER_ID,
                    refreshToken,
                    Duration.ofHours(1)
            );

            ReIssueTokenRequest request = new ReIssueTokenRequest(refreshToken);

            // when & then
            mockMvc.perform(post("/auth/re-issue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists());
        }
    }
}