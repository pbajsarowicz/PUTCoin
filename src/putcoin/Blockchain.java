/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import putcoin.exceptions.ConfirmBlockException;

/**
 *
 * @author piotrbajsarowicz
 */
public class Blockchain {
    private static Blockchain instance = null;
    private ArrayList<Block> blocks = new ArrayList<Block>();
    private ArrayList<Block> unconfirmedBlocks = new ArrayList<Block>();
    public static final int REWARD = 50;
    
    protected Blockchain() {}
    
    public static Blockchain getInstance() {
        if(instance == null) {
            instance = new Blockchain();
        }
        return instance;
    }

    public ArrayList<Block> getUnconfirmedBlocks() {
        return unconfirmedBlocks;
    }
    

    public boolean addBlock(Block block) throws ConfirmBlockException {
        if (
            block.getDisplayName() != "GENESIS" &&
            !this.validateBlock(block)
        ) {
            Utils.log("A block validation failed. Cannot add the block to the blockchain");
            
            throw new ConfirmBlockException();
        } else {
            try {
                block.setNonce();
                block.setBlockHash();
                blocks.add(block);
                unconfirmedBlocks.remove(block);
                
                for (Transaction transaction : block.getTransactions()) {
                    transaction.spend();
                }
                
                Utils.log("Successfully added a block to the blockchain");
                
                return true;
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Blockchain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }
    
    public Boolean validateBlock(Block block) {
        for (Transaction transaction : block.getTransactions()) {
            Boolean status = false;
            
            if (transaction.isReward()) {
                status = !transaction.isSpent();
            } else {
                status = transaction.verifySignature() && !transaction.isSpent();
            }

            if (!status) {
                Utils.log("CANNOT CONFIRM BLOCK " + block.getDisplayName());
                return false;
            }
        };
        return true;
    }

    /**
     * @return the blocks
     */
    public ArrayList<Block> getBlocks() {
        return blocks;
    }
    
    /**
     * @return the block
     */
    public Block getLastBlock() {
        return blocks.get(blocks.size() - 1);
    }
}
