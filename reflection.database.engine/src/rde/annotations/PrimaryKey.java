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
 * Annotation que seta a chave primaria de uma tabela
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {

	/**
	 * Indica a fun��o que gera o valor do 
	 * campo caso seja auto incrementado
	 * @return fun��o sql que gera a chave prim�ria
	 */
	String keygen() default "";
	
}
