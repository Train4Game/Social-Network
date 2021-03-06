package com.train4game.social.config;

import com.train4game.social.AuthorizedUser;
import com.train4game.social.addons.jwt.JwtAuthenticationFilter;
import com.train4game.social.addons.jwt.JwtLoginFilter;
import com.train4game.social.addons.jwt.TokenAuthenticationService;
import com.train4game.social.addons.oAuth.CustomOAuthAuthenticationFilter;
import com.train4game.social.addons.oAuth.OAuthClientResources;
import com.train4game.social.model.User;
import com.train4game.social.service.OAuthService;
import com.train4game.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private OAuth2ClientContext oAuth2ClientContext;

    @Autowired
    private Environment environment;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(ssoFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(
                        "/login", "/register",
                        "/resend-token", "/confirm-token",
                        "/login/vk", "/response/vk",
                        "/forgot-password", "/reset-password")
                .anonymous()
                .antMatchers(HttpMethod.POST, "/rest/login")
                .anonymous()
                .and()
                .authorizeRequests()
                .antMatchers("/rest/**", "/**")
                .authenticated()
                .and()
                .authorizeRequests()
                .antMatchers("/rest/admin/**", "/admin/**")
                .hasRole("ADMIN")
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/profile", true)
                .loginProcessingUrl("/login")
                .and()
                .rememberMe()
                .rememberMeParameter("remember")
                .rememberMeCookieName("remember")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .and()
                .csrf().ignoringAntMatchers("/rest/**")
                .and()
                .addFilterBefore(new JwtLoginFilter("/rest/login", authenticationManager(), tokenAuthenticationService()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(tokenAuthenticationService()), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public FilterRegistrationBean oAuth2ClientFilterRegistration(OAuth2ClientContextFilter oAuth2ClientContextFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(oAuth2ClientContextFilter);
        registration.setOrder(-100);
        return registration;
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(facebook(), "/login/facebook",
                map -> oAuthService.getUserFromFacebookOAuth(map)));
        filters.add(ssoFilter(google(), "/login/google",
                map -> oAuthService.getUserFromGoogleOAuth(map)));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(OAuthClientResources client, String path, PrincipalExtractor extractor) {
        final var oAuth2ClientAuthenticationFilter = new CustomOAuthAuthenticationFilter(path, tokenAuthenticationService());
        final var oAuth2RestTemplate = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
        oAuth2ClientAuthenticationFilter.setRestTemplate(oAuth2RestTemplate);
        final var tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(),
                client.getClient().getClientId());
        tokenServices.setRestTemplate(oAuth2RestTemplate);
        tokenServices.setPrincipalExtractor(map -> new AuthorizedUser((User) extractor.extractPrincipal(map)));
        oAuth2ClientAuthenticationFilter.setTokenServices(tokenServices);
        return oAuth2ClientAuthenticationFilter;
    }

    @Bean
    @ConfigurationProperties("google")
    public OAuthClientResources google() {
        return new OAuthClientResources();
    }

    @Bean
    @ConfigurationProperties("facebook")
    public OAuthClientResources facebook() {
        return new OAuthClientResources();
    }

    @Bean
    @ConfigurationProperties("vk")
    public OAuthClientResources vk() {
        return new OAuthClientResources();
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/webjars/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Description("Password Encoder")
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public TokenAuthenticationService tokenAuthenticationService() {
        return new TokenAuthenticationService(environment, userService);
    }
}