/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    
    private int TARGET_THRESHOLD = 290;

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
     * Lack of a previous block means it's a genesis block.
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
     * Allows to provide extra nonce. It's being used while generating a nonce.
     * 
     * @param _nonce
     * @return a message to be hashed
     */
    public String getRawMessage(int _nonce) {
        return getPreviousBlockHash() + ":" +
               transactions + ":" +
               _nonce;
    }
    
    /**
     * Generates a hash of a block.
     * 
     * @return a block's hash
     */
    public String generateHash() {
        return generateHash(0);
    }
    
    /**
     * Generates a hash of a block.
     * 
     * @param message used for nonce generation
     * @return a block's hash
     */
    public String generateHash(int _nonce) {
        try {
            String rawMessage = (
                _nonce == 0 ? getRawMessage() : getRawMessage(_nonce)
            );
            MessageDigest md;
            byte[] digest = null;
            
            md = MessageDigest.getInstance("SHA-256");
            md.update(rawMessage.getBytes());
            digest = md.digest();
            
            StringBuffer hexString = new StringBuffer();
            
            for (int i = 0; i < digest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * Generates a nonce as long as it's lower than a target threshold.
     * It generates a random 8 characters long number (nonce candidate) and
     * add it to the block's hash. Then generates a hash of it (converted
     * to String), calculates a sum of all numeric characters being part of
     * that hash and finally, it checks if it's higher than TARGET_THRESHOLD.
     * 
     * e.g.
     * hash = `d389759084e819873f8388a7997f92f8b9b87488a6989911dd637a80281a4a99`
     * sum = 311
     * 
     * @return
     */
    public int generateNonce() {
        TARGET_THRESHOLD += Blockchain.getInstance().getBlocks().size();
        
        Random random = new Random();
        int _nonce;
        
        long startTime = System.currentTimeMillis();
        
        while (true) {
            _nonce = 10000000 + random.nextInt(90000000);
            String _hash = generateHash(_nonce);
            
            try {
                char character;
                int count = 0;
                for(int i=0; i<_hash.length(); i++) {
                    character  = _hash.charAt(i);
                    
                    try {
                        count = count + Integer.parseInt(_hash.valueOf(character));
                    } catch(NumberFormatException e) {
                        continue;
                    }
                }
                
                if (count > TARGET_THRESHOLD) {
                    break;
                }
            } catch(NumberFormatException e) {
                continue;
            }
        }
        
        long estimatedTime = System.currentTimeMillis() - startTime;
        Utils.log("\n");
        Utils.log("nonce = " + _nonce + " (it took " + estimatedTime/ (double) 1000 + "s to find it)");
        
        return _nonce;
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
        
        Utils.log(
            status.getReason() +
            "Transaction has been rejected and won't be included in the block."
        );

        return false;
    }
}
