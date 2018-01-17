/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author piotrbajsarowicz
 */
public class Block {
    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private ArrayList<Transaction> rejectedTransactions = new ArrayList<Transaction>();
    private int nonce;    
    private String blockHash;
    private Block previousBlock;
    private String displayName;
    
    private int TARGET_THRESHOLD = 10000001;

    /**
     *
     * @param previousBlock
     * @param displayName
     */
    public Block(Block previousBlock, String displayName) {
        this.previousBlock = previousBlock;
        this.displayName = displayName;
    }
    
    /**
     * @return the nonce
     */
    public int getNonce() {
        return nonce;
    }
    
    /**
     * @param nonce the nonce to set
     */
    public void setNonce() {
        this.nonce = generateNonce();
    }

    /**
     * @return the blockHash
     */
    public String getBlockHash() {
        return blockHash;
    }
    
    /**
     * @param blockHash the blockHash to set
     */
    public void setBlockHash() throws NoSuchAlgorithmException {
        this.blockHash = generateHash();
    }
    
    /**
     * @return the previousBlock
     */
    public Block getPreviousBlock() {
        return previousBlock;
    }

    /**
     * 
     * @return the previousBlockHash
     */
    public String getPreviousBlockHash() {
        if (isGenesisBlock()) {
            return "";
        }
        
        return previousBlock.blockHash;
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
    
    /**
     * Lack of a previous block means the genesis block.
     * 
     * @return if a block is a genesis
     */
    public Boolean isGenesisBlock() {
        return previousBlock == null;
    }
    
    /**
     * Returns a raw message that will be used for generating a block's hash.
     * 
     * @return a message to be hashed
     */
    public String getRawMessage() {
        return getPreviousBlockHash() + ":" +
               transactions + ":" +
               nonce;
    }
    
    /**
     * Generates a hash of a block.
     * 
     * @return a block's hash
     * @throws NoSuchAlgorithmException
     */
    public String generateHash() throws NoSuchAlgorithmException {
        MessageDigest md;
        byte[] digest = null;
        
        String rawMessage = getRawMessage();
        
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
     * Generates a nonce as long as it's lower or equal to target threshold.
     * 
     * @return
     */
    public int generateNonce() {
        Random random = new Random();
        int nonce = TARGET_THRESHOLD;
        
        long startTime = System.currentTimeMillis();
        
        while (nonce >= TARGET_THRESHOLD) {
            nonce = nonce = 10000000 + random.nextInt(90000000);
        }
        
        long estimatedTime = System.currentTimeMillis() - startTime;
        
        System.out.println("nonce = " + nonce + " (it took " + estimatedTime/ (double) 1000 + "s to find it)");
        
        return nonce;
    }
    
    /**
     * Adds a transaction to the block.
     * Verifies transaction, protects against double spending.
     * 
     * @param transaction
     * @return status of adding a transaction to the block.
     */
    public boolean addTransaction(Transaction transaction) {
        Status status = transaction.verify(this);

        if (status.isOk()) {
            for (Transaction.Input input : transaction.getInputs()) {

                Transaction.Output originOutput = input.getOriginOutput();

                // Protects against double spending (spending an already spent transation)
                if (originOutput.isSpent()) {
                    return false;
                }
            }

            transactions.add(transaction);

            return true;
        }
        
        rejectedTransactions.add(transaction);
        
        System.err.println(
            status.getReason() +
            "Transaction has been rejected and won't be included in the block."
        );

        return false;
    }
}
