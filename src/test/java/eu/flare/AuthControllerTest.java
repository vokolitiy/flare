package eu.flare;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.flare.model.response.Responses;
import eu.flare.service.AuthService;
import eu.flare.service.JwtService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class AuthControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_signUp_then_expect_ok_response() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/api/v1/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(testAuthJson("Testsub", "Subject", "Averages", "Joei","testsub@email.com", "Traders", "USER"))
        ).andExpect(status().isOk())
                .andReturn();

        Assertions.assertThat(result).isNotNull();
        String json = result.getResponse().getContentAsString();
        Assertions.assertThat(json).isNotEmpty();

        Responses.UserSignedUpResponse response = objectMapper.readValue(json, Responses.UserSignedUpResponse.class);
        Assertions.assertThat(response.userId()).isNotNegative();
        Assertions.assertThat(response.username()).isNotEmpty();
        Assertions.assertThat(response.firstName()).isNotEmpty();
        Assertions.assertThat(response.middleName()).isNotEmpty();
        Assertions.assertThat(response.lastName()).isNotEmpty();
    }

    @Test
    public void test_signup_the_same_credentials() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/signup").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testAuthJson("Testsub", "Subject", "Averages", "Joei","testsub@email.com", "Traders", "USER"))
                ).andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void test_signup_empty_email() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/signup").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testAuthJsonEmptyEmail())
                ).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void test_signup_too_long_email() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/signup").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testAuthJsonTooLongEmail())
                ).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void test_signup_empty_body() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/signup").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testAuthEmptyJson())
                ).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void test_login() throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testLoginJson())
                ).andExpect(status().isOk())
                .andReturn();

        Assertions.assertThat(result).isNotNull();
        String json = result.getResponse().getContentAsString();
        Assertions.assertThat(json).isNotEmpty();

        Responses.UserLoggedInResponse response = objectMapper.readValue(json, Responses.UserLoggedInResponse.class);
        Assertions.assertThat(response.token()).isNotEmpty();
        Assertions.assertThat(response.expiry()).isGreaterThan(0L);
    }

    @Test
    public void test_login_emptyuser() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testLoginJsonEmptyUsername())
                ).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void test_login_emptypassword() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testLoginJsonEmptyPassword())
                ).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void test_login_emptybody() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testLoginJsonEmptyPasswordAndUser())
                ).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void test_login_user_not_exists() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testLoginJsonNonExistentUser())
                ).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void test_logout() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/signup").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testLogoutUser())
                ).andExpect(status().isOk())
                .andReturn();

        MvcResult loginResult = mockMvc.perform(
                        post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(testLogoutJson())
                ).andExpect(status().isOk())
                .andReturn();

        Assertions.assertThat(loginResult).isNotNull();
        String json = loginResult.getResponse().getContentAsString();
        Assertions.assertThat(json).isNotEmpty();

        Responses.UserLoggedInResponse response = objectMapper.readValue(json, Responses.UserLoggedInResponse.class);
        String token = response.token();
        Assertions.assertThat(token).isNotEmpty();
        Assertions.assertThat(response.expiry()).isGreaterThan(0L);
        String bearerToken = "Bearer " + token;
        mockMvc.perform(
                        post("/api/v1/auth/logout").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                ).andExpect(status().isOk())
                .andReturn();
    }
}