package com.huan.vhr_springboot.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huan.vhr_springboot.entity.LoginUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomUserDetails extends LoginUser implements UserDetails {
    public CustomUserDetails(LoginUser user){
        super(user.getUid(), user.getUname(), user.getPassword(), user.getEmail(), user.getPhone(), user.getImage(),user.getStatus());
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return super.getUname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
