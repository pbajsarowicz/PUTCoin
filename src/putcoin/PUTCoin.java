/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;

/**
 *
 * @author piotrbajsarowicz
 */
public class PUTCoin {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        Wallet walletJohn = new Wallet();
        Wallet walletGeorge = new Wallet();
        
        // Genesis block
        ArrayList<Transaction> genesisTransactions = new ArrayList<Transaction>();
        // John sends PUTCoins to himself
        Transaction genesisTransaction = walletJohn.createTransaction(walletJohn.getPubKey(), 20); 
        genesisTransactions.add(genesisTransaction);
        Block genesisBlock = new Block(null, genesisTransactions);
        walletJohn.appendBlock(genesisBlock);
        
        ArrayList<Transaction> newTransactions = new ArrayList<Transaction>();
        // John sends PUTCoins to George
        Transaction newTransaction = walletJohn.createTransaction(walletGeorge.getPubKey(), 5);
        newTransactions.add(newTransaction);
        Block block2 = new Block(genesisBlock.getBlockHash(), newTransactions);
        walletJohn.appendBlock(block2);
        walletGeorge.appendBlock(block2);
        
        
//        System.out.println("walletJohn " + walletJohn.getBalance(block2.getBlockHash()));
//        System.out.println("walletGeorge " + walletGeorge.getBalance(block2.getBlockHash()));
//        
        System.out.println("walletJohn " + walletJohn.getBalance());
        System.out.println("walletGeorge " + walletGeorge.getBalance());
        
//        String[] block2Transactions = {"Third"};
//        Block block2 = new Block(genesisBlock.getBlockHash(), block2Transactions);
//
//        String[] block3Transactions = {"Fourth, Fifth"};
//        Block block3 = new Block(block2.getBlockHash(), block3Transactions);
//
//        System.out.println("Genesis block:");
//        System.out.println(genesisBlock.getBlockHash());
//
//        System.out.println("Block 2:");
//        System.out.println(block2.getBlockHash());
//
//        System.out.println("Block 3:");
//        System.out.println(block3.getBlockHash());
        
        
//        String[] genesisTransactions = {"First", "Second"};
//        Block genesisBlock = new Block(0, genesisTransactions);
//
//        String[] block2Transactions = {"Third"};
//        Block block2 = new Block(genesisBlock.getBlockHash(), block2Transactions);
//
//        String[] block3Transactions = {"Fourth, Fifth"};
//        Block block3 = new Block(block2.getBlockHash(), block3Transactions);
//
//        System.out.println("Genesis block:");
//        System.out.println(genesisBlock.getBlockHash());
//
//        System.out.println("Block 2:");
//        System.out.println(block2.getBlockHash());
//
//        System.out.println("Block 3:");
//        System.out.println(block3.getBlockHash());
    }
    
}
