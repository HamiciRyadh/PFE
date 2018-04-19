package usthb.lfbservices.com.pfe.utils;

/**
 * Created by ryadh on 16/04/2018.
 */

public class FormValidation {

    /**
     * Tests if the given String is a mail address using a regex.
     * @param mailAddress The String to test with the regex.
     * @return true if the parameter is a mail address, false otherwise.
     */
    public static boolean isMailAddress(final String mailAddress) {
        if (mailAddress == null) return false;
        return mailAddress.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    /**
     * Tests if the given String is a password fitting the specifications using a regex.
     * @param password The String to test with the regex.
     * @return true if the parameter corresponds to the password specifications, false otherwise.
     */
    // ^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$ at least 1 letter and 1 digit
    public static boolean isPassword(final String password) {
        if (password == null) return false;
        return password.matches("^[a-zA-Z0-9_-]{5,30}");
    }
}
