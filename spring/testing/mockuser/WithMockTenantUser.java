package com.kubra.prepay.instances.testutils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockTenantUserSecurityContextFactory.class)
public @interface WithMockTenantUser {
  String username() default "admin";

  String password() default "{noop}admin";

  String[] authorities() default {};

  String[] roles() default {"USER"};
}

