package de.ii.xtraplatform.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface DocFile {

  /**
   * relative markdown file path (default is derived from class name with com.google.common.base.CaseFormat#LOWER_HYPHEN)
   * @return
   */
  String value() default "";
}