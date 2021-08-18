package rde;

import java.lang.reflect.Field;
import java.util.ArrayList;

import rde.annotations.ForeignKey;
import rde.annotations.PrimaryKey;

/**
 * Classe com as constantes do sistema e métodos pra uso das outras classes
 * @author Roger
 */
public class RDE {
	
	/**
	 *  Constante usada para definir quando o banco de dados não tem suporte a Arquivos
	 */
	public static final int FILETYPE_NONE = 0;
	/**
	 * Constante usada para definir quando o banco de dados salva os arquivos
	 * em um diretório no prorio computador onde a aplicação roda
	 */
	public static final int FILETYPE_SYSTEM = 1;
	/**
	 *  Constante usada para definir quando o banco de dados usa o tipo Blob para arquivos
	 */
	public static final int FILETYPE_BLOB = 2;
	/**
	 *  Constante usada para definir quando o banco de dados usa o tipo byte[] para arquivos
	 */
	public static final int FILETYPE_BYTE = 3;
	
	
	/**
	 * Constante usada para indicar a operação de inserção no DB
	 */
	public static final int OP_INSERT = 4;
	/**
	 * Constante usada para indicar a operação de atualização no DB
	 */
	public static final int OP_UPDATE = 5;
	/**
	 * Constante usada para indicar a operação de exclusão no DB
	 */
	public static final int OP_DELETE = 6;
	/**
	 * Constante usada para definir que não hà operação a ser realizada no DB
	 */
	public static final int OP_NONE = 7;
	
	/**
	 * Constante usada para definir o tipo de busca como igual a 'variavel'
	 */
	public static final int FIND_EQUALS = 8;
	/**
	 * Constante usada para definir o tipo de busca como inicia com 'variavel'
	 */
	public static final int FIND_START_WITH = 9;
	/**
	 * Constante usada para definir o tipo de busca como termina com 'variavel'
	 */
	public static final int FIND_END_WITH = 10;
	/**
	 * Constante usada para definir o tipo de busca como contém 'variavel'
	 */
	public static final int FIND_CONTAIN = 11;
	
	
	public static boolean logErrors = true;
	/**
	 * Retorna um vetor das chaves primárias
	 * @param c - classe de referência 
	 * @return Array de chaves primárias(Field[])
	 */
	public static Field[] getPrimaryKeys(Class<?> c){
		Field[] field = c.getDeclaredFields();
		ArrayList<Field> pks = new ArrayList<Field>();
		for (Field f : field) {
			if (f.isAnnotationPresent(PrimaryKey.class))
				pks.add(f);
		}
		Field[] primaryKeys = new Field[pks.size()];
		for (int i = 0; i < primaryKeys.length; i++) {
			primaryKeys[i] = pks.get(i);
		}
		return primaryKeys;
	}
	
	/**
	 * Retorna um vetor com as chaves estrangeiras
	 * @param c - Classe de referência
	 * @return Array de chaves estrangeiras(Field[])
	 */
	public static Field[] getForeignKeys(Class<?> c){
		Field[] field = c.getDeclaredFields();
		ArrayList<Field> fks = new ArrayList<Field>();
		for (Field f : field) {
			if (f.isAnnotationPresent(ForeignKey.class))
				fks.add(f);
		}
		Field[] primaryKeys = new Field[fks.size()];
		for (int i = 0; i < primaryKeys.length; i++) {
			primaryKeys[i] = fks.get(i);
		}
		return primaryKeys;
	}
	
	/**
	 * Retorna a função SQL c/ valor gerado automáticamente
	 * @param field - Campo a ser verificado
	 * @return String com a função ou null caso não seja auto incrementado
	 */
	public static String getKeyGen(Field field){
		String keyGen = null;
		if ((field.isAnnotationPresent(PrimaryKey.class))
		&&(field.getAnnotation(PrimaryKey.class).keygen().trim().length() != 0)){
			keyGen = field.getAnnotation(PrimaryKey.class).keygen();
		}
		return keyGen;
	}
	
