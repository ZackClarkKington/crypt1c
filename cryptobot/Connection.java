package crypt1c.cryptobot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.util.encoders.Base64;

/**
 * Created by Zack on 18/04/2017.
 */
public class Connection extends Thread{
    private Socket ircServer = null;
    private String host;
    private int port;
    Connection(String host, int port){
        this.host = host;
        this.port = port;
    }
    @Override
    public void run(){
        try(
                Socket newConn = new Socket(this.host,this.port);
                PrintWriter out = new PrintWriter(newConn.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(newConn.getInputStream()));
        ){
            boolean running = true;
            while(running) {
                System.out.println(in.readLine());
                System.out.println(in.readLine());
                out.print("NICK CryptoBot\r\n");
                out.flush();
                String ping = in.readLine();
                if(ping.contains("PING")){
                    out.print(ping.replaceAll("PING","PONG") + "\r\n");
                    out.flush();
                    System.out.println(ping);
                    System.out.println(ping.replaceAll("PING","PONG"));
                }
                out.print("USER Crypt1c 0 * :Zack Clark-Kington\r\n");
                out.flush();
                out.flush();
                String message = in.readLine();
                while (message != null) {
                    if(message.contains("001")){
                        out.print("JOIN #crypt1c\r\n");
                        out.flush();
                        out.flush();
                    }
                    else if(message.contains("PING")){
                        out.print(message.replaceAll("PING","PONG") + "\r\n");
                        out.flush();
                    }
                    else if(message.contains("CRYPT1C-PUBLIC-KEY")){
                        try {
                            String[] messageArr = message.split("\\|");
                            String publicKeyString = messageArr[1].replace("\r\n","");
                            X509EncodedKeySpec publicKey = new X509EncodedKeySpec(Base64.decode(publicKeyString));
                            KeyFactory factory = KeyFactory.getInstance("RSA");
                            AsymmetricEncryptionEngine engine = new AsymmetricEncryptionEngine();
                            String symKey = Base64.toBase64String(CryptoBot.engine.getKey().getEncoded());
                            System.out.println("KEY: " + symKey);
                            byte[] keyToSend = engine.encrypt(Base64.decode(symKey), factory.generatePublic(publicKey));
                            out.print("PRIVMSG #crypt1c :SYM-KEY|" + Base64.toBase64String(keyToSend) + "\r\n");
                            out.flush();
                            out.flush();
                            System.out.println("should have returned key by now");
                        }catch (Exception e){
                            System.err.println(e.getMessage());
                            e.printStackTrace();
                            System.exit(1);
                        }
                    }
                    else if(message.contains("ENC|")){
                        String[] messageArr = message.split("\\|");
                        String encMsg = messageArr[1].replace("\r\n","");
                        System.out.println(messageArr[1]);
                        try {
                            System.out.println("Decrypted: " + CryptoBot.engine.decrypt(Base64.decode(encMsg)));
                        }catch(Exception e){
                            e.printStackTrace();
                            System.exit(1);
                        }
                    }
                    System.out.println(message);
                    message = in.readLine();
                }
            }
        }catch(IOException e){
            System.out.println("ERROR - Unable to connect to IRC server");
        }
    }
}
