package ej.springframework.services.security;

import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Created by eunsoojung on 1/23/17.
 * Section 13 Lecture 75 Spring AOP Example
 */
@Aspect
@Component          // Register Spring bean
public class LoginAspect {

    // S14_L76_4.*
    private LoginFailureEventPublisher publisher;
    // S14_L78_3.*
    private LoginSuccessEventPublisher successEventPublisher;

    // S14_L76_4.*
    @Autowired
    public void setPublisher(LoginFailureEventPublisher publisher) {
        this.publisher = publisher;
    }

    // S14_L78_3.*
    @Autowired
    public void setSuccessEventPublisher(LoginSuccessEventPublisher successEventPublisher) {
        this.successEventPublisher = successEventPublisher;
    }

    @Pointcut("execution(* org.springframework.security.authentication.AuthenticationProvider.authenticate(..))")
    // above  .. <-- two dots is wild card for data type (any data types is ok)
    public void doAuthenticate() {

    }

    @Before("ej.springframework.services.security.LoginAspect.doAuthenticate() && args(authentication)")
    public void logBefore(Authentication authentication) {
        System.out.println("This is before the Authenticate Method: authentication: " +
                authentication.isAuthenticated());
    }

    @AfterReturning(value = "ej.springframework.services.security.LoginAspect.doAuthenticate()",
            returning = "authentication")
    public void logAfterAuthenticate(Authentication authentication) {
        System.out.println("This is after the Authenticate Method authentication: " +
                authentication.isAuthenticated());
        // S14_L78_3.*
        successEventPublisher.publishEvent(new LoginSuccessEvent(authentication));
    }

    @AfterThrowing("ej.springframework.services.security.LoginAspect.doAuthenticate() && args(authentication)")
    public void logAuthenticationException(Authentication authentication) {
        String userDetails = (String) authentication.getPrincipal();
        System.out.println("Login failed for user: " + userDetails);

        // S14_L76_4.*
        publisher.publish(new LoginFailureEvent(authentication));
    }

}