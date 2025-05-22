package eu.flare;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import eu.flare.model.dto.CreateBoardDto;
import eu.flare.model.dto.EmptyProjectDto;
import eu.flare.model.dto.LoginDto;
import eu.flare.model.dto.SignupDto;
import eu.flare.model.dto.add.*;
import eu.flare.model.dto.rename.RenameEpicDto;
import eu.flare.model.dto.rename.RenameProjectDto;
import eu.flare.model.dto.rename.RenameSprintDto;
import eu.flare.model.dto.rename.RenameStoryDto;
import eu.flare.model.dto.update.UpdateEstimateDto;
import eu.flare.model.dto.update.UpdateStoryPointsDto;
import eu.flare.model.dto.update.UpdateStoryPriorityDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    protected String testLoginJsonNonExistentUser() {
        try {
            return objectMapper.writeValueAsString(new LoginDto(
                    "Testsub DoesnotExist", "Traders"
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testLogoutUser() {
        try {
            return objectMapper.writeValueAsString(new SignupDto(
                    "Testsub Logout", "Subject", "Averages", "Joei","logout@email.com", "Traders", "USER"
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testLogoutJson() {
        try {
            return objectMapper.writeValueAsString(new LoginDto(
                    "Testsub Logout", "Traders"
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testLoginJsonEmptyUsername() {
        try {
            return objectMapper.writeValueAsString(new LoginDto(
                    "", "Traders"
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testLoginJsonEmptyPassword() {
        try {
            return objectMapper.writeValueAsString(new LoginDto(
                    "Testsub", ""
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testLoginJsonEmptyPasswordAndUser() {
        try {
            return objectMapper.writeValueAsString(new LoginDto(
                    "", ""
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testAuthJson() {
        try {
            return objectMapper.writeValueAsString(new SignupDto(
                    "Testsub", "Subject", "Averages", "Joei","testsub@email.com", "Traders", "USER"
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testAuthJsonEmptyEmail() {
        try {
            return objectMapper.writeValueAsString(new SignupDto(
                    "Testsub Sub", "Subject", "Averages", "Joei","", "Traders", "USER"
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testAuthJsonTooLongEmail() {
        try {
            return objectMapper.writeValueAsString(new SignupDto(
                    "Testsub Subb", "Subject", "Averages", "Joei","testsubsubsubsubsubsubsubsubsubsubtestsubsubsubsubsubsubsubsubsubsub@email.com", "Traders", "USER"
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testAuthEmptyJson() {
        try {
            return objectMapper.writeValueAsString(new SignupDto(
                    "", "", "", "","", "", ""
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testLoginJson() {
        try {
            return objectMapper.writeValueAsString(new LoginDto(
                    "Testsub", "Traders"
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testCreateBoardJson(String board) {
        try {
            return objectMapper.writeValueAsString(new CreateBoardDto(board));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testAddSprintJson(String name) {
        try {
            return objectMapper.writeValueAsString(new AddSprintDto(name));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testCreateProjectJson(String newProject) {
        try {
            return objectMapper.writeValueAsString(new EmptyProjectDto(newProject));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected Integer getProjectId(String name, String authToken) throws Exception {
        MvcResult searchProjectResult = mockMvc.perform(
                get(MessageFormat.format("/api/v1/project?name={0}", name)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isOk()).andReturn();
        String searchProjectJson = searchProjectResult.getResponse().getContentAsString();
        Integer id = JsonPath.read(searchProjectJson, "$.project.id");
        Assertions.assertThat(id).isNotNegative();
        return id;
    }

    protected Integer getEpicId(String name,String authToken) throws Exception {
        MvcResult searchEpicResult = mockMvc.perform(
                get(MessageFormat.format("/api/v1/epic?name={0}", name)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isOk()).andReturn();

        String searchEpicJson = searchEpicResult.getResponse().getContentAsString();
        int id = JsonPath.read(searchEpicJson, "$.epic.id");
        Assertions.assertThat(id).isNotNegative();

        return id;
    }

    protected Integer getSprintId(String name, String authToken) throws Exception {
        MvcResult searchSprintResult = mockMvc.perform(
                get(MessageFormat.format("/api/v1/sprint?name={0}", name)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isOk()).andReturn();

        String searchSprintJson = searchSprintResult.getResponse().getContentAsString();
        int id = JsonPath.read(searchSprintJson, "$.sprint.id");
        Assertions.assertThat(id).isNotNegative();

        return id;
    }

    protected Integer getStoryId(String name, String authToken) throws Exception {
        MvcResult searchStoryResult = mockMvc.perform(
                get(MessageFormat.format("/api/v1/story?name={0}", name)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        ).andExpect(status().isOk()).andReturn();

        String searchStoryJson = searchStoryResult.getResponse().getContentAsString();
        int id = JsonPath.read(searchStoryJson, "$.story.id");
        Assertions.assertThat(id).isNotNegative();

        return id;
    }

    protected String testRenameJson(String renamedEpic) {
        try {
            return objectMapper.writeValueAsString(new RenameEpicDto(renamedEpic));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testAddStoryJson(List<AddStoryDto> dtos) {
        try {
            return objectMapper.writeValueAsString(dtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testAddEpicsJson(List<String> epicNames) {
        try {
            return objectMapper.writeValueAsString(new AddEpicsDto(epicNames));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testAddMembersDtoJson(List<String> members) {
        List<AddMembersDto> membersDtos = members.stream().map(AddMembersDto::new).toList();
        try {
            return objectMapper.writeValueAsString(membersDtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testRenameProjectJson(String newName) {
        try {
            return objectMapper.writeValueAsString(new RenameProjectDto(newName));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testCreateBacklogJson(String name) {
        try {
            return objectMapper.writeValueAsString(new AddBacklogDto(name));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testAddBacklogStoriesJson(List<Integer> ids) {
        List<AddBacklogStoryDto> collect = ids.stream().map(AddBacklogStoryDto::new).toList();
        try {
            return objectMapper.writeValueAsString(collect);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testRenameStoryJson(String newStoryName) {
        try {
            return objectMapper.writeValueAsString(new RenameStoryDto(newStoryName));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testUpdatePriorityJson(String newPriority) {
        try {
            return objectMapper.writeValueAsString(new UpdateStoryPriorityDto(newPriority));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testUpdateOriginalEstimateJson(long millis) {
        try {
            return objectMapper.writeValueAsString(new UpdateEstimateDto(millis));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testUpdateStoryPointsJson(int points) {
        try {
            return objectMapper.writeValueAsString(new UpdateStoryPointsDto(points));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testRenameSprintJson(String newName) {
        try {
            return objectMapper.writeValueAsString(new RenameSprintDto(newName));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testAddStoriesIdsJson(List<Integer> ids) {
        List<AddSprintStoryDto> dtos = ids.stream().map(AddSprintStoryDto::new)
                .toList();
        try {
            return objectMapper.writeValueAsString(dtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
