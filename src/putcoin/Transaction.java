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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

public class Transaction {
//    private ArrayList input = new ArrayList();
//    private ArrayList output = new ArrayList();
//    private PublicKey sender;
//    private PublicKey receiver;
    private Wallet sender;
    private Wallet receiver;
    private int amount;
    private String hash;
    private String signature;

    public Transaction(Wallet sender, Wallet receiver, int amount, String signature) throws NoSuchAlgorithmException {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.hash = this.generateHash();
        this.signature = signature;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return the sender
     */
    public Wallet getSender() {
        return sender;
    }
    
    /**
     * @return the receiver
     */
    public Wallet getReceiver() {
        return receiver;
    }
    
    public String getMessage() {        
        String message = this.receiver.getPubKey() + ":" +
                         this.amount;
        
        if (this.sender == null) {
            return "genesis:" + message;
        } else {
            return this.sender.getPubKey() + ":" + message;
        }
    }
    
    public String generateHash() throws NoSuchAlgorithmException {
        String valueToHash = this.getMessage() + ":" + this.signature;
        
        MessageDigest md;
        byte[] digest = null;
        
        md = MessageDigest.getInstance("SHA-256");
        
        if (sender != null) {
            md.update(sender.getPubKey().getEncoded());
        }
        
        digest = md.digest(valueToHash.getBytes(StandardCharsets.UTF_8));

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & digest[i]));
        }
        
        return hexString.toString();
    }
    
    public boolean verifySignature() throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IOException, SignatureException {
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initVerify(this.sender.getPubKey());
        
        byte[] messageData = this.getMessage().getBytes("UTF8");
        sig.update(messageData);
        
        return sig.verify(new BASE64Decoder().decodeBuffer(this.signature));
    }
    
    public boolean verifyAmount() {
        if (sender == null) {
            return amount > 0;
        } else {
            return (
                amount > 0 &&
                sender.getBalance(false) > amount 
            );
        }
    }
    
    public Status verify() throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IOException, SignatureException {
        boolean ok;
        String reason = "Verified transaction for " + amount + " PTC";
        boolean isAmountCorrect = this.verifyAmount();
        
        if (sender == null) {
            ok = isAmountCorrect;
        } else {
            ok = (
                isAmountCorrect &&
                this.verifySignature()
            );
        }
        
        if (!ok) {
            if (!isAmountCorrect) {
                reason = "Amount " + amount + " PTC is incorrect (too low/high or an insufficient balance of a sender's wallet).";
            } else {
                reason = "Failed due to signature verification";
            }
        }
        
        return new Status(ok, reason);
    }
}
