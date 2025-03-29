package controller;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidations {
    /**
     * Função para validar um email com regex
     * @param email
     * @return true or false dependendo se foi validado ou não
     */
    public boolean validarEmail(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+[-*_?$&]*+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9]{2,}$");
        Matcher matcher = pattern.matcher(email);

        boolean matchFound  = matcher.find();

        return matchFound;
    }

    /**
     * Função para validar um número de telemóvel com regex
     * @param telefone
     * @return true or false dependendo de foi validado ou não
     */
    public boolean validarTelemovel(String telefone){
        Pattern pattern = Pattern.compile("^[923]\\d{8}$");
        Matcher matcher = pattern.matcher(telefone);

        boolean matchFound  = matcher.find();

        return matchFound;
    }

}
