package eu.flare.config;

import eu.flare.config.seed.*;
import eu.flare.model.Privilege;
import eu.flare.model.Role;
import eu.flare.model.User;
import eu.flare.repository.PrivilegeRepository;
import eu.flare.repository.RoleRepository;
import eu.flare.repository.UserRepository;
import eu.flare.repository.story.StoryPriorityRepository;
import eu.flare.repository.story.StoryProgressRepository;
import eu.flare.repository.story.StoryResolutionRepository;
import eu.flare.repository.task.TaskPriorityRepository;
import eu.flare.repository.task.TaskProgressRepository;
import eu.flare.repository.task.TaskResolutionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Configuration
@Transactional
public class BootstrapDataConfigurer implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final StoryProgressRepository storyProgressRepository;
    private final StoryPriorityRepository storyPriorityRepository;
    private final StoryResolutionRepository storyResolutionRepository;
    private final TaskProgressRepository taskProgressRepository;
    private final TaskPriorityRepository taskPriorityRepository;
    private final TaskResolutionRepository taskResolutionRepository;
    private final PasswordEncoder passwordEncoder;

    private boolean alreadySetup = false;

    @Autowired
    public BootstrapDataConfigurer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PrivilegeRepository privilegeRepository,
            StoryProgressRepository storyProgressRepository,
            StoryPriorityRepository storyPriorityRepository,
            StoryResolutionRepository storyResolutionRepository,
            TaskProgressRepository taskProgressRepository,
            TaskPriorityRepository taskPriorityRepository,
            TaskResolutionRepository taskResolutionRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.storyProgressRepository = storyProgressRepository;
        this.storyPriorityRepository = storyPriorityRepository;
        this.storyResolutionRepository = storyResolutionRepository;
        this.taskProgressRepository = taskProgressRepository;
        this.taskPriorityRepository = taskPriorityRepository;
        this.taskResolutionRepository = taskResolutionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        DataSeeder seederChain = seederChain();
        seederChain.seedData(List.of("Todo", "In Progress", "In review", "Done"));
        seederChain.seedData(List.of("Minor", "Major", "Severe", "Blocker"));
        seederChain.seedData(List.of("Done", "Will not fix"));
        seederChain.seedData(List.of("Todo", "In Progress", "In review", "Done"));
        seederChain.seedData(List.of("Minor", "Major", "Severe", "Blocker"));
        seederChain.seedData(List.of("Done", "Will not fix"));

        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Collections.singletonList(readPrivilege));

        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole.isEmpty()) {
            return;
        }
        User user = new User();
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEmail("admin@admin.com");
        user.setCredentialsNonExpired(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setRoles(List.of(adminRole.get()));
        user.setEnabled(true);
        userRepository.save(user);

        alreadySetup = true;
    }

    @Transactional
    private Privilege createPrivilegeIfNotFound(String name) {
        Optional<Privilege> privilegeOptional = privilegeRepository.findByName(name);
        if (privilegeOptional.isEmpty()) {
            Privilege privilege = new Privilege();
            privilege.setName(name);
            privilegeRepository.save(privilege);
            return privilege;
        }
        return privilegeOptional.get();
    }

    @Transactional
    private void createRoleIfNotFound(String roleName, List<Privilege> privileges) {
        Optional<Role> optionalRole = roleRepository.findByName(roleName);
        if (optionalRole.isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
    }

    private DataSeeder seederChain() {
        DataSeeder storyProgressSeeder = new StoryProgressSeeder(storyProgressRepository);
        DataSeeder storyPrioritySeeder = new StoryPrioritySeeder(storyPriorityRepository);
        DataSeeder storyResolutionSeeder = new StoryResolutionSeeder(storyResolutionRepository);
        DataSeeder taskProgressSeeder = new TaskProgressSeeder(taskProgressRepository);
        DataSeeder taskPrioritySeeder = new TaskPrioritySeeder(taskPriorityRepository);
        DataSeeder taskResolutionSeeder = new TaskResolutionSeeder(taskResolutionRepository);

        storyProgressSeeder.setNextSeeder(storyPrioritySeeder);
        storyPrioritySeeder.setNextSeeder(storyResolutionSeeder);
        storyResolutionSeeder.setNextSeeder(taskProgressSeeder);
        taskProgressSeeder.setNextSeeder(taskPrioritySeeder);
        taskPrioritySeeder.setNextSeeder(taskResolutionSeeder);
        taskResolutionSeeder.setNextSeeder(null);

        return storyProgressSeeder;
    }
}
