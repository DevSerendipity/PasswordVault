package org.foolProof.PasswordVault.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration @EnableWebSecurity public class WebSecurityConfig {

    @Autowired private MyUserDetailsService myUserDetailsService;

    @Bean public SecurityFilterChain websiteFilter(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/getFile").authenticated().antMatchers("/uploadFile", "/register")
                .permitAll().and().formLogin().permitAll().and().logout().permitAll();
        return http.build();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("Auth");
        auth.userDetailsService(myUserDetailsService);
    }

    @Bean public static PasswordEncoder argon2() {
        return new Argon2PasswordEncoder();
    }

}
