package meeseeks.box.config;

import meeseeks.box.domain.UserEntity.UserRole;
import meeseeks.box.repository.UserRepository;
import meeseeks.box.security.JWTAuthenticationFilter;
import meeseeks.box.security.JWTAuthorizationFilter;
import meeseeks.box.security.SecurityConstants;
import meeseeks.box.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"meeseeks.box.config", "meeseeks.box.service",
        "meeseeks.box.security", "meeseeks.box.repository"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecurityConstants securityConstants;
    private final UserRepository userRepository;

    @Autowired
    public SecurityConfiguration(UserService userService,
                                 BCryptPasswordEncoder bCryptPasswordEncoder,
                                 SecurityConstants securityConstants,
                                 UserRepository userRepository) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.securityConstants = securityConstants;
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable().exceptionHandling().accessDeniedPage("/access-denied").and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/user").permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/provider/register").permitAll()
                .antMatchers(HttpMethod.POST, "/consumer/register").permitAll()
                .antMatchers("/account/provider/**").hasAuthority(UserRole.provider.toString())
                .anyRequest().authenticated().and()
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), securityConstants, userRepository))
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), securityConstants, userService));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**");
    }
}
