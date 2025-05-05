package eu.flare.config;

import eu.flare.config.seed.*;
import eu.flare.model.Privilege;
import eu.flare.model.RefreshTokenStatus;
import eu.flare.model.Role;
import eu.flare.model.User;
import eu.flare.repository.PrivilegeRepository;
import eu.flare.repository.RefreshTokenRepository;
import eu.flare.repository.RoleRepository;
import eu.flare.repository.UserRepository;
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
public class BootstrapDataConfigurer implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoryPrioritySeeder storyPrioritySeeder;
    private final StoryProgressSeeder storyProgressSeeder;
    private final StoryResolutionSeeder storyResolutionSeeder;
    private final TaskPrioritySeeder taskPrioritySeeder;
    private final TaskProgressSeeder taskProgressSeeder;
    private final TaskResolutionSeeder taskResolutionSeeder;

    private boolean alreadySetup = false;

    @Autowired
    public BootstrapDataConfigurer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PrivilegeRepository privilegeRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            StoryPrioritySeeder storyPrioritySeeder,
            StoryProgressSeeder storyProgressSeeder,
            StoryResolutionSeeder storyResolutionSeeder,
            TaskPrioritySeeder taskPrioritySeeder,
            TaskProgressSeeder taskProgressSeeder,
            TaskResolutionSeeder taskResolutionSeeder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.storyPrioritySeeder = storyPrioritySeeder;
        this.storyProgressSeeder = storyProgressSeeder;
        this.storyResolutionSeeder = storyResolutionSeeder;
        this.taskPrioritySeeder = taskPrioritySeeder;
        this.taskProgressSeeder = taskProgressSeeder;
        this.taskResolutionSeeder = taskResolutionSeeder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!alreadySetup) {
            seedDefaultData();
            cleanupRevokedTokens();
        }
    }

    private void cleanupRevokedTokens() {
        refreshTokenRepository.findAll()
                .forEach(refreshToken -> {
                    if (refreshToken.getRefreshTokenStatus() == RefreshTokenStatus.REVOKED) {
                        refreshTokenRepository.delete(refreshToken);
                    }
                });
    }

    private void seedDefaultData() {
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        storyPrioritySeeder.createDataIfNotExists(List.of("Minor", "Major", "Severe", "Blocker"));
        storyProgressSeeder.createDataIfNotExists(List.of("Todo", "In Progress", "In review", "Done"));
        storyResolutionSeeder.createDataIfNotExists(List.of("Done", "Will not fix"));
        taskPrioritySeeder.createDataIfNotExists(List.of("Minor", "Major", "Severe", "Blocker"));
        taskProgressSeeder.createDataIfNotExists(List.of("Todo", "In Progress", "In review", "Done"));
        taskResolutionSeeder.createDataIfNotExists(List.of("Done", "Will not fix"));


        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Collections.singletonList(readPrivilege));

        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole.isEmpty()) {
            return;
        }
        Optional<User> userOptional = userRepository.findByUsername("admin_admin");
        if (userOptional.isEmpty()) {
            User user = new User();
            user.setFirstName("admin_admin");
            user.setLastName("admin_admin");
            user.setUsername("admin_admin");
            user.setPassword(passwordEncoder.encode("admin_admin"));
            user.setEmail("email@admin.com");
            user.setCredentialsNonExpired(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setRoles(List.of(adminRole.get()));
            user.setEnabled(true);
            userRepository.save(user);
        }

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
}
