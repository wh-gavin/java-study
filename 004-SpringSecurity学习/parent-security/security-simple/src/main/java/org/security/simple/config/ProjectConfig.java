package org.security.simple.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.DefaultLdapUsernameToDnMapper;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.LdapUserDetailsManager;

@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {

//	@Autowired
//	private CustomAuthenticationProvider customAuthenticationProvider;
//	
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) {
//		auth.authenticationProvider(customAuthenticationProvider);
//	}

    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        var cs = new DefaultSpringSecurityContextSource("ldap://127.0.0.1:33389/dc=springframework,dc=org");
        cs.afterPropertiesSet();

        LdapUserDetailsManager manager = new LdapUserDetailsManager(cs);
        manager.setUsernameMapper(
                new DefaultLdapUsernameToDnMapper("ou=groups", "uid"));
        manager.setGroupSearchBase("ou=groups");
        return manager;
    }
//	@Bean
//	public UserDetailsService userDetailsService(DataSource dataSource) {
//		String usersByUsernameQuery = "select username, password, enabled from spring.users where username = ?";
//		String authsByUserQuery = "select username, authority from spring.authorities where username = ?";
//		var userDetailsManager = new JdbcUserDetailsManager(dataSource);
//		userDetailsManager.setUsersByUsernameQuery(usersByUsernameQuery);
//		userDetailsManager.setAuthoritiesByUsernameQuery(authsByUserQuery);
//		return userDetailsManager;
//	}

//	@Override
//	@Bean
//	public UserDetailsService userDetailsService() {
//		var userDetailsService = new InMemoryUserDetailsManager();
//		var user = User.withUsername("john").password("{noop}12345").authorities("read").build();
//		userDetailsService.createUser(user);
//		return userDetailsService;
//	}

//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return NoOpPasswordEncoder.getInstance();
//	}
//
//	@Bean
//    public  static PasswordEncoder passwordEncoder( ){
//        DelegatingPasswordEncoder delegatingPasswordEncoder =
//                (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
//        return  delegatingPasswordEncoder;
//    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic();
		http.authorizeRequests().anyRequest().authenticated();
	}
}
