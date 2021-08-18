/**
 * 
 */
package rde.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Roger
 * Annotation que seta quando um campo nao pode ser null
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotNull {

	/**
	 * Mensagem de erro quando o valor for null
	 * @return String - mensagem 
	 */
	String errorMessage() default "";
	
}
