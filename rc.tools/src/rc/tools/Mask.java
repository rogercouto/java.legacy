package rc.tools;

public class Mask {

	public static String cpf(String cpf){
		if (cpf.trim().length() != 11)
			return null;
		char[] ca = cpf.toCharArray();
		StringBuilder mCpf = new StringBuilder();
		for (int i = 0; i < ca.length; i++) {
			if (i == 3 || i == 6)
				mCpf.append('.');
			if (i == 9)
				mCpf.append('-');
			mCpf.append(ca[i]);
		}
		return mCpf.toString();
	}
	
	public static String cnpj(String cnpj){
		if (cnpj.trim().length() != 14)
			return null;
		char[] ca = cnpj.toCharArray();
		StringBuilder mCnpj = new StringBuilder();
		for (int i = 0; i < ca.length; i++) {
			if (i == 1 || i == 5)
				mCnpj.append('.');
			if (i == 8)
				mCnpj.append('/');
			if (i == 12)
				mCnpj.append('-');
			mCnpj.append(ca[i]);
		}
		return mCnpj.toString();
	}
	
	public static String identFederal(String number){
		if (number.trim().length() == 11)
			return cpf(number);
		else if (number.trim().length() == 14)
			return cnpj(number);
		return null;
	}
	
	public static String lblIdentFederal(String number){
		if (number.trim().length() == 11)
			return "CPF Nº: "+cpf(number);
		else if (number.trim().length() == 14)
			return "CNPJ Nº: "+cnpj(number);
		return null;
	}
	
	public static String telefone(String telefone){
		if (telefone.trim().length() != 10)
			return null;
		char[] ca = telefone.toCharArray();
		StringBuilder mTelefone = new StringBuilder();
		for (int i = 0; i < ca.length; i++) {
			if (i == 0)
				mTelefone.append('(');
			if (i == 2)
				mTelefone.append(')');
			if (i == 6)
				mTelefone.append('-');
			mTelefone.append(ca[i]);
		}
		return mTelefone.toString();
	}
	
	public static String cep(String cep){
		if (cep.trim().length() != 8)
			return null;
		char[] ca = cep.toCharArray();
		StringBuilder mCep = new StringBuilder();
		for (int i = 0; i < ca.length; i++) {
			if (i == 5)
				mCep.append('-');
			mCep.append(ca[i]);
		}
		return mCep.toString();
	}
	
}
