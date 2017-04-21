package crypt1c.cryptobot;
/**
 * Created by Zack on 18/04/2017.
 */

public class CryptoBot{
    public static SymmetricEncryptionEngine engine = null;
    public static void main(String[] args) {
        try{
            engine = new SymmetricEncryptionEngine();
            engine.generateKey();
            Connection server = new Connection("shell.zackck.me",6667);
            server.start();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
