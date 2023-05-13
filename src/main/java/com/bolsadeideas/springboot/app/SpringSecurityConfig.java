package com.bolsadeideas.springboot.app;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.bolsadeideas.springboot.app.auth.handler.LoginSuccessHandler;

// si no pones esta linea no puedes usar las anotaciones @Secured y @Preauthorize
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Autowired // mensaje de exito al iniciar sesion
    private LoginSuccessHandler successHandler;

    @Autowired // para encriptar contraseñas lo inyectasmos de MVCConfig
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired // para conectarse a la base de datos
    private DataSource dataSource;

    /**
     * Filtrar las rutas que se pueden acceder dependiendo el rol
     * 
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable().cors().and()
                .authorizeHttpRequests()
                // añadir las rutas publicas
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar")
                .permitAll()
                // añadir rutas privadas
                /*
                 * las comentamos para poder usar anotaciones
                 * .requestMatchers("/ver/**").hasAnyRole("USER")
                 * .requestMatchers("/uploads/**").hasAnyRole("USER")
                 * .requestMatchers("/form/**").hasAnyRole("ADMIN")
                 * .requestMatchers("/eliminar/**").hasAnyRole("ADMIN")
                 * .requestMatchers("/factura/**").hasAnyRole("ADMIN")
                 */
                .anyRequest().authenticated()
                // implementacion del formulario login
                .and()
                // esta permitido para todos los usuarios, se envia al loginController
                .formLogin()
                // mensaje de exito al iniciar sesion
                .successHandler(successHandler)
                // mostramos nuestro login personalizado
                .loginPage("/login")
                .permitAll()
                .and()
                .logout().permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/error_403");

        return http.build();
    }

    /**
     * Configuracion de usuario, contraseña y roles
     * Lo comentamos para que podamos conectarnos a una BD
     * 
     * @return
     * @throws Exception
     * 
     * @Bean
     *       public UserDetailsService userDetailsService() throws Exception {
     * 
     *       InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
     *       manager.createUser(User
     *       .withUsername("coach")
     *       .password(passwordEncoder.encode("user_pass"))
     *       .roles("USER")
     *       .build());
     *       manager.createUser(User
     *       .withUsername("admin")
     *       .password(passwordEncoder.encode("admin_pass"))
     *       .roles("ADMIN", "USER")
     *       .build());
     * 
     *       return manager;
     *       }
     */

     /**
      * Metodo con JDBC 
      * @param http
      * @return
      * @throws Exception
      */
    @Bean
    AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder)
                .usersByUsernameQuery("select username, password, enabled from users where username=?")
                .authoritiesByUsernameQuery(
                        "select u.username, a.authority from authorities a inner join users u on (a.user_id=u.id) where u.username=?")
                .and().build();
    }
}
