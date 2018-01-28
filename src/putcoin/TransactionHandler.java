/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import putcoin.exceptions.CannotCreateTransactionException;
import putcoin.exceptions.ConfirmBlockException;
import putcoin.exceptions.InsufficientFundsException;

/**
 *
 * @author piotrbajsarowicz
 */
public class TransactionHandler implements Runnable {
    ArrayList<Wallet> wallets;
    Block genesisBlock;
    Block lastBlock;
    
    private BlockingQueue transfers = null;

    public ArrayList<Wallet> getWallets() {
        return wallets;
    }
    
    public TransactionHandler(TransactionGenerator init, BlockingQueue transfers) {
        this.transfers = transfers;
        wallets = init.getWallets();
    }
    
    public void getBalances() {
        Utils.log("---------------------------------------- balances");
        for (Wallet wallet : wallets) {
            Utils.log("| [" + wallet.getDisplayName() + "] " + wallet.getBalance());
        }
        Utils.log("-------------------------------------------------");
    }
    
    public Wallet getRandomWallet() {
        Random randomGenerator = new Random();
        
        return wallets.get(randomGenerator.nextInt(wallets.size()));
    }
    
    /**
     * Creates actual transaction objects, adds them to a block, confirms within 
     * the block.
     * 
     * @param transactionInfoLists - list of transactionInfo objects (list of
     * lists), where each object represents an info about a single output.
     * e.g.
     * list: [T1, T2]
     * T1: [TransInfo1, TransInfo2]                              (two outputs)
     * T2: [TransInfo3, TransInfo4, TransInfo4]                  (three outputs)
     */
    
    public void handleTransaction(ArrayList<ArrayList<TransactionInfo>> transactionInfoLists) {
        Block block = null;
        Wallet sender;
        Wallet confirmingWallet = getRandomWallet();
        Boolean status = false;
        
        try {
            for (ArrayList<TransactionInfo> transactionInfo : transactionInfoLists) {
                sender = transactionInfo.get(0).getSender();
                block = transactionInfo.get(0).getTargetBlock();
                Transaction newTransaction = sender.createTransaction(transactionInfo, confirmingWallet);
                
                status = block.addTransaction(newTransaction);
            }
            
            if (status) {
                confirmingWallet.confirmBlock(block);
            }
        } catch (CannotCreateTransactionException ex) {
            Logger.getLogger(TransactionGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConfirmBlockException ex) {
            Logger.getLogger(TransactionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InsufficientFundsException ex) {
            Utils.log("InsufficientFundsException");
        }
    }

    @Override
    public void run() {
        try {
            long threadId = Thread.currentThread().getId();
            
            while (true) {
                handleTransaction((ArrayList<ArrayList<TransactionInfo>>) transfers.take());

                if (!wallets.isEmpty()) {

                    getBalances();
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(TransactionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
