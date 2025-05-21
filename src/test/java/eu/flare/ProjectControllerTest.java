package eu.flare;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.flare.model.dto.add.AddStoryDto;
import eu.flare.repository.UserRepository;
import eu.flare.service.AuthService;
import eu.flare.service.JwtService;
import eu.flare.service.ProjectService;
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

import java.text.MessageFormat;
import java.util.Collections;
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
public class ProjectControllerTest extends BaseIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationTestHelper helper;

    private String authToken;

    @BeforeEach
    public void beforeTests() throws Exception {
        authToken = helper.authToken();
    }

    @Test
    @Order(1)
    public void test_create_project() throws Exception {
        mockMvc.perform(
                        post("/api/v1/project/create").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testCreateProjectJson("ProjectControllerTest"))
                ).andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    public void test_create_project_duplicate() throws Exception {
        mockMvc.perform(
                        post("/api/v1/project/create").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testCreateProjectJson("ProjectControllerTest"))
                ).andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    @Order(3)
    public void test_create_project_emptyname() throws Exception {
        mockMvc.perform(
                        post("/api/v1/project/create").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testCreateProjectJson(""))
                ).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Order(4)
    @Test
    public void test_create_project_verylong() throws Exception {
        mockMvc.perform(
                        post("/api/v1/project/create").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testCreateProjectJson("AbstractSingletonFactoryProxyBeanBridgeVisitorComposite"))
                ).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Order(5)
    public void test_project_query_for_epics() throws Exception {
        Integer id = getProjectId("ProjectControllerTest", authToken);

        mockMvc.perform(
                        get(MessageFormat.format("/api/v1/project/{0}/epics", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                ).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Order(6)
    public void test_project_add_epics() throws Exception {
        Integer id = getProjectId("ProjectControllerTest", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/project/{0}/epics/add", id)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddEpicsJson(List.of("Epic ONE")))
        ).andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(
                        get(MessageFormat.format("/api/v1/project/{0}/epics", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                ).andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Order(7)
    public void test_add_duplicate_epics() throws Exception {
        Integer id = getProjectId("ProjectControllerTest", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/epics/add", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddEpicsJson(List.of("Epic ONE")))
                ).andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    @Order(8)
    public void test_add_empty_epics() throws Exception {
        Integer id = getProjectId("ProjectControllerTest", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/epics/add", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddEpicsJson(Collections.emptyList()))
                ).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Order(9)
    public void test_add_epic_to_nonexistent_project() throws Exception {
        mockMvc.perform(
                        put("/api/v1/project/100/epics/add").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddEpicsJson(List.of("Epic TWO")))
                ).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Order(10)
    public void test_add_project_members() throws Exception {
        Integer id = getProjectId("ProjectControllerTest", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/members/add", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddMembersDtoJson(List.of("admin_admin")))
                ).andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @Order(11)
    public void test_add_nonexistent_project_members() throws Exception {
        Integer id = getProjectId("ProjectControllerTest", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/members/add", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddMembersDtoJson(List.of("serious_developer")))
                ).andExpect(status().is5xxServerError())
                .andReturn();
    }

    @Test
    @Order(12)
    public void test_add_project_members_to_nonexistent_project() throws Exception {
        mockMvc.perform(
                        put("/api/v1/project/100000/members/add").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddMembersDtoJson(List.of("admin_admin")))
                ).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Order(13)
    public void test_rename_project() throws Exception {
        Integer id = getProjectId("ProjectControllerTest", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/rename", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testRenameProjectJson("New Project XFS"))
                ).andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Order(14)
    public void test_rename_non_existent_project() throws Exception {
        mockMvc.perform(
                        put("/api/v1/project/100000000/rename").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testRenameProjectJson("New Project XFS"))
                ).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Order(15)
    public void test_add_sprints() throws Exception {
        Integer id = getProjectId("New Project XFS", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/sprints/add", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddSprintJson("New Sprint XFS"))
                ).andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Order(16)
    public void test_add_sprint_tononexistent_project() throws Exception {
        mockMvc.perform(
                        put("/api/v1/project/10000000/sprints/add").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddSprintJson("New Sprint XFS"))
                ).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Order(17)
    public void test_add_duplicate_sprint_name() throws Exception {
        Integer id = getProjectId("New Project XFS", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/sprints/add", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddSprintJson("New Sprint XFS"))
                ).andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    @Order(18)
    public void test_create_backlog() throws Exception {
        Integer id = getProjectId("New Project XFS", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/backlog/create", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testCreateBacklogJson("New Backlog XFS"))
                ).andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @Order(19)
    public void test_create_backlog_for_nonexistent_project() throws Exception {
        mockMvc.perform(
                        put("/api/v1/project/1000000/backlog/create").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testCreateBacklogJson("New Backlog XFSO"))
                ).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Order(20)
    public void test_create_duplicate_backlog() throws Exception {
        Integer id = getProjectId("New Project XFS", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/backlog/create", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testCreateBacklogJson("New Backlog XFS"))
                ).andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    @Order(21)
    public void test_add_mixed_epics() throws Exception {
        Integer id = getProjectId("New Project XFS", authToken);

        mockMvc.perform(
                        put(MessageFormat.format("/api/v1/project/{0}/epics/add", id)).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authToken)
                                .content(testAddEpicsJson(List.of("Epic ONE", "Epic TWO")))
                ).andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Order(22)
    public void test_add_stories_to_epic() throws Exception {
        Integer epicId = getEpicId("Epic ONE", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/epic/{0}/stories/add", epicId)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddStoryJson(
                                List.of(new AddStoryDto("Implement bottom navigation",
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
    @Order(23)
    public void test_add_stories_to_nonexistent_epic() throws Exception {
        mockMvc.perform(
                put("/api/v1/epic/100000000/stories/add").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddStoryJson(
                                List.of(new AddStoryDto("Implement bottom navigation",
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
                        ))).andExpect(status().isNotFound());
    }

    @Test
    @Order(24)
    public void test_add_empty_stories_to_epic() throws Exception {
        Integer epicId = getEpicId("Epic ONE", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/epic/{0}/stories/add", epicId)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddStoryJson(Collections.emptyList())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(25)
    public void test_add_duplicates_to_epic() throws Exception {
        Integer epicId = getEpicId("Epic ONE", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/epic/{0}/stories/add", epicId)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddStoryJson(
                                List.of(new AddStoryDto("Implement bottom navigation",
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
                        ))).andExpect(status().isConflict());
    }

    @Test
    @Order(26)
    public void test_rename_epic() throws Exception {
        Integer epicId = getEpicId("Epic ONE", authToken);

        mockMvc.perform(
                put(MessageFormat.format("/api/v1/epic/{0}/rename", epicId)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testRenameJson("Renamed epic"))).andExpect(status().isOk());
    }

    @Test
    @Order(27)
    public void test_rename_non_existent_epic() throws Exception {
        mockMvc.perform(
                put("/api/v1/epic/100000000000000/rename").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testRenameJson("Renamed epic"))).andExpect(status().isNotFound());
    }

    @Test
    @Order(28)
    public void test_add_stories_to_backlog_should_return_ok() throws Exception {
        mockMvc.perform(
                put("/api/v1/backlog/1/stories/add").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddBacklogStoriesJson(List.of(1)))
        ).andExpect(status().isOk());
    }

    @Test
    @Order(29)
    public void test_add_nonexistentstories_to_backlog_should_return_servererror() throws Exception {
        mockMvc.perform(
                put("/api/v1/backlog/1/stories/add").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddBacklogStoriesJson(List.of(2,3,4,5,6,7)))
        ).andExpect(status().is5xxServerError());
    }

    @Test
    @Order(30)
    public void test_add_stories_to_backlog_should_return_notfound() throws Exception {
        mockMvc.perform(
                put("/api/v1/backlog/1000/stories/add").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .content(testAddBacklogStoriesJson(List.of(1)))
        ).andExpect(status().isNotFound());
    }
}
