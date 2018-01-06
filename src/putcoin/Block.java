/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author piotrbajsarowicz
 */
public class Block {
    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private ArrayList<Transaction> rejectedTransactions = new ArrayList<Transaction>();
//    private String[] transactions;
    private String displayName;
    private int nonce;
    
    private String blockHash;
    private Block previousBlock;

    public Block(Block previousBlock, String displayName) throws NoSuchAlgorithmException {
        this.previousBlock = previousBlock;
        this.blockHash = this.generateHash();
        this.nonce = 0; // A temporary value
        this.displayName = displayName;
    }
    
    public String generateHash() throws NoSuchAlgorithmException {
        MessageDigest md;
        byte[] digest = null;
        
        String rawMessage = this.getPreviousBlockHash() + ":" +
                            this.transactions + ":" +
                            this.nonce;
        
        md = MessageDigest.getInstance("SHA-256");
        md.update(rawMessage.getBytes());
        digest = md.digest();

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & digest[i]));
        }
        
        return hexString.toString();
    }

    /**
     * @return the nonce
     */
    public int getNonce() {
        return nonce;
    }

    /**
     * @return the blockHash
     */
    public String getBlockHash() {
        return blockHash;
    }
    
    /**
     * @return the previousBlock
     */
    public Block getPreviousBlock() {
        return this.previousBlock;
    }

    /**
     * @return the previousBlockHash
     */
    public String getPreviousBlockHash() {
        if (this.previousBlock != null) {
            return this.previousBlock.blockHash;
        }
        
        return "";
    }

    /**
     * @return the transactions
     */
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean addTransaction(Transaction transaction) throws InvalidKeyException, NoSuchAlgorithmException, IOException, UnsupportedEncodingException, SignatureException {
        Status status = transaction.verify();
        System.out.println(status.getReason());
        
        if (status.isOk()) {
            transactions.add(transaction);
            
            return true;
        } else {
            rejectedTransactions.add(transaction);
            
            return false;
        }
    }
}
