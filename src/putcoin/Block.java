/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author piotrbajsarowicz
 */
public class Block {
    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();
//    private String[] transactions;
    private int nonce;
    
    private String blockHash;
    private String previousBlockHash;

    public Block(String previousBlockHash, ArrayList<Transaction>  transactions) throws NoSuchAlgorithmException {
        this.previousBlockHash = previousBlockHash;
        this.transactions = transactions;
        this.blockHash = this.generateHash();
        this.nonce = 0; // A temporary value
    }
    
    public String generateHash() throws NoSuchAlgorithmException {
        MessageDigest md;
        byte[] digest = null;
        
        String rawMessage = this.previousBlockHash + ":" +
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
     * @return the previousBlockHash
     */
    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    /**
     * @return the transactions
     */
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
    
    public boolean validateBlock(Block block) {
        return true;
    }

}
