/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

/**
 *
 * @author piotrbajsarowicz
 */
public class Wallet {
    private String displayName;
    private PublicKey pubKey;
    private PrivateKey privKey;
    private int RSA_KEY_SIZE = 2048;    
    private KeyPairGenerator keyPairGen = null;

    public Wallet(String displayName) throws NoSuchAlgorithmException {
        this.keyPairGen = KeyPairGenerator.getInstance("RSA");
        this.keyPairGen.initialize(this.RSA_KEY_SIZE);
        KeyPair keyPair = this.keyPairGen.genKeyPair();
        this.pubKey = keyPair.getPublic();
        this.privKey = keyPair.getPrivate();
        this.displayName = displayName;
    }

    /**
     * @return the pubKey
     */
    public PublicKey getPubKey() {
        return pubKey;
    }
    
    public String generateHash(String message) throws NoSuchAlgorithmException {
        MessageDigest md;
        byte[] digest = null;
        
        md = MessageDigest.getInstance("SHA-256");
        md.update(pubKey.getEncoded());
        digest = md.digest(message.getBytes(StandardCharsets.UTF_8));

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & digest[i]));
        }
        
        return hexString.toString();
    }
    
    public String sign(String message) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, SignatureException {
        byte[] messageData = message.getBytes("UTF8");
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initSign(this.privKey);
        sig.update(messageData);
        byte[] signatureBytes = sig.sign();
        
        return new BASE64Encoder().encode(signatureBytes);
    }
    
    public boolean verify(String signature, String message, PublicKey pubKey) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, SignatureException, IOException {
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initVerify(pubKey);
        byte[] messageData = message.getBytes("UTF8");
        sig.update(messageData);
        byte[] signatureBytes = new BASE64Decoder().decodeBuffer(signature);
        
        return sig.verify(signatureBytes);
    }
    
    public int getBalance() {
        return getBalance(true);
    }
    
    public int getBalance(boolean prompt) {
        int balance = 0;
        Blockchain blockchain = Blockchain.getInstance();
        
        for (Block block : blockchain.getBlocks()) {
            for (Transaction transaction : block.getTransactions()) {
                if (
                    transaction.getSender() != null &&
                    transaction.getSender().getPubKey() == this.getPubKey()
                ) {
                    balance -= transaction.getAmount();
                } else if (transaction.getReceiver().getPubKey() == this.getPubKey()) {
                    balance += transaction.getAmount();   
                }
            }
        }
        
        if (prompt) {
            System.out.println("| [" + this.getPubKey().hashCode() + "] " + balance);
        }
        
        return balance;
    }
    
    public String getMessage(PublicKey receiverPubKey, int amount) {
        return this.getPubKey() + ":" +
               receiverPubKey + ":" +
               amount;
    }
    
    public Transaction createTransaction(Wallet receiver, int amount) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, SignatureException {
        String transactionRaw = this.getMessage(receiver.getPubKey(), amount);
        String signature = this.sign(transactionRaw);
        
        System.out.println(this.getPubKey().hashCode() + " ==[" + amount + " PUTCoins]==> " + receiver.getPubKey().hashCode());
    
        return new Transaction(this, receiver, amount, signature);
    }
}
