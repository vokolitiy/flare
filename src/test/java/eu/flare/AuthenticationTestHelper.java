package eu.flare;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.flare.model.dto.LoginDto;
import eu.flare.model.response.Responses;
import eu.flare.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class AuthenticationTestHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    public String authToken() throws Exception{
        Assertions.assertThat(userRepository.findAll()).isNotEmpty();
        String username = userRepository.findAll().getFirst().getUsername();
        String password = "admin_admin";
        String loginJson = testLoginJson(username, password);
        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(loginJson)
                ).andExpect(status().isOk())
                .andReturn();
        Assertions.assertThat(result).isNotNull();
        String json = result.getResponse().getContentAsString();
        Assertions.assertThat(json).isNotEmpty();

        Responses.UserLoggedInResponse response = objectMapper.readValue(json, Responses.UserLoggedInResponse.class);
        String token = response.token();
        Assertions.assertThat(token).isNotEmpty();
        return "Bearer " + token;
    }

    private String testLoginJson(String username, String password) {
        try {
            return objectMapper.writeValueAsString(new LoginDto(
                    username, password
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
