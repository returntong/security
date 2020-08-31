package com.security.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Table
@Entity(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String mobile;

    private String nickName;

    private boolean enabled;

    private Date expiredDate;

    private boolean accountLocked;

    private Date createTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLE")
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ORGANIZATION")
    private Set<Organization> organizations;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<Permission> permSet = new HashSet<>();

        this.roles.forEach(role -> permSet.addAll(role.getPermissions()));

        return permSet.stream().map(perm -> new SimpleGrantedAuthority(perm.getName())).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.expiredDate.after(new Date());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}