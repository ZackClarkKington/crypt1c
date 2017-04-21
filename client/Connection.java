package crypt1c.client;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
/**
 * Created by Zack on 18/04/2017.
 */
public class Connection extends Thread{
    private Socket ircServer = null;
    private String host;
    private int port;
    private SecretKeySpec msgKey = null;
    private KeyGen keyGen = null;
    private PrintWriter outbound = null;
    private String nick;
    private String user;
    private Controller viewController = null;
    private boolean running = true;
    Connection(String host, int port, String nick, String user){
        this.host = host;
        this.port = port;
        this.nick = nick;
        this.user = user;
    }
    public void setViewController(Controller viewController){
        this.viewController = viewController;
    }
    @Override
    public void run(){
        try(
                Socket newConn = new Socket(this.host,this.port);
                PrintWriter out = new PrintWriter(newConn.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(newConn.getInputStream()));
        ){
            this.outbound = out;
            while(this.running) {
                System.out.println(in.readLine());
                System.out.println(in.readLine());
                out.print("NICK " + nick + "\r\n");
                out.flush();
                String ping = in.readLine();
                if(ping.contains("PING")){
                    out.print(ping.replaceAll("PING","PONG") + "\r\n");
                    out.flush();
                    System.out.println(ping);
                    System.out.println(ping.replaceAll("PING","PONG"));
                }
                out.print("USER " + user + " 0 * :Crypt1c IRC Client\r\n");
                out.flush();
                out.flush();
                String message = in.readLine();
                while (message != null) {
                    if(message.contains("001")){
                        joinChannel("crypt1c");
                        keyGen = new KeyGen(1024);
                        keyGen.createKeys();
                        PublicKey publicKey = keyGen.getPublicKey();
                        String publicKeyString = Base64.toBase64String(publicKey.getEncoded());
                        out.print("PRIVMSG #crypt1c :CRYPT1C-PUBLIC-KEY|" + publicKeyString + "\r\n");
                        out.flush();
                    }
                    else if(message.contains("PING")){
                        out.print(message.replaceAll("PING","PONG") + "\r\n");
                        out.flush();
                    }
                    else if(message.contains("SYM-KEY")){
                        try {
                            String[] messageArr = message.split("\\|");
                            String symKeyString = messageArr[1].replace("\r\n", "");
                            AsymmetricEncryptionEngine asymEng = new AsymmetricEncryptionEngine();
                            String symKey = asymEng.decrypt(Base64.decode(symKeyString), keyGen.getPrivateKey());
                            this.msgKey = new SecretKeySpec(Base64.decode(symKey), "AES");
                            viewController.addMsg("KEY RECEIVED --> " + symKey);
                            out.print("PART #crypt1c\r\n");
                            out.flush();
                            viewController.addMsg("Encryption enabled, you may wish to join a channel now");
                        }catch (Exception e){
                            e.printStackTrace();
                            System.exit(1);
                        }
                    }
                    else if(message.contains("ENC|")){
                        SymmetricEncryptionEngine engine = new SymmetricEncryptionEngine(this.msgKey);
                        String[] messageArr = message.split("\\|");
                        String encMsg = messageArr[1].replace("\r\n","");
                        System.out.println(messageArr[1]);
                        try {
                            String decryptedMsg = engine.decrypt(Base64.decode(encMsg));
                            System.out.println("Decrypted: " + decryptedMsg);
                            viewController.addMsg(decryptedMsg);
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
            viewController.addMsg("ERROR - Unable to connect to IRC server");
        }
    }
    public void joinChannel(String channelName){
        outbound.print("JOIN #" + channelName + "\r\n");
        outbound.flush();
        viewController.addMsg("Joined channel " + channelName);
    }
    public void leaveChannel(String channelName){
        outbound.print("PART #" + channelName + "\r\n");
        outbound.flush();
        viewController.addMsg("Left channel " + channelName);
    }
    public void sendMessage(String toSend, String channelName){
        try {
            SymmetricEncryptionEngine engine = new SymmetricEncryptionEngine(this.msgKey);
            byte[] msg = engine.encrypt(toSend);
            String msgString = Base64.toBase64String(msg);
            System.out.println("ENC-MSG: " + msgString);
            outbound.print("PRIVMSG #" + channelName + " :ENC|" + msgString + "\r\n");
            outbound.flush();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
    public void close(){
        this.outbound.print("QUIT\r\n");
        this.outbound.flush();
        this.running = false;
    }
}
