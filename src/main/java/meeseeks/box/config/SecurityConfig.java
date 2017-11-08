package meeseeks.box.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import meeseeks.box.domain.UserEntity.UserRole;
import meeseeks.box.repository.UserRepository;
import meeseeks.box.security.JWTAuthenticationFilter;
import meeseeks.box.security.JWTAuthorizationFilter;
import meeseeks.box.security.SecurityConstants;
import meeseeks.box.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecurityConstants securityConstants;
    private final UserRepository userRepo;

    @Autowired
    public SecurityConfig(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, SecurityConstants securityConstants, UserRepository userRepo) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.securityConstants = securityConstants;
        this.userRepo = userRepo;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().csrf().disable()
            .exceptionHandling().accessDeniedPage("/access-denied")
            .and()
            .authorizeRequests()
                .antMatchers("/").permitAll() // "/" route is publicly accessible
                .antMatchers("/user").permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll() // "/login" route is publicly accesible for POST
                .antMatchers(HttpMethod.POST, "/provider/register").permitAll()
                .antMatchers(HttpMethod.POST, "/consumer/register").permitAll()
                .antMatchers("/account/provider/**").hasAuthority(UserRole.provider.toString()) // example, should be changed later
            .anyRequest()
            .authenticated()
            .and()
             // We filter the api/login requests
            .addFilter(new JWTAuthorizationFilter(authenticationManager(), securityConstants, userRepo))
            // And filter other requests to check the presence of JWT in header
            .addFilter(new JWTAuthenticationFilter(authenticationManager(), securityConstants));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userService)
            .passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // TODO: To be updated later
        web
           .ignoring()
           .antMatchers("/images/**");
    }
}
