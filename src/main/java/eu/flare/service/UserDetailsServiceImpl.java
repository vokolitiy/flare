package eu.flare.service;

import eu.flare.model.Privilege;
import eu.flare.model.Role;
import eu.flare.model.User;
import eu.flare.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> appUser = userRepository.findByUsername(username);
        if (appUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else {
            User user = appUser.get();
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                    user.isAccountNonLocked(), getAuthorities(user.getRoles()));
        }
    }

    private List<GrantedAuthority> getAuthorities(List<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }


    private List<String> getPrivileges(Collection<Role> roles) {
       List<String> privileges = new ArrayList<>();
       List<Privilege> rolePrivileges = new ArrayList<>();
       roles.forEach(role -> {
           privileges.add(role.getName());
           rolePrivileges.addAll(role.getPrivileges());
       });
       rolePrivileges.forEach(item -> privileges.add(item.getName()));
       return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        privileges.forEach(privilege -> authorities.add(new SimpleGrantedAuthority(privilege)));
        return authorities;
    }
}
