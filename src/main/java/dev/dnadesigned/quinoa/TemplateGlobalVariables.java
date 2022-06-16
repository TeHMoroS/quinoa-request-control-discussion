package dev.dnadesigned.quinoa;

import io.vertx.core.http.HttpServerRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * Not true Qute-style template globals, because those need to be static. These, on the other hand, are request-based.
 */
@ApplicationScoped
@Named("globals")
public class TemplateGlobalVariables {

    @Inject
    HttpServerRequest request;

    public boolean hxRequest() {
        return Optional.ofNullable(this.request.getHeader("HX-Request"))
            .map(Boolean::parseBoolean)
            .orElse(false);
    }
}