	/**
	 * Retorna a classe de referência de uma chave primária
	 * @param field - Campo a ser verificado
	 * @return Class<?> Referência da PrimaryKey
	 */
	public static Class<?> getReference(Field field){
		Class<?> c = null;
		if (field.isAnnotationPresent(ForeignKey.class)){
			c = field.getAnnotation(ForeignKey.class).reference();
		}
		return c;
	}
	
	/**
	 * Retorna os campos de uma classe conforme os índices
	 * @param c - classe a ser verificada
	 * @param indices - vetor de inteiros que correspondem aos numeros dos campos
	 * @return Vetor com os campos resultantes (Field[])
	 */
	public static Field[] getFields(Class<?> c,int[] indices){
		Field[] fields = c.getDeclaredFields();
		ArrayList<Field> list = new ArrayList<Field>();
		for (int i = 0; i < indices.length; i++) {
			for (int j = 0; j < fields.length; j++) {
				if (indices[i] == j)
					list.add(fields[j]);
			}
		}
		fields = new Field[list.size()];
		for (int i = 0; i < list.size(); i++) {
			fields[i] = list.get(i);
		}
		return fields;
	}
	
	/**
	 * Retorna os campos de uma classe conforme os Nomes dos mesmos
	 * @param c - classe a ser verificada
	 * @param fieldNames - vetor com o nome dos campos
	 * @return Vetor com os campos resultantes (Field[])
	 */
	public static Field[] getFields(Class<?> c,String[] fieldNames){
		Field[] fields = c.getDeclaredFields();
		ArrayList<Field> list = new ArrayList<Field>();
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < fieldNames.length; j++) {
				if (fieldNames[j].compareTo(fields[i].getName()) == 0)
					list.add(fields[i]);
			}
		}
		fields = new Field[list.size()];
		for (int i = 0; i < list.size(); i++) {
			fields[i] = list.get(i);
		}
		return fields;
	}
	
	public static Field getField(Class<?> c,String fieldName){
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().compareTo(fieldName) == 0){
				return field;
			}
		}
		return null;
	}
	/**
	 * Método para verificar as dependencias direta ou indireta de uma classe
	 * @param c - classe a ser verificadas
	 * @return Lista com as classes da qual a classe c depende 
	 */
	public static ArrayList<Class<?>> getDependences(Class<?> c){
		return getDependences(c,null);
	}
	
	private static ArrayList<Class<?>> getDependences(Class<?> c,ArrayList<Class<?>> list){
		if (list == null){
			list = new ArrayList<Class<?>>();
			list.add(c);
		}
		ArrayList<Class<?>> fClasses = new ArrayList<Class<?>>();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(ForeignKey.class)){
				Class<?> ref = field.getAnnotation(ForeignKey.class).reference();
				if (!ref.equals(c))
					fClasses.add(ref);
			}
		}
		for (Class<?> class1 : fClasses) {
			boolean add = true;
			for (int i = 0; i < list.size(); i++) {
				if (class1.equals(list.get(i))){
					add = false;
				}
			}
			if (add){
				list.add(class1);
				getDependences(class1, list).size();
			}
		}
		return list;
	}
	
	/**
	 * Retorna as classes referencia de uma classe
	 * @param c
	 * @return classes
	 */
	@Deprecated
	public static ArrayList<Class<?>> getFClasses(Class<?> c){
		ArrayList<Class<?>> fClasses = new ArrayList<Class<?>>();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(ForeignKey.class)){
				Class<?> ref = field.getAnnotation(ForeignKey.class).reference();
				if (!ref.equals(c))
					fClasses.add(ref);
			}
		}
		return fClasses;
	}
	
	/**
	 * Retorna um vetor com os nomes dos campos
	 * @param c
	 * @return Array
	 */
	public static String[] getFieldNames(Class<?> c){
		Field[] fields = c.getDeclaredFields();
		String[] fieldNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			fieldNames[i] = fields[i].getName();
		}
		return fieldNames;
	}
	/**
	 * Imprime na tela os campos de um objeto qualquer
	 * @param object - Objeto p/ verificação
	 */
	public static void printFields(Object object){
		if (object == null){
			System.out.println(object);
			return;
		}
		Field[] fields = object.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				fields[i].setAccessible(true);
				System.out.println(fields[i].getName()+" = "+fields[i].get(object));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		System.out.println();
	}
	
}
