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
import putcoin.exceptions.InsufficientFundsException;

/**
 *
 * @author piotrbajsarowicz
 */
public class Genesis {
    private Block genesisBlock;
    private Transaction genesisTransaction;
    
    public Transaction createGenesisTransaction(Wallet receiver) throws NoSuchAlgorithmException {                
        Blockchain blockchain = Blockchain.getInstance();
        ArrayList<TransactionInfo> transactionInfo = new ArrayList<TransactionInfo>();
        
        transactionInfo.add(new TransactionInfo(null, receiver, blockchain.REWARD, genesisBlock));
        
        Utils.log("Genesis Transaction ==[" + blockchain.REWARD + " PTC]==> " + receiver.getDisplayName());
        
        try {
            genesisTransaction = new Transaction(transactionInfo);
        } catch (InsufficientFundsException ex) {
            Logger.getLogger(Genesis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return genesisTransaction;
    }
    
    public Block createGenesisBlock() throws NoSuchAlgorithmException {
        genesisBlock = new Block(null, "GENESIS");

        return genesisBlock;
    }
}
