package eu.flare;

import com.jayway.jsonpath.JsonPath;
import eu.flare.model.dto.add.AddStoryDto;
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
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SprintControllerTest extends BaseIntegrationTest {

    @Autowired
    private AuthenticationTestHelper helper;

    @Autowired
    private MockMvc mockMvc;

    private String authToken;

    @BeforeEach
    public void beforeTests() throws Exception {
        authToken = helper.authToken();
    }

    @Test
    @Order(1)
    public void test_get_sprint_should_return_ok() throws Exception {
        mockMvc.perform(
                post("/api/v1/project/create").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testCreateProjectJson("SprintControllerTest"))
        ).andExpect(status().isCreated());

        Integer projectId = getProjectId("SprintControllerTest", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/sprints/add", projectId)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddSprintJson("SCTSprint"))
                ).andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Order(2)
    public void test_rename_sprint_should_return_ok() throws Exception {
        Integer sprintId = getSprintId("SCTSprint", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/sprint/{0}/rename", sprintId)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testRenameSprintJson("SCTSprintN"))
        ).andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void test_rename_sprint_should_return_notfound() throws Exception {
        mockMvc.perform(
                put("/api/v1/sprint/1223445555/rename").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testRenameSprintJson("SCTSprintN"))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    public void test_start_sprint_should_return_started() throws Exception {
        Integer sprintId = getSprintId("SCTSprintN", authToken);

        MvcResult startResult = mockMvc.perform(
                post(MessageFormat.format("/api/v1/sprint/{0}/start", sprintId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isOk()).andReturn();

        String startJson = startResult.getResponse().getContentAsString();
        Boolean isStarted = JsonPath.read(startJson, "$.sprint.started");
        Assertions.assertThat(isStarted).isTrue();
    }

    @Test
    @Order(5)
    public void test_start_sprint_should_return_conflict() throws Exception {
        Integer sprintId = getSprintId("SCTSprintN", authToken);

        mockMvc.perform(
                post(MessageFormat.format("/api/v1/sprint/{0}/start", sprintId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isConflict());
    }

    @Test
    @Order(6)
    public void test_start_sprint_should_return_notfound() throws Exception {
        mockMvc.perform(
                post("/api/v1/sprint/172727727272/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    public void test_complete_sprint_should_return_completed() throws Exception {
        Integer sprintId = getSprintId("SCTSprintN", authToken);

        MvcResult completeResult = mockMvc.perform(
                post(MessageFormat.format("/api/v1/sprint/{0}/complete", sprintId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isOk()).andReturn();

        String completeJson = completeResult.getResponse().getContentAsString();
        Boolean isCompleted = JsonPath.read(completeJson, "$.sprint.completed");
        Assertions.assertThat(isCompleted).isTrue();
    }

    @Test
    @Order(8)
    public void test_complete_sprint_should_return_notfound() throws Exception {
        mockMvc.perform(
                post("/api/v1/sprint/17772727377373737/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    public void test_complete_sprint_already_completed() throws Exception {
        Integer sprintId = getSprintId("SCTSprintN", authToken);

        mockMvc.perform(
                post(MessageFormat.format("/api/v1/sprint/{0}/complete", sprintId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @Order(10)
    public void test_complete_not_started_sprint() throws Exception {
        Integer projectId = getProjectId("SprintControllerTest", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/sprints/add", projectId)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddSprintJson("SCTSprintNew"))
                ).andExpect(status().isOk())
                .andReturn();

        Integer sprintId = getSprintId("SCTSprintNew", authToken);

        mockMvc.perform(
                post(MessageFormat.format("/api/v1/sprint/{0}/complete", sprintId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @Order(11)
    public void test_add_stories_should_return_ok() throws Exception {
        Integer id = getProjectId("SprintControllerTest", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/epics/add", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddEpicsJson(List.of("Epic NEUU")))
                ).andExpect(status().isOk())
                .andReturn();

        Integer epicId = getEpicId("Epic NEUU", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/epic/{0}/stories/add", epicId)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddStoryJson(
                                List.of(new AddStoryDto("Story-X11",
                                        "Implement bottom navigation using Jetpack navigation library",
                                        86400,
                                        8400,
                                        90,
                                        30,
                                        "Major",
                                        "Todo",
                                        "admin_admin",
                                        "admin_admin",
                                        List.of("admin_admin")), new AddStoryDto("Story-X12",
                                        "Implement bottom navigation using Jetpack navigation library",
                                        86400,
                                        8400,
                                        90,
                                        30,
                                        "Major",
                                        "Todo",
                                        "admin_admin",
                                        "admin_admin",
                                        List.of("admin_admin")), new AddStoryDto("Story-X13",
                                        "Implement bottom navigation using Jetpack navigation library",
                                        86400,
                                        8400,
                                        90,
                                        30,
                                        "Major",
                                        "Todo",
                                        "admin_admin",
                                        "admin_admin",
                                        List.of("admin_admin")))
                        ))).andExpect(status().isCreated());

        List<Integer> stories = List.of(getStoryId("Story-X11", authToken), getStoryId("Story-X13", authToken));
        Integer sprintId = getSprintId("SCTSprintNew", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/sprint/{0}/stories/add", sprintId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddStoriesIdsJson(stories))
        ).andExpect(status().isOk());
    }

    @Test
    @Order(12)
    public void test_add_stories_should_return_badrequest() throws Exception {
        List<Integer> stories = List.of(getStoryId("Story-X11", authToken), getStoryId("Story-X13", authToken));
        Integer sprintId = getSprintId("SCTSprintN", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/sprint/{0}/stories/add", sprintId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddStoriesIdsJson(stories))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @Order(13)
    public void test_add_stories_should_return_notfound() throws Exception {
        List<Integer> stories = List.of(getStoryId("Story-X11", authToken), getStoryId("Story-X13", authToken));

        mockMvc.perform(
                put("/api/v1/sprint/1828282891919/stories/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddStoriesIdsJson(stories))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(14)
    public void test_add_emptystories_should_return_badrequest() throws Exception {
        List<Integer> stories = Collections.emptyList();
        Integer sprintId = getSprintId("SCTSprintNew", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/sprint/{0}/stories/add", sprintId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddStoriesIdsJson(stories))
        ).andExpect(status().isBadRequest());
    }
}
