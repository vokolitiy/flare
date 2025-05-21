package eu.flare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import eu.flare.model.response.Responses;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
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

import java.text.MessageFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BoardControllerTest extends BaseIntegrationTest {

    @Autowired
    private AuthenticationTestHelper helper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    public void beforeTests() throws Exception {
        authToken = helper.authToken();
    }

    @Test
    @Order(1)
    public void test_createboard_should_return_created() throws Exception {
        mockMvc.perform(
                post("/api/v1/board/create").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testCreateBoardJson("Board"))
        ).andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    public void test_createboard_should_return_conflict() throws Exception {
        mockMvc.perform(
                post("/api/v1/board/create").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testCreateBoardJson("Board"))
        ).andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    public void test_addBoardToSprint_should_return_ok() throws Exception {
        MvcResult createProjectResult = mockMvc.perform(
                        post("/api/v1/project/create").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testCreateProjectJson("BoardControllerTest"))
                ).andExpect(status().isCreated())
                .andReturn();

        String json = createProjectResult.getResponse().getContentAsString();
        Assertions.assertThat(json).isNotEmpty();
        Responses.CreateProjectResponse createProjectResponse = objectMapper.readValue(json, Responses.CreateProjectResponse.class);
        long id = createProjectResponse.project().id();

        MvcResult createSprintResult = mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/sprints/add", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddSprintJson("BoardControllerTestSprint"))
                ).andExpect(status().isOk())
                .andReturn();
        String createSprintJson = createSprintResult.getResponse().getContentAsString();;
        Integer sprintId = JsonPath.read(createSprintJson, "$.project.sprints[0].id");
        Assertions.assertThat(sprintId).isNotNegative();

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/board/1/sprint/{0}/add", sprintId)).accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void test_addBoardToSprint_should_return_not_found() throws Exception {
        MvcResult readSprintResult = mockMvc.perform(
                        get("/api/v1/sprint?name=BoardControllerTestSprint").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                ).andExpect(status().isOk())
                .andReturn();

        String json = readSprintResult.getResponse().getContentAsString();
        Assertions.assertThat(json).isNotEmpty();
        Integer id = JsonPath.read(json, "$.sprint.id");

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/board/10/sprint/{0}/add", id)).accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void test_addBoardToSprint_should_return_sprintNotFound() throws Exception {
        MvcResult createBoardResult = mockMvc.perform(
                post("/api/v1/board/create").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testCreateBoardJson("BoardTwo"))
        ).andExpect(status().isCreated()).andReturn();

        String createBoardJson = createBoardResult.getResponse().getContentAsString();
        Integer boardId = JsonPath.read(createBoardJson, "$.board.id");
        Assertions.assertThat(boardId).isNotNegative();

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/board/{0}/sprint/76/add", boardId)).accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    public void test_addBoardToSprint_should_return_conflict() throws Exception {
        mockMvc.perform(
                put("/api/v1/board/1/sprint/1/add").accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isConflict());
    }

    @Test
    @Order(7)
    public void test_refresh_board_shouldreturn_ok() throws Exception {
        MvcResult searchBoardResult = mockMvc.perform(
                get("/api/v1/board?name=Board").accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isOk()).andReturn();

        String json = searchBoardResult.getResponse().getContentAsString();
        Integer id = JsonPath.read(json, "$.board.id");
        Assertions.assertThat(id).isNotNegative();

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/board/{0}/refresh", id)).accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(8)
    public void test_refresh_board_shouldreturn_notfound() throws Exception {
        mockMvc.perform(
                put("/api/v1/board/10000/refresh").accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isNotFound());
    }
}
