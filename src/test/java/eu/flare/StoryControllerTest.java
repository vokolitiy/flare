package eu.flare;

import eu.flare.model.dto.add.AddStoryDto;
import eu.flare.model.dto.add.AddTaskDto;
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

import javax.print.attribute.standard.Media;
import java.text.MessageFormat;
import java.util.List;

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
public class StoryControllerTest extends BaseIntegrationTest {

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
    public void test_find_story_should_return_created() throws Exception{
        mockMvc.perform(
                post("/api/v1/project/create").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testCreateProjectJson("StoryControllerTest"))
        ).andExpect(status().isCreated());
        Integer projectId = getProjectId("StoryControllerTest", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/epics/add", projectId)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddEpicsJson(List.of("Epic THREE")))
                ).andExpect(status().isOk());
        Integer epicId = getEpicId("Epic THREE", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/epic/{0}/stories/add", epicId)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddStoryJson(
                                List.of(new AddStoryDto("Story-1",
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
    }

    @Test
    @Order(2)
    public void test_rename_story_should_return_ok() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/rename").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testRenameStoryJson("Story-X"))
        ).andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void test_rename_story_should_return_notfound() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/100/rename").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testRenameStoryJson("Story-X"))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    public void test_update_story_priority_should_return_ok() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/priority/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdatePriorityJson("Blocker"))
        ).andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void test_update_nonexistentstory_priority_should_return_ok() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/133344/priority/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdatePriorityJson("Blocker"))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    public void test_update_story_priority_should_return_badrequest() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/priority/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdatePriorityJson("Wrong"))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    public void test_update_story_progress_should_return_ok() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/progress/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdatePriorityJson("Done"))
        ).andExpect(status().isOk());
    }

    @Test
    @Order(8)
    public void test_update_story_priority_should_return_notfound() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/18383838/progress/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdatePriorityJson("Done"))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    public void test_update_story_progress_should_return_badrequest() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/progress/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdatePriorityJson("Unknown"))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @Order(10)
    public void test_update_story_resolution_should_return_ok() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/resolution/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdatePriorityJson("Done"))
        ).andExpect(status().isOk());
    }

    @Test
    @Order(11)
    public void test_update_story_resolution_should_return_notfound() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/18383838/resolution/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdatePriorityJson("Done"))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    public void test_update_story_resolution_should_return_badrequest() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/resolution/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdatePriorityJson("Unknown"))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @Order(13)
    public void test_update_original_estimate_should_return_ok() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/estimate/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdateOriginalEstimateJson(99999999))
        ).andExpect(status().isOk());
    }

    @Test
    @Order(14)
    public void test_update_original_estimate_should_return_notfound() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/183838380030/estimate/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdateOriginalEstimateJson(99999999))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(15)
    public void test_update_remaining_estimate_should_return_ok() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/estimate/remaining/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdateOriginalEstimateJson(789999))
        ).andExpect(status().isOk());
    }

    @Test
    @Order(16)
    public void test_update_remaining_estimate_should_return_notfound() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/183883838380/estimate/remaining/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdateOriginalEstimateJson(789999))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(17)
    public void test_update_remaining_estimate_should_return_5xx() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/estimate/remaining/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdateOriginalEstimateJson(599999999))
        ).andExpect(status().is5xxServerError());
    }

    @Test
    @Order(18)
    public void test_update_storypoints_should_return_ok() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/storypoints/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdateStoryPointsJson(23))
        ).andExpect(status().isOk());
    }

    @Test
    @Order(19)
    public void test_update_storypoints_should_return_notfound() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/18383838839991/storypoints/update").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testUpdateStoryPointsJson(23))
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(20)
    public void test_addtasks_should_return_created() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1/tasks/add").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddTasksJson(List.of(new AddTaskDto(
                                "Implement bottom navigation",
                                "Implement bottom navigation using Jetpack navigation library",
                                86400,
                                100,
                                50,
                                10,
                                "Major",
                                "Todo",
                                "admin_admin",
                                "admin_admin"
                        ))))
        ).andExpect(status().isCreated());
    }

    @Test
    @Order(21)
    public void test_addtasks_should_return_notfound() throws Exception {
        mockMvc.perform(
                put("/api/v1/story/1828282992929/tasks/add").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddTasksJson(List.of(new AddTaskDto(
                                "Implement bottom navigation",
                                "Implement bottom navigation using Jetpack navigation library",
                                86400,
                                100,
                                50,
                                10,
                                "Major",
                                "Todo",
                                "admin_admin",
                                "admin_admin"
                        ))))
        ).andExpect(status().isNotFound());
    }
}
