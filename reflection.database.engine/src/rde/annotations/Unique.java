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
 * Annotation que seta quando um campo for único
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Unique {

	/**
	 * Mensagem de erro quando ja existe um registro igual no DB
	 * @return String - mensagem 
	 */
	String errorMessage();
	
}
