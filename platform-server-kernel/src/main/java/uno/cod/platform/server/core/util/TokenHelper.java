package uno.cod.platform.server.core.util;

import org.springframework.security.crypto.codec.Base64;

public class TokenHelper {
    /**
     * Decodes given {@link String} using Base64 and splits it by the colon character.
     *
     * @throws IllegalArgumentException if <code>encoded</code> is <code>null</null> or
     * it's decoded form does not contain two non-empty substring separated by exactly
     * one colon.
     * @return the two parts of the decoded string
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-2">RFC 7235, Section 2</a>
     */
    public static String[] decodeAndSplit(String encoded) {
        final String[] result = new String(Base64.decode(encoded.getBytes())).split(":");

        if (result.length != 2) {
            throw new IllegalArgumentException("Decoded string does not contain two non-empty substrings separated by exactly one colon.");
        }

        return result;
    }
}
