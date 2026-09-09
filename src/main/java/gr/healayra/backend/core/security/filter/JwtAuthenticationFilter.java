package gr.healayra.backend.core.security.filter;

import gr.healayra.backend.authentication.CustomUserDetailsService;
import gr.healayra.backend.authentication.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        // Skip JWT authentication when the request does not contain a Bearer token.
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Remove the "Bearer " prefix and keep only the raw JWT.
        String jwt =
                authorizationHeader.substring(7);

        try {

            String email =
                    jwtService.extractUsername(jwt);

            // Authenticate only when no authentication has already been established.
            if (email != null
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                UserDetails userDetails =
                        customUserDetailsService
                                .loadUserByUsername(email);

                // Verify the token before trusting the user information it contains.
                if (jwtService.isTokenValid(
                        jwt,
                        userDetails
                )) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Store the authenticated user in Spring Security's context for this request.
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }


        } catch (
                JwtException
                | IllegalArgumentException
                | AuthenticationException exception
        ) {

            // Invalid, malformed or expired tokens continue as unauthenticated requests.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}