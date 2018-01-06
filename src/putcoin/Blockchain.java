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
    
    public boolean addBlock(Block block) {
        if (
            block.getDisplayName() != "GENESIS" &&
            !this.validateBlock(block)
        ) {
            System.out.println("A block validation failed. Cannot add the block to the blockchain");
            
            return false;
        } else {
            this.getBlocks().add(block);

            System.out.println("Successfully added a block to the blockchain");

            return true;
        }
    }
    
    public boolean validateBlock(Block block) {
        block.getTransactions().forEach((transaction) -> {
            try {
                Status status = transaction.verify();
                
                if (!status.isOk()) {
                    System.out.println("CANNOT CONFIRM BLOCK");
                }
            } catch (InvalidKeyException ex) {
                Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SignatureException ex) {
                Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return true;
    }

    /**
     * @return the blocks
     */
    public ArrayList<Block> getBlocks() {
        return blocks;
    }
}
