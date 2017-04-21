package crypt1c.client;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.params.DESedeParameters;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.security.SecureRandom;
import java.security.Security;
/**
 * Created by Zack on 18/04/2017.
 */
public class SymmetricEncryptionEngine {
    private Cipher cipher = null;
    private BufferedInputStream in = null;
    private BufferedOutputStream out = null;
    private byte[] keyBytes = null;
    private SecretKeySpec key = null;
    private int ctLength = 0;
    public SymmetricEncryptionEngine(){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());//This line is very important, don't remove or you'll break encryption
    }
    public SymmetricEncryptionEngine(SecretKeySpec key){
        this.key = key;
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());//This line is very important, don't remove or you'll break encryption
    }
    public void generateKey(){
        SecureRandom sr = null;
        try{
            sr = new SecureRandom();
        }catch (Exception e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        KeyGenerationParameters kgp = new KeyGenerationParameters(sr,DESedeParameters.DES_EDE_KEY_LENGTH*8);
        DESedeKeyGenerator kg = new DESedeKeyGenerator();
        kg.init(kgp);
        this.keyBytes = kg.generateKey();
        this.key = new SecretKeySpec(keyBytes,"AES");
    }
    public byte[] encrypt(String toEncrypt) throws Exception{
        cipher = Cipher.getInstance("AES/ECB/PKCS7Padding","BC");
        cipher.init(Cipher.ENCRYPT_MODE,this.key);
        byte[] input = toEncrypt.getBytes();
        byte[] cipherText = cipher.doFinal(input);
        return cipherText;
    }
    public String decrypt(byte[] toDecrypt) throws Exception{
        cipher = cipher.getInstance("AES/ECB/PKCS7Padding","BC");
        cipher.init(Cipher.DECRYPT_MODE,this.key);
        byte[] plainText = cipher.doFinal(toDecrypt);
        return new String(plainText);
    }
}
