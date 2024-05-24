package com.example.application.services;

import com.example.application.data.*;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.application.security.AuthenticatedUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    private PasswordTokenRepository tokenRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private AuthenticatedUser authenticatedUser;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, AuthenticatedUser authenticatedUser) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUser = authenticatedUser;
    }

    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public User getCurrentUser(){
        final Optional<User> u = authenticatedUser.get();
        if (u.isPresent()){
            return repository.findByUsername(u.get().getUsername());
        }
        throw new EntityNotFoundException();
    }

    public User saveUser(User entity){
        entity.setRoles(Collections.singleton(Role.USER));
        String encodedPassword = passwordEncoder.encode(entity.getPassword());
        entity.setPassword(encodedPassword);
        return repository.save(entity);
    }

    public User changePassword(User entity){
        String encodedPassword = passwordEncoder.encode(entity.getPassword());
        entity.setPassword(encodedPassword);
        return repository.save(entity);
    }

    public boolean usernameExists(String username){
        List<User> userList = repository.findAllByUsername(username);
        return (userList != null && !userList.isEmpty());
    }

    public User findUserByEmail(String email){
        return repository.findByEmail(email);
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        tokenRepository.save(myToken);
    }

    public PasswordResetToken validatePasswordToken(String token){
        final PasswordResetToken passToken = tokenRepository.findByToken(token);
        final Calendar cal = Calendar.getInstance();
        return passToken == null ? null
                : passToken.getExpiryDate().before(cal.getTime()) ? null
                : passToken;
    }


    public User getUserByPasswordResetToken(final String token) {
        return tokenRepository.findByToken(token) .getUser();
    }

    public PasswordResetToken getPasswordResetToken(final String token) {
        return tokenRepository.findByToken(token);
    }

}
