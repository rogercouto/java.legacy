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
 * Annotation que seta a chave estrangeira de uma tabela
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {

	/**
	 * Referencia da chave estrangeira
	 * @return - Classe - que o campo se referencia
	 */
	Class<?> reference();
	
}
