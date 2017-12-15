/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    private ArrayList<Block> blocks = new ArrayList<Block>();
    
    private PublicKey pubKey;
    private PrivateKey privKey;
    private int RSA_KEY_SIZE = 2048;    
    private KeyPairGenerator keyPairGen = null;

    public Wallet() throws NoSuchAlgorithmException {
        this.keyPairGen = KeyPairGenerator.getInstance("RSA");
        this.keyPairGen.initialize(this.RSA_KEY_SIZE);
        KeyPair keyPair = this.keyPairGen.genKeyPair();
        this.pubKey = keyPair.getPublic();
        this.privKey = keyPair.getPrivate();
    }
    
    public void appendBlock(Block block) {
        this.blocks.add(block);
    }

    /**
     * @return the pubKey
     */
    public PublicKey getPubKey() {
        return pubKey;
    }

    /**
     * @return the privKey
     */
    public PrivateKey getPrivKey() {
        return privKey;
    }
    
    public String generateHash(String message) throws NoSuchAlgorithmException {
        MessageDigest md;
        byte[] digest = null;
        
        md = MessageDigest.getInstance("SHA-256");
        md.update(pubKey.getEncoded());
        digest = md.digest();

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
        int balance = 0;
        
        for (Block block : this.blocks) {
            for (Transaction transaction : block.getTransactions()) {
                if (transaction.getInput().contains(this.getPubKey())) {
                    balance += transaction.getAmount();
                } else if (transaction.getOutput().contains(this.getPubKey())) {
                    balance -= transaction.getAmount();
                }
            }
        }
        
        return balance;
    }
    
    public Transaction createTransaction(PublicKey receiverPubKey, int amount) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, SignatureException {
        String transactionRaw = this.getPubKey() + ":" +
                                receiverPubKey + ":" +
                                amount;
        
        ArrayList input = new ArrayList();
        input.add(receiverPubKey);
        
        ArrayList output = new ArrayList();
        output.add(this.getPubKey());
        
        String signature = this.sign(transactionRaw);
        
        String valueToHash = transactionRaw + ":" + signature;
        String hash = this.generateHash(valueToHash);
    
        return new Transaction(input, output, amount, hash, signature);
    }
}
