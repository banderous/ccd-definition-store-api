package uk.gov.hmcts.ccd.definition.store.repository;

import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.ServiceAndUserDetails;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

@Named
@Singleton
public class SecurityUtils {

    private final AuthTokenGenerator authTokenGenerator;

    @Autowired
    public SecurityUtils(final AuthTokenGenerator authTokenGenerator) {
        this.authTokenGenerator = authTokenGenerator;
    }

    public HttpHeaders authorizationHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("ServiceAuthorization", authTokenGenerator.generate());
        return headers;
    }

    public ServiceAndUserDetails getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return (ServiceAndUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }

    public HttpHeaders userAuthorizationHeaders() {
        final ServiceAndUserDetails serviceAndUser =
            (ServiceAndUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, serviceAndUser.getPassword());
        return headers;
    }
}
