package userservice.util;

/**
 * Маскирует email перед отдачей наружу через REST API.
 */
public final class EmailMasker {

    private static final String EMAIL_SEPARATOR = "@";
    private static final String MASK = "***";
    private static final int MIN_VISIBLE_LOCAL_PART_LENGTH = 2;
    private static final int FIRST_VISIBLE_SYMBOL_INDEX = 0;

    private EmailMasker() {
    }

    public static String mask(String email) {
        if (email == null || email.isBlank()) {
            return MASK;
        }

        String[] parts = email.split(EMAIL_SEPARATOR, 2);

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            return MASK;
        }

        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() < MIN_VISIBLE_LOCAL_PART_LENGTH) {
            return MASK + EMAIL_SEPARATOR + domain;
        }

        return localPart.charAt(FIRST_VISIBLE_SYMBOL_INDEX) + MASK + EMAIL_SEPARATOR + domain;
    }
}