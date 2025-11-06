package in.narakcode.authify.config;

import in.narakcode.authify.filter.JwtRequestFilter;
import in.narakcode.authify.service.AppUserDetailsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AppUserDetailsService appUserDetailsService;
  private final JwtRequestFilter jwtRequestFilter;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  /**
   * Defines the main Spring Security filter chain.
   *
   * @param http HttpSecurity instance provided by Spring
   * @return SecurityFilterChain that applies CORS, disables CSRF, defines allowed
   *         endpoints, sets
   *         stateless session, and disables logout.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Enable CORS with default settings (uses the corsConfigurationSource bean)
        .cors(Customizer.withDefaults())

        // Disable CSRF protection (common for stateless APIs)
        .csrf(AbstractHttpConfigurer::disable)

        // Define which endpoints are publicly accessible
        .authorizeHttpRequests(
            auth -> auth.requestMatchers("/login", "/register", "/send-reset-otp",
                "/reset-password", "/logout", "/resend-verification-otp", "/verify-account").permitAll()
                .anyRequest().authenticated())
        .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint))

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

        // Configure session management to be stateless (typical for REST APIs)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Disable default Spring Security logout handling
        .logout(AbstractHttpConfigurer::disable);

    return http.build();
  }

  /**
   * Provides a password encoder bean.
   *
   * @return PasswordEncoder using BCrypt hashing algorithm
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Defines a CORS filter bean that applies the CORS configuration.
   *
   * @return CorsFilter that intercepts requests to handle cross-origin issues
   */
  @Bean
  public CorsFilter corsFilter() {
    return new CorsFilter(corsConfigurationSource());
  }

  /**
   * Provides the CORS configuration for the application. Specifies allowed
   * origins, methods,
   * headers, and credentials.
   *
   * @return UrlBasedCorsConfigurationSource with registered CORS settings
   */
  private UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000")); // frontend origin
    config.setAllowedMethods(
        List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // allowed HTTP methods
    config.setAllowedHeaders(
        List.of("Authorization", "Cache-Control", "Content-Type")); // allowed headers
    config.setAllowCredentials(true); // allow cookies/auth info

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config); // apply to all endpoints
    return source;
  }

  /**
   * Defines the AuthenticationManager bean used by Spring Security.
   *
   * @param appUserDetailsService service that loads user details from database
   * @param passwordEncoder       encoder for hashing and verifying passwords
   * @return AuthenticationManager that authenticates users via
   *         DaoAuthenticationProvider
   */
  @Bean("customAuthenticationManager")
  public AuthenticationManager customAuthenticationManager(
      AppUserDetailsService appUserDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(appUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(provider);
  }

}
