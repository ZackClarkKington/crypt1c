package crypt1c.client;
import java.security.*;

/**
 * Created by Zack on 18/04/2017.
 */
public class KeyGen {
    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public KeyGen(int keylength){
        try {
            this.keyGen = KeyPairGenerator.getInstance("RSA");
            this.keyGen.initialize(keylength);
        }catch(NoSuchAlgorithmException e){
            System.err.println("Unable to generate keypair for key exchange");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void createKeys(){
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PrivateKey getPrivateKey(){
        return this.privateKey;
    }

    public PublicKey getPublicKey(){
        return this.publicKey;
    }
}
