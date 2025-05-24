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

        String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                logger.warn("Expired JWT for " + request.getRequestURI());
            } catch (UnsupportedJwtException e) {
                logger.warn("Unsupported JWT for " + request.getRequestURI());
            } catch (MalformedJwtException e) {
                logger.warn("Malformed JWT for " + request.getRequestURI());
            } catch (SignatureException e) {
                logger.warn("Invalid JWT signature for " + request.getRequestURI());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid JWT token for " + request.getRequestURI());
            }
        } else {
            if (request.getRequestURI().startsWith("/api/")
                    && !request.getRequestURI().startsWith("/api/auth")) {
                logger.warn("Invalid header for protected endpoint: " + request.getRequestURI());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userRepository.findByUsername(username).orElse(null);

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
                logger.info("Authenticated user " + username + " for " + request.getRequestURI());
            } else {
                logger.warn("User not found: " + username + " for " + request.getRequestURI());
            }
        }
        chain.doFilter(request, response);
    }
}
