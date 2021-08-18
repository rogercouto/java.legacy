package rc.tools;

/**
 * 
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe usada para validar tipos comuns de dados
 * @author Roger
 */
public class Verify {

	private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
	private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
	
	private static int calcularDigito(String str, int[] peso) {
		int soma = 0;
		for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
			digito = Integer.parseInt(str.substring(indice,indice+1));
			soma += digito*peso[peso.length-str.length()+indice];
		}
		soma = 11 - soma % 11;
		return soma > 9 ? 0 : soma;
	}
	
	/**
	 * Método que valida se o cpf é válido
	 * @param cpf
	 * @return boolean
	 */
	public static boolean cpf(String cpf){
		if (cpf.trim().length() != 11){
			return false;
		}
		String fc = cpf.substring(0,1);
		boolean flag = false;
		for (int i = 1; i < cpf.length(); i++) {
			if (cpf.substring(i,i+1).compareTo(fc) != 0){
				flag = true;
				break;
			}
		}
		if (!flag)return false;
		Integer digito1 = calcularDigito(cpf.substring(0,9), pesoCPF);
		Integer digito2 = calcularDigito(cpf.substring(0,9) + digito1, pesoCPF);
		return (cpf.equals(cpf.substring(0,9) + digito1.toString() + digito2.toString()));
	}
	/**
	 * Método que valida se o cnpj é valido
	 * @param cnpj
	 * @return boolean
	 */
	public static boolean cnpj(String cnpj){
		if (cnpj.trim().length() != 14){
			return false;
		}
		String fc = cnpj.substring(0,1);
		boolean flag = false;
		for (int i = 1; i < cnpj.length(); i++) {
			if (cnpj.substring(i,i+1).compareTo(fc) != 0){
				flag = true;
				break;
			}
		}
		if (!flag)return false;
		Integer digito1 = calcularDigito(cnpj.substring(0,12), pesoCNPJ);
		Integer digito2 = calcularDigito(cnpj.substring(0,12) + digito1, pesoCNPJ);
		return (cnpj.equals(cnpj.substring(0,12) + digito1.toString() + digito2.toString()));
	}
	
	/**
	 * Método que verifica se é cpf ou cnpj e valida conforme o caso
	 * @Param identFederal
	 * @return boolean
	 */
	public static boolean identFederal(String identFederal){
		if (identFederal.trim().length() == 11)
			return cpf(identFederal);
		else if (identFederal.trim().length() == 14)
			return cnpj(identFederal);
		return false;
	}
	/**
	 * Método que valida se um e-mail é válido usando uma expressão regular
	 * @param email
	 * @return boolean
	 */
	public static boolean email(String email){
		Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");  
		Matcher m = p.matcher(email);
		return (m.find());
	}
	
	public static boolean buscaPhone(String phone){
		if (phone.length() > 13)
			return false;
		char[] pa = phone.toCharArray();
		for (int i = 0; i < pa.length; i++) {
			switch (i) {
			case 0:
				if (!Character.isDigit(pa[i]) && (pa[i] != '('))
					return false;
				break;
			case 3:
				if (pa[0] == '('){
					if (pa[i] != ')')
						return false;
				}else if (!Character.isDigit(pa[0]))
					return false;
				break;
			case 4:
				if ((pa[0] == '(')&&(pa[3] == ')')){
					if (!Character.isDigit(pa[i]))
						return false;
				}else if (!Character.isDigit(pa[i])&&(pa[i] != '-'))
					return false;
				break;
			case 8:
				if ((pa[0] == '(')&&(pa[3] == ')')){ 
					if (pa[i] != '-')
						return false;
				}else if (!Character.isDigit(pa[i]))
					return false;
				break;
			default:
				if (!Character.isDigit(pa[i]))
					return false;
				break;
			}
		}
		return true;
	}
	
	public static boolean buscaPlaca(String placa){
		if (placa.length() > 8)
			return false;
		char[] pa = placa.toCharArray();
		for (int i = 0; i < pa.length; i++) {
			if (i < 3 && !Character.isLetter(pa[i]))
				return false;
			if (i == 3 && ((!Character.isDigit(pa[i]))&&(pa[i] != '-')))
				return false;
			if (i > 3 && !Character.isDigit(pa[i]))
				return false;
		}
		return true;
	}
	
	public static boolean date(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setLenient(false);
		try {
			sdf.parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
		
}
