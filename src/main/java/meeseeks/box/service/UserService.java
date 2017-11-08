package meeseeks.box.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.repository.UserRepository;
import meeseeks.box.security.SecurityConstants;

@Service
public class UserService implements UserDetailsService
{
    @Autowired
    private UserRepository repo;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private SecurityConstants securityConstants;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.repo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User sau parola incorecta");
        }

        return user;
    }

    public void saveUser(UserEntity user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(user.getPassword());

        repo.save(user);
    }
    
    public String getJWTToken(UserEntity user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + securityConstants.EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS512, securityConstants.SECRET.getBytes())
            .compact();
    }
}
