package seedu.duke;

import java.util.regex.Pattern;

/**
 * Keeps customer-name rules consistent across order creation, editing and lookup commands.
 */
public final class CustomerNameValidator {
    /** Recognises extra fields while preserving the name abbreviation s/o (son of). */
    private static final Pattern EXTRA_PREFIX =
            Pattern.compile("(?:^|\\s)(?!s/o(?:\\s|$))[a-z]/", Pattern.CASE_INSENSITIVE);

    private CustomerNameValidator() {
    }

    /**
     * Rejects prefix-shaped fields that would make a stored customer name unusable in later commands.
     *
     * @param customer customer name already checked as non-empty by the caller
     * @param format command format included in an error message
     * @throws SystemException if the name contains an additional field
     */
    public static void validate(String customer, String format) throws SystemException {
        if (EXTRA_PREFIX.matcher(customer).find()) {
            throw new SystemException("The customer name cannot contain extra command fields. " + format);
        }
    }
}
