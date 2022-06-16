package dev.dnadesigned.quinoa;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class PagesController extends Controller {

    @CheckedTemplate
    static class Templates {
        private Templates() {
        }

        public static native TemplateInstance template1();

        public static native TemplateInstance template2();
    }


    @GET()
    @Path("/")
    public TemplateInstance index() {
        return this.page1();
    }

    @GET()
    @Path("/page1")
    @Produces({MediaType.TEXT_HTML})
    public TemplateInstance page1() {
        return Templates.template1();
    }

    @GET()
    @Path("/page2")
    @Produces({MediaType.TEXT_HTML})
    public TemplateInstance page2() {
        return Templates.template2();
    }
}
