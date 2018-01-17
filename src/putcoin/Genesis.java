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
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import putcoin.exceptions.InsufficientFundsException;

/**
 *
 * @author piotrbajsarowicz
 */
public class Genesis {
    private Transaction genesisTransaction;
    
    public Transaction createGenesisTransaction(Wallet receiver, int amount, Block targetBlock) throws NoSuchAlgorithmException {                
        System.out.println("Genesis Transaction ==[" + amount + "PUTCoins]==> " + receiver.getPubKey().hashCode());
        
        ArrayList<TransactionInfo> transactionInfo = new ArrayList<TransactionInfo>();
        transactionInfo.add(new TransactionInfo(receiver, amount));
        
        try {
            genesisTransaction = new Transaction(null, transactionInfo, targetBlock);
        } catch (InsufficientFundsException ex) {
            Logger.getLogger(Genesis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return genesisTransaction;
    }
    
    public Block createGenesisBlock() throws NoSuchAlgorithmException {
        Block genesisBlock = new Block(null, "GENESIS");

        return genesisBlock;
    }
}
