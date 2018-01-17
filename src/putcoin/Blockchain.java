/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
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
    
    protected Blockchain() {}
    
    public static Blockchain getInstance() {
        if(instance == null) {
            instance = new Blockchain();
        }
        return instance;
    }

    public boolean confirmBlock(Block block) throws ConfirmBlockException {
        if (
            block.getDisplayName() != "GENESIS" &&
            !this.validateBlock(block)
        ) {
            System.out.println("A block validation failed. Cannot add the block to the blockchain");
            
            throw new ConfirmBlockException();
        } else {
            try {
                block.setNonce();
                block.setBlockHash();
                blocks.add(block);
                
                for (Transaction transaction : block.getTransactions()) {
                    transaction.spend();
                }
                
                System.out.println("Successfully added a block to the blockchain");
                
                return true;
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Blockchain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }
    
    public Boolean validateBlock(Block block) {
        for (Transaction transaction : block.getTransactions()) {
            Boolean status = (
                transaction.verifySignature() && transaction.isSpend()
            );

            if (!status) {
                System.out.println("CANNOT CONFIRM BLOCK");
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
}
