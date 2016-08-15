package gov.nasa.pds.web.ui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documents an attribute to a Struts tag
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WidgetTagAttribute {
    String name() default "";

    boolean required() default false;

    boolean rtexprvalue() default true;

    String description();

    String defaultValue() default "";

    String type() default "String";
}
