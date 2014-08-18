package org.nofdev.servicefacade;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FacadeApi {
    String[] params() default {};
}
