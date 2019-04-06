package org.test.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.enabled:false}")
    private Boolean securityEnabled = Boolean.FALSE;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (securityEnabled) {
            http.authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .httpBasic()
                    .and()
                    .logout()
                    .logoutUrl("/logout")
                    .deleteCookies("remember-me")
                    .logoutSuccessUrl("/")
                    .permitAll()
                    .and()
                    .rememberMe();
        } else {
            http.authorizeRequests()
                    .antMatchers("/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .csrf()
                    .disable();
        }
    }

}
