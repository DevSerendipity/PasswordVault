package org.foolProof.PasswordVault.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration @EnableWebSecurity public class WebSecurityConfig {

    @Bean public SecurityFilterChain websiteFilter( HttpSecurity http ) throws Exception {
        http.authorizeRequests().antMatchers( "/getFile" ).authenticated().antMatchers( "/register" ).permitAll().and()
                .formLogin().and().httpBasic();
        return http.build();
    }

    @Bean public InMemoryUserDetailsManager userDetailsService() {
        PasswordEncoder passwordEncoder = argon2();
        // outputs {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
        // remember the password that is printed out and use in the next step
        System.out.println( passwordEncoder.encode( "password" ) );
        String password = passwordEncoder.encode( "password" );

        UserDetails user = User.withUsername( "user" )
                .password( "{argon2}"+password ).roles( "USER" )
                .build();
        return new InMemoryUserDetailsManager( user );
    }

    public static PasswordEncoder argon2(){
        return new Argon2PasswordEncoder();
    }

}
