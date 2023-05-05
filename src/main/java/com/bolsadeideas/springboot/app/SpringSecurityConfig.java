package com.bolsadeideas.springboot.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuracion de usuario, contraseña y roles
     * 
     * @return
     * @throws Exception
     */
    @Bean
    public UserDetailsService userDetailsService() throws Exception {

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername("coach")
                .password(passwordEncoder().encode("user_pass"))
                .roles("USER")
                .build());
        manager.createUser(User
                .withUsername("admin")
                .password(passwordEncoder().encode("admin_pass"))
                .roles("ADMIN", "USER")
                .build());

        return manager;
    }

    /**
     * Filtrar las rutas que se pueden acceder dependiendo el rol
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable().cors().and()
                .authorizeHttpRequests()
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar")
                .permitAll()
                .requestMatchers("/ver/**").hasAnyRole("USER")
                .requestMatchers("/uploads/**").hasAnyRole("USER")
                .requestMatchers("/form/**").hasAnyRole("ADMIN")
                .requestMatchers("/delete/**").hasAnyRole("ADMIN")
                .requestMatchers("/invoice/**").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
                .and().formLogin()
                .permitAll()
                .and().logout()
                .permitAll();

        return http.build();
    }
}
