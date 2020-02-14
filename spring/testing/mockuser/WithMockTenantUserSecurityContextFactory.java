package com.kubra.prepay.instances.testutils;

import com.kubra.security.oidc.rs.context.User;
import com.kubra.security.oidc.rs.context.UserContext;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockTenantUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockTenantUser> {

  /**
   * Create a {@link SecurityContext} given an Annotation.
   *
   * @param withUser the {@link Annotation} to create the {@link SecurityContext} from. Cannot be
   *     null.
   * @return the {@link SecurityContext} to use. Cannot be null.
   */
  @Override
  public SecurityContext createSecurityContext(WithMockTenantUser withUser) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    User user =
        new User(
            UUID.fromString("a8c2b204-1563-4a9a-9581-72f0cf71e5ef"),
            null,
            Arrays.asList(withUser.authorities()));
    UserContext.setUserUnverifiedTenant(user);
    List<GrantedAuthority> authorities = new ArrayList<>(withUser.authorities().length);
    for (String perm : withUser.authorities()) {
      authorities.add((GrantedAuthority) () -> perm);
    }
    Authentication auth =
        new UsernamePasswordAuthenticationToken(
            withUser.username(),
            withUser.password(),
            withUser.authorities().length == 0 ? AuthorityUtils.NO_AUTHORITIES : authorities);
    context.setAuthentication(auth);
    return context;
  }
}

