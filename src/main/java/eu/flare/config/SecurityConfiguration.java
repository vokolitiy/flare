package eu.flare.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter authenticationFilter;

    @Autowired
    public SecurityConfiguration(JwtAuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchy = "ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER";
        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        httpSecurity.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.authorizeHttpRequests(authorizeHttpRequests -> {
            authorizeHttpRequests
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    .requestMatchers("/api/v1/auth/signup").permitAll()
                    .requestMatchers("/api/v1/auth/logout").permitAll()
                    .requestMatchers("/api/v1/project").authenticated()
                    .requestMatchers("/api/v1/project/create").authenticated()
                    .requestMatchers("/api/v1/project/{id}/epics").authenticated()
                    .requestMatchers("/api/v1/project/{id}/epics/add").authenticated()
                    .requestMatchers("/api/v1/project/{id}/members/add").authenticated()
                    .requestMatchers("/api/v1/project/{id}/rename").authenticated()
                    .requestMatchers("/api/v1/project/{id}/sprints/add").authenticated()
                    .requestMatchers("/api/v1/project/{id}/backlog/create").authenticated()
                    .requestMatchers("/api/v1/epic").authenticated()
                    .requestMatchers("/api/v1/epic/{id}/stories/add").authenticated()
                    .requestMatchers("/api/v1/epic/{id}/rename").authenticated()
                    .requestMatchers("/api/v1/story").authenticated()
                    .requestMatchers("/api/v1/story/{id}/tasks/add").authenticated()
                    .requestMatchers("/api/v1/story/{id}/rename").authenticated()
                    .requestMatchers("/api/v1/story/{id}/priority/update").authenticated()
                    .requestMatchers("/api/v1/story/{id}/progress/update").authenticated()
                    .requestMatchers("/api/v1/story/{id}/resolution/update").authenticated()
                    .requestMatchers("/api/v1/task").authenticated()
                    .requestMatchers("/api/v1/task/{id}/rename").authenticated()
                    .requestMatchers("/api/v1/backlog").authenticated()
                    .requestMatchers("/api/v1/backlog/{id}/stories/add").authenticated()
                    .requestMatchers("/api/v1/sprint").authenticated()
                    .requestMatchers("/api/v1/sprint/{id}/rename").authenticated()
                    .requestMatchers("/api/v1/sprint/{id}/start").authenticated()
                    .requestMatchers("/api/v1/sprint/{id}/complete").authenticated()
                    .requestMatchers("/api/v1/sprint/{id}/stories/add").authenticated()
                    .anyRequest().permitAll();
        });
        httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
