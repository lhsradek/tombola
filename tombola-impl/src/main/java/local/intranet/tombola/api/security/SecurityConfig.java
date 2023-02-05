package local.intranet.tombola.api.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import local.intranet.tombola.api.exception.TombolaException;
import local.intranet.tombola.api.service.UserService;

/**
 *
 * {@link SecurityConfig} for {@link local.intranet.tombola.TombolaApplication}.
 * <p>
 * https://www.baeldung.com/spring-security-csp <br>
 * https://www.baeldung.com/spring-security-session <br>
 * https://www.baeldung.com/spring-security-cors-preflight <br>
 * https://www.baeldung.com/spring-security-basic-authentication <br>
 * https://www.baeldung.com/spring-security-login <br>
 * https://www.baeldung.com/spring-security-logout <br>
 * https://stackoverflow.com/questions/24057040/content-security-policy-spring-security
 * 
 * @author Radek Kádner
 *
 */
@SuppressWarnings("deprecation")
@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebSecurityConfigurer<WebSecurity> {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

	@Value("#{'${tombola.app.authenticated}'.split('\\s{1,}')}")
	private List<String> authenticated;

	@Value("#{'${tombola.app.permitAll}'.split('\\s{1,}')}")
	private List<String> permitAll;

	@Autowired
	private UserService userService;

	/**
	 * 
	 * Set {@link CorsConfiguration}
	 * 
	 * @return {@link CorsFilter}
	 */
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		CorsFilter ret = new CorsFilter(source);
		return ret;
	}

	/**
	 * 
	 * Create AuthenticationManager
	 * <p>
	 * For {@link local.intranet.tombola.api.controller.IndexController#signin}
	 * 
	 * @return {@link AuthenticationManager}
	 * @throws {@link TombolaException}
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws TombolaException {
		try {
			AuthenticationManager ret = super.authenticationManagerBean();
			return ret;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new TombolaException(e.getMessage());
		}
	}

	/**
	 * 
	 * Set PasswordEncoder
	 * 
	 * @return {@link PasswordEncoder}
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder ret = new BCryptPasswordEncoder();
		return ret;
	}

	/**
	 * 
	 * Configure AuthenticationManagerBuilder
	 * 
	 * @param auth {@link AuthenticationManagerBuilder}
	 * @throws {@link Exception}
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}

	/**
	 * 
	 * Configure {@link HttpSecurity}
	 * <p>
	 * {@link local.intranet.tombola.api.security.LogoutSuccess} invalidates
	 * {@link javax.servlet.http.HttpSession}.
	 * 
	 * @param http {@link HttpSecurity}
	 * @throws {@link IndexException}
	 */
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests(authorizeRequests -> {
			permitAll.forEach(key -> {
				if (key.length() > 0) {
					authorizeRequests.antMatchers(key).permitAll();
				}
			});
			authenticated.forEach(key -> {
				if (key.length() > 0) {
					authorizeRequests.antMatchers(key).authenticated();
				}
			});
		}).headers().xssProtection().and()
				.contentSecurityPolicy("script-src 'self'; object-src 'self'; form-action 'self'; style-src 'self'")
				.and().cacheControl().and().httpStrictTransportSecurity().and().frameOptions().disable().frameOptions()
				.sameOrigin().and().httpBasic().and().formLogin().loginPage("/login").permitAll()
				.failureUrl("/login?error=true").and().exceptionHandling().accessDeniedPage("/login?error=403").and()
				.logout().logoutSuccessHandler(userService.logoutSuccess())
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/")
				.invalidateHttpSession(true).deleteCookies("JSESSIONID").and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS).sessionFixation().migrateSession()
				.maximumSessions(1);
	}

}