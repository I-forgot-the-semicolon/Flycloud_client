package security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class AES
{
    private static SecretKeySpec keySpec;
    public static String randomHash;

    private static void SetKey(String inputKey)
    {
        MessageDigest sha = null;
        try
        {
            byte[] key = inputKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            keySpec = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    public static String RandomKey()
    {
        char[] tmpString = new char[20];
        Random random = new Random();
        for(int i = 0; i < 20; i++)
        {
            tmpString[i] = (char)(random.nextInt((122-65) + 1) + 65);
        }
        randomHash = new String(tmpString);
        return randomHash;
    }

    public static String Encrypt(String inputString, String key)
    {
        String encryptedString = null;
        SetKey(key);
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            encryptedString = Base64.getEncoder().encodeToString(cipher.doFinal(inputString.getBytes(StandardCharsets.UTF_8)));
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }

        return encryptedString;
    }

}
