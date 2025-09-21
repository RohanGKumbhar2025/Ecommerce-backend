package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.dto.*;
import com.ecom.productcatalog.exception.EmailAlreadyExistsException;
import com.ecom.productcatalog.exception.PasswordMismatchException;
import com.ecom.productcatalog.model.User;
import com.ecom.productcatalog.repository.UserRepository;
import com.ecom.productcatalog.security.JwtUtils;
import com.ecom.productcatalog.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt, userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getName(), roles);
    }

    // ✅ UPDATED: Now throws specific, handled exceptions.
    @PostMapping("/signup")
    public void signup(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already taken!");
        }

        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords don't match!");
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(new HashSet<>());
        user.getRoles().add("ROLE_USER");

        userRepository.save(user);
    }
}