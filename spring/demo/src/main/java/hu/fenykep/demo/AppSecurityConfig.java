package hu.fenykep.demo;

import hu.fenykep.demo.exception.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .usernameParameter("email") // HTML form input name
                .passwordParameter("jelszo")
                .loginPage("/felhasznalo/bejelentkezes")
                .defaultSuccessUrl("/felhasznalo/profil")
                .failureUrl("/felhasznalo/bejelentkezes?hiba");
        http.logout()
                .logoutUrl("/felhasznalo/kijelentkezes") // POST method CSRF miatt
                .logoutSuccessUrl("/felhasznalo/bejelentkezes?kijelentkezes")
                .logoutRequestMatcher(new AntPathRequestMatcher("/felhasznalo/kijelentkezes", "GET"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        http.sessionManagement()
                .invalidSessionUrl("/felhasznalo/bejelentkezes")
                .sessionAuthenticationErrorUrl("/felhasznalo/bejelentkezes");

        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());

    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
