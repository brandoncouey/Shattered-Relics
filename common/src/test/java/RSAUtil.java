import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {

    private static String publicKey = "MIGiMA0GCSqGSIb3DQEBAQUAA4GQADCBjAKBhAD2B6d3KCa3CAYujKu+D78WiHx2ItxXfPJE/Ilu4jVy12lsoIotRE+1tZr+ptiSsJokxrgI0ddk02PX015ZuqBsMqVqEFm9L8PhE0U1o2C+zeCkDDtLkk4UxqIJNdztWWZnfyTfGR45rrVcOkK5FWbsGSGQ9dUd19dihVE/IA0OnlcgcwIDAQAB";
    private static String privateKey = "MIICgwIBADANBgkqhkiG9w0BAQEFAASCAm0wggJpAgEAAoGEAPYHp3coJrcIBi6Mq74PvxaIfHYi3Fd88kT8iW7iNXLXaWygii1ET7W1mv6m2JKwmiTGuAjR12TTY9fTXlm6oGwypWoQWb0vw+ETRTWjYL7N4KQMO0uSThTGogk13O1ZZmd/JN8ZHjmutVw6QrkVZuwZIZD11R3X12KFUT8gDQ6eVyBzAgMBAAECgYMQKjyUlazFhd9yG4gFOt+hSWJ0GKJFlU4NvlIBWReN7h1dJ8csZjqeg8olRS0hpjzagbwByFfQphr/SUH/v0VfFoq8GROnleyylSka7Vzy+zwcWktuiTL9740nFqOrDeG3+9d02dL8C8LbuihMw3uLxSnR96e2nxz6P/g4sJUdTP0wMQJCD8640/aHgWEv7lOffmiEhoFEMxbLNZ3pi+q7yq1eJp1GkCV+aGtwzdKJePX4WYYYJ+GRNyzWUb1CAZIBRbkZ7uIFAkIPkGn3pYITJUqvdgXLP82CHLF0AKkKfC6W+QjUcCoNJRb25f+M6RHbM8gXeHRtotclQXAra6pjrUrGLyc7gpNcKhcCQgyIgS8+Vm4UmqZCE0FsqTbkIyjGt9QJInVXZhHqp63CIYKE19b+6O+oMGuByqlgkqJ8TGEM4djpT/5ivzOC8bbf6QJCBUmxVvwaV1riJv7YNRe2QLBy8dCPngpW4eA2ScbZ4qFpTfUSf9Nnx6uuHZtFI0KiCfxTmcz4lOj5cJ/wml1gp319AkIM6hyFjlLjusU4+9BiQmgvNWv6sr/iuDBmvP50BUaDNLmXzCgYZUCV9BknEgnnefdhBnAe8HorR6VqtnAMyTXAfH4=";

    public static PublicKey getPublicKey(String publicKeyPair){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyPair.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String privateKeyPair){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyPair.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static byte[] encrypt(String data, String publicKeyPair) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKeyPair));
        return cipher.doFinal(data.getBytes());
    }

    public static String encryptedString(String message, String publicKeyPair) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException {
        return Base64.getEncoder().encodeToString(encrypt(message, publicKeyPair));
    }

    public static String decrypt(byte[] data, PrivateKey privateKeyPair) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKeyPair);
        return new String(cipher.doFinal(data));
    }

    public static String decrypt(String data, String privateKeyPair) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(privateKeyPair));
    }

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        try {
            String encryptedString = encryptedString("$2y$10$qe8W3R55F3LlC8MAHdqx..u68PJSHaqZRTRTOTYVTksjx36lnSauK", publicKey);
            System.out.println(encryptedString);
            String decryptedString = decrypt(encryptedString, privateKey);
            System.out.println(decryptedString);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }

    }
}
