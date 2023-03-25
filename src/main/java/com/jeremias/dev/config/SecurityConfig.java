package com.jeremias.dev.config;

import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity


public class SecurityConfig {
	  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	    private String issuer;

	    @Value("${auth0.audience}")
	    private String audience;
	
	private static final String[] AUTH_WHITE_LIST = {
			"/authenticate",
	        "/swagger-resources/**",
	        "/swagger-ui/**",
	        
	        "/v3/api-docs/**",
	        "/webjars/**"
    };
	
	@Bean
	  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		/*http.cors().and().csrf().disable()
       .authorizeRequests()
    //   .requestMatchers(AUTH_WHITE_LIST).permitAll()
       //.requestMatchers("/**").permitAll()
       .anyRequest()
       .authenticated()
       .and()
       .sessionManagement()
       .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
       .and()
       .oauth2ResourceServer()
       .jwt();
		 */
		http.cors().and()
          .authorizeRequests()
          .requestMatchers("/api/**")
          .permitAll()
          .anyRequest().authenticated().and()
          .oauth2ResourceServer().jwt();
		  /*
		 http.authorizeRequests()
         .anyRequest().authenticated()
         .and()
         .sessionManagement()
         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
         .and()
       
         .oauth2ResourceServer()
         .jwt();
*/
	    return http.build();
	  }
	
	@Bean
	public JwtDecoder jwtDecoder() {
		
		System.out.println("audience: " + audience);
		System.out.println("issuer: " + issuer);
		   //URLEncoder.encode(issuer) 
		NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer );
		System.out.println("==============================: " + issuer);
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder; 
	}
	 
	 @Bean
	    PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	 @Bean
	  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	    return config.getAuthenticationManager();
	  }
}
