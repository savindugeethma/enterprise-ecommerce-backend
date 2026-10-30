package lk.dropti.ninewood.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class PayHereUtil {

    private static final String MERCHANT_ID = "1231403";
    private static final String MERCHANT_SECRET = "MjIyNTA3MDU2MzYzNTQxMTcwNDE1MDQ3NzA3OTMzNTA3NjU4MzM0";

    public static final String APP_CURRENCY = "LKR";

    public static String getMerchantId() { return MERCHANT_ID; }

    public static String generateHash(String orderId, double amount) {
        String formattedAmount = String.format(Locale.US, "%.2f", amount);
        String secretHash = md5(PayHereUtil.MERCHANT_SECRET).toUpperCase();
        String raw = PayHereUtil.MERCHANT_ID + orderId + formattedAmount + PayHereUtil.APP_CURRENCY + secretHash;
        return md5(raw).toUpperCase();
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5"); // සර්ගේ විදිහමයි
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 ERROR: " + e);
        }
    }
}