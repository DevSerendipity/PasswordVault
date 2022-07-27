package org.foolProof.PasswordVault.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration @EnableWebSecurity public class WebSecurityConfig {

    @Autowired private MyUserDetailsService myUserDetailsService;

    @Autowired private PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

    @Bean public SecurityFilterChain websiteFilter(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/file").authenticated().antMatchers("/uploadFile", "/register")
                .permitAll().and().formLogin().defaultSuccessUrl("/file").loginPage("/login")
                .failureUrl("/login?error=true").permitAll().and().logout().permitAll();
        return http.build();
    }

    @Bean public static PasswordEncoder argon2() {
        return new Argon2PasswordEncoder();
    }

    @Bean public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(myUserDetailsService);
        return authenticationProvider;
    }

    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }
}
