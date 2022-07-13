package org.foolProof.PasswordVault.web;

import jakarta.transaction.Transactional;
import org.foolProof.PasswordVault.User.Client;
import org.foolProof.PasswordVault.User.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service @Transactional public class MyUserDetailsService implements UserDetailsService {

    @Autowired private ClientRepository clientRepository;

    @Override public UserDetails loadUserByUsername( String email ) throws UsernameNotFoundException {
        Client client = clientRepository.findClientByEmail( email );
        if ( client == null ) {
            throw new UsernameNotFoundException( "No user found with username: " + email );
        }
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        return new User( client.getEmail(), client.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
                         accountNonLocked, getAuthorities( List.of( "ROLE_USER" ) ) );
    }

    private static List<GrantedAuthority> getAuthorities( List<String> roles ) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for ( String role: roles ) {
            authorities.add( new SimpleGrantedAuthority( role ) );
        }
        return authorities;
    }
}
