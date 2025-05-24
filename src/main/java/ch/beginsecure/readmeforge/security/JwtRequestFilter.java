package ch.beginsecure.readmeforge.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ch.beginsecure.readmeforge.repository.UserRepository;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                logger.warn("JWT token has expired for request " + request.getRequestURI() + ": " + e.getMessage());
            } catch (UnsupportedJwtException e) {
                logger.warn("Unsupported JWT token for request " + request.getRequestURI() + ": " + e.getMessage());
            } catch (MalformedJwtException e) {
                logger.warn("Malformed JWT token for request " + request.getRequestURI() + ": " + e.getMessage());
            } catch (SignatureException e) {
                logger.warn("JWT signature validation failed for request " + request.getRequestURI() + ": " + e.getMessage());
            } catch (IllegalArgumentException e) {
                logger.warn("JWT token compact of handler are invalid for request " + request.getRequestURI() + ": " + e.getMessage());
            }
        } else {
            if (request.getRequestURI().startsWith("/api/") && !request.getRequestURI().startsWith("/api/auth")) {
                logger.warn("Authorization header missing or not Bearer for protected API: " + request.getRequestURI());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userRepository.findByUsername(username).orElse(null);

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                logger.info("Successfully authenticated user '" + username + "' and set SecurityContext for " + request.getRequestURI());
            } else {
                logger.warn("User '" + username + "' extracted from JWT not found in database for " + request.getRequestURI());
            }
        }
        chain.doFilter(request, response);
    }
}