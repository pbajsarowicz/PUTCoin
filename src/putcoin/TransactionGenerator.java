/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import putcoin.exceptions.ConfirmBlockException;

/**
 *
 * @author piotrbajsarowicz
 */
public class TransactionGenerator implements Runnable {
    private ArrayList<Wallet> wallets = new ArrayList<Wallet>();
    Blockchain blockchain;
    Block genesisBlock;
    int numerOfWallets;
    private Random randomGenerator = new Random();
    
    private int MAX;
    private int MIN = 1;
    private BlockingQueue transfers = null;
    private int last = 0;
    private static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    
    public int getLast() {
        last += 1;
        return last;
    }

    public TransactionGenerator(int numerOfWallets, BlockingQueue transfers) {
        this.numerOfWallets = numerOfWallets;
        this.blockchain = Blockchain.getInstance();
        this.transfers = transfers;
        this.MAX = numerOfWallets - 1;
    }

    public ArrayList<Wallet> getWallets() {
        return wallets;
    }

    public Block getGenesisBlock() {
        return genesisBlock;
    }
    
    public void initializeWallets() {
        for (int i = 0; i < numerOfWallets; i++) {
            try {
                wallets.add(new Wallet("Wallet" + i));
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(TransactionGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public Wallet getRandomWallet() {
        return wallets.get(randomGenerator.nextInt(wallets.size()));
    }
    
    public Wallet getRandomWallet(Wallet exclude) {
        Wallet choice = wallets.get(randomGenerator.nextInt(wallets.size()));
        
        while (choice == exclude) {
            choice = wallets.get(randomGenerator.nextInt(wallets.size()));
        }
        
        return choice;
    }
    
    
    public void initializeGenesisBlock() {
        try {
            Genesis genesis = new Genesis();
            // Create a genesis block
            genesisBlock = genesis.createGenesisBlock();
            // Send first 50 PTC to random wallet
            Transaction genesisTransaction = genesis.createGenesisTransaction(getRandomWallet());
            genesisBlock.addTransaction(genesisTransaction);
            // Confirm the genesis block
            blockchain.addBlock(genesisBlock);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TransactionGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConfirmBlockException ex) {
            Logger.getLogger(TransactionGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getBalances() {
        Utils.log("---------------------------------------- balances");
        for (Wallet wallet : wallets) {
            Utils.log("| [" + wallet.getDisplayName() + "] " + wallet.getBalance());
        }
        Utils.log("-------------------------------------------------");
    }
    
    public ArrayList<TransactionInfo> createTransactionInfoList(Block block, int number) {
        Wallet sender = getRandomWallet();
        ArrayList<TransactionInfo> transactionInfoList = new ArrayList<TransactionInfo>();
        int amount;
        
        for (int i = 0; i < number; i++) {
            amount = randomGenerator.nextInt(100);
            TransactionInfo transactionInfo = new TransactionInfo(
                sender, getRandomWallet(sender), amount, block
            );
            transactionInfoList.add(transactionInfo);
        }
        
        return transactionInfoList;
    }
    
    public String getRandomName() {
        StringBuilder name = new StringBuilder();

        while (name.length() < 5) {
            int index = (int) (randomGenerator.nextFloat() * CHARS.length());
            name.append(CHARS.charAt(index));
        }
        
        return name.toString() + "_" + getLast();

    }
    
    @Override
    public void run() {
        try {
            int numberOfTransactions;
            int numberOfOutputs;
            long threadId = Thread.currentThread().getId();
            Block newBlock;
            ArrayList<ArrayList<TransactionInfo>> transactionInfoLists = null;
            
            initializeWallets();
            initializeGenesisBlock();
            
            getBalances();
            
            while(true) {
                newBlock = new Block(blockchain.getLastBlock(), getRandomName());
                numberOfTransactions = randomGenerator.nextInt(MAX + 1 - MIN) + MIN;
                // Required to have a possibility to track transactions in a queue to be confirmed
                blockchain.getUnconfirmedBlocks().add(newBlock);
                transactionInfoLists = new ArrayList<ArrayList<TransactionInfo>>();
                
                // Handles creating multiple transactions within a block (list of (*)lists)
                // with multiple outputs ((*)list of transactionInfo objects)
                for (int i = 0; i < numberOfTransactions; i++) {
                    numberOfOutputs = randomGenerator.nextInt(MAX + 1 - MIN) + MIN;
                    transactionInfoLists.add(
                        createTransactionInfoList(newBlock, numberOfOutputs)
                    );
                }
                
                transfers.put(transactionInfoLists);
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(TransactionGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
