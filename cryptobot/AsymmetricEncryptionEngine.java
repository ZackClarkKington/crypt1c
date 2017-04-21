package crypt1c.cryptobot;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

/**
 * Created by Zack on 18/04/2017.
 */
public class AsymmetricEncryptionEngine {
    private Cipher cipher;
    public AsymmetricEncryptionEngine() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException{
        this.cipher = Cipher.getInstance("RSA");
    }

    public byte[] encrypt(byte[] input, PublicKey key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        this.cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] cipherText = cipher.doFinal(input);
        return cipherText;
    }
    public String decrypt(byte[] input, PrivateKey key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        this.cipher.init(Cipher.DECRYPT_MODE,key);
        byte[] plainText = cipher.doFinal(input);
        return new String(plainText);
    }
}
