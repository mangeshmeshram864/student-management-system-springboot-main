package com.ytp.SmartContactManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter{
	
     @Bean
	public UserDetailsService getUserDetailService()
	{
		return new UserDetailsSeviceImpl();
		
	}
     @Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
     @Bean
     public DaoAuthenticationProvider authenticationProvider() {
    	 
    	 DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
    	 daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
    	 daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    	
    	 return daoAuthenticationProvider;
     }
     //config methods...
     protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    	 
    	  auth.authenticationProvider(authenticationProvider());
    	  
    }
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		  http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN").antMatchers("/user/**").hasRole("USER").antMatchers("/**")
		  .permitAll().and().formLogin().loginPage("/signin").loginProcessingUrl("/do_login").defaultSuccessUrl("/user/dashboard").and().csrf().disable();
	}
     
     
}
