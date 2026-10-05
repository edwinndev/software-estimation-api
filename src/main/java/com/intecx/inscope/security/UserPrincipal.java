package com.intecx.inscope.security;

import com.intecx.inscope.entity.Permission;
import com.intecx.inscope.entity.User;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.active = user.isActive() && user.getDeletedAt() == null;

        Set<GrantedAuthority> auths = new HashSet<>();
        if (user.getRole() != null) {
            auths.add(new SimpleGrantedAuthority(user.getRole().getCode()));
            if (user.getRole().getPermissions() != null) {
                for (Permission perm : user.getRole().getPermissions()) {
                    auths.add(new SimpleGrantedAuthority(perm.getCode()));
                }
            }
        }
        this.authorities = auths;
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    @NonNull
    public String getPassword() {
        return password;
    }

    @Override
    @NonNull
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
