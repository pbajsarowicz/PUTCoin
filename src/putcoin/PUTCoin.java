/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
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
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, SignatureException, IOException {
        // Initialize blockchain
        Blockchain blockchain = Blockchain.getInstance();
        
        // Create first wallets
        Wallet walletJohn = new Wallet("John");
        Wallet walletGeorge = new Wallet("George");
        
        // Take care of genesis block
        Genesis genesis = new Genesis();
        // Send first PUTCoins to John
        genesis.createGenesisTransaction(walletJohn, 20); 
        
        Block genesisBlock = genesis.createGenesisBlock();
        blockchain.addBlock(genesisBlock);
        
        System.out.println("------------------------------------ genesisBlock");
        walletJohn.getBalance();
        walletGeorge.getBalance();
        System.out.println("-------------------------------------------------");

        
        // New block
        Block newBlock = new Block(genesisBlock, "NEW");
        // [PASS] Correct transaction - it's supposed to pass
        newBlock.addTransaction(walletJohn.createTransaction(walletGeorge, 4));
        // [FAIL] Negative amount - should fail
        newBlock.addTransaction(walletJohn.createTransaction(walletGeorge, -1));
        // [FAIL] Amount greater than a balance
        newBlock.addTransaction(walletJohn.createTransaction(walletGeorge, 27)); 
        // [FAIL] George does not have PTC yet (block not confirmed (not in the blockchain))
        newBlock.addTransaction(walletGeorge.createTransaction(walletJohn, 1)); 
        blockchain.addBlock(newBlock);
        System.out.println("---------------------------------------- newBlock");
        walletJohn.getBalance();
        walletGeorge.getBalance();
        System.out.println("-------------------------------------------------");
        
        
        // Another block
        Block anotherBlock = new Block(genesisBlock, "NEW");
        // Trying again with a last transaction (not confirmed with a first attempt)
        anotherBlock.addTransaction(walletGeorge.createTransaction(walletJohn, 1)); // George does not have PTC yet (block not confirmed)
        blockchain.addBlock(anotherBlock);
        System.out.println("------------------------------------ anotherBlock");
        walletJohn.getBalance();
        walletGeorge.getBalance();
        System.out.println("-------------------------------------------------");
    }
    
}
