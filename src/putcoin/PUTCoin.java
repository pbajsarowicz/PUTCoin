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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import putcoin.exceptions.CannotCreateTransactionException;
import putcoin.exceptions.ConfirmBlockException;
import putcoin.exceptions.InsufficientFundsException;
import sun.nio.cs.ext.Johab;


/**
 *
 * @author piotrbajsarowicz
 */
public class PUTCoin {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {            
/////////// Initialize blockchain
            Blockchain blockchain = Blockchain.getInstance();
            
/////////// Create first wallets
            Wallet walletJohn = new Wallet("John");
            Wallet walletGeorge = new Wallet("George");
            Wallet walletEve = new Wallet("Eve");

/////////// Genesis block
            // Take care of genesis block
            Genesis genesis = new Genesis();
            // Create a genesis block
            Block genesisBlock = genesis.createGenesisBlock();
            // Send first PUTCoins to John
            Transaction genesisTransaction = genesis.createGenesisTransaction(walletJohn, 20, genesisBlock);
            genesisBlock.addTransaction(genesisTransaction);
            // Confirm the genesis block 
            blockchain.addBlock(genesisBlock);
            
            System.out.println("------------------------------------ genesisBlock");
            walletJohn.getBalance();
            walletGeorge.getBalance();
            walletEve.getBalance();
            System.out.println("-------------------------------------------------");
            
/////////// New block and first transactions between folks
            // New block - many transactions within a single block
            Block newBlock = new Block(genesisBlock, "NEW");
            
            // [PASS] Correct transaction with one output - it's supposed to pass
            ArrayList<TransactionInfo> transactionInfo = new ArrayList<TransactionInfo>(
                Arrays.asList(
                    new TransactionInfo(walletGeorge, 4)
                )
            );
            Transaction newTransaction = walletJohn.createTransaction(transactionInfo, newBlock);
            newBlock.addTransaction(newTransaction);

            // [PASS] Correct transaction with many output
            ArrayList<TransactionInfo> transactionInfo2 = new ArrayList<TransactionInfo>(
                Arrays.asList(
                    new TransactionInfo(walletGeorge, 1),
                    new TransactionInfo(walletGeorge, 10)
                )
            );
            Transaction newTransaction2 = walletJohn.createTransaction(transactionInfo2, newBlock);
            newBlock.addTransaction(newTransaction2);
            
            // [PASS] George returns 1 PTC to John in a single transaction
            ArrayList<TransactionInfo> transactionInfo3 = new ArrayList<TransactionInfo>(
                Arrays.asList(
                    new TransactionInfo(walletJohn, 1)
                )
            );
            Transaction newTransaction3 = walletGeorge.createTransaction(transactionInfo3, newBlock);
            newBlock.addTransaction(newTransaction3);
            
            // [PASS] ... and 5 PTC extra in two transactions
            ArrayList<TransactionInfo> transactionInfo4 = new ArrayList<TransactionInfo>(
                Arrays.asList(
                    new TransactionInfo(walletJohn, 3),
                    new TransactionInfo(walletJohn, 2)
                )
            );
            Transaction newTransaction4 = walletGeorge.createTransaction(transactionInfo4, newBlock);
            newBlock.addTransaction(newTransaction4);
            
            // Finally confirm a block
            walletEve.confirmBlock(newBlock);
            
            System.out.println("---------------------------------------- newBlock");
            walletJohn.getBalance();
            walletGeorge.getBalance();
            walletEve.getBalance();
            System.out.println("-------------------------------------------------");

////////////
            Block newBlock0 = new Block(newBlock, "NEW0");
            
            ArrayList<TransactionInfo> transactionInfo0 = new ArrayList<TransactionInfo>(
                Arrays.asList(
                    new TransactionInfo(walletGeorge, 50)
                )
            );
            Transaction newTransaction0 = walletEve.createTransaction(transactionInfo0, newBlock);
            newBlock0.addTransaction(newTransaction0);
            walletGeorge.confirmBlock(newBlock0);
            
            System.out.println("---------------------------------------- newBlock");
            walletJohn.getBalance();
            walletGeorge.getBalance();
            walletEve.getBalance();
            System.out.println("-------------------------------------------------");
             
/////////// Check unsuccessful cases for transactions
/** This code raises exceptions (expected). Uncomment to check exception path cases.
            Block anotherBlock = new Block(newBlock, "ANOTHER");
            // [FAIL] Negative amount - should fail
            ArrayList<TransactionInfo> transactionInfoF1 = new ArrayList<TransactionInfo>(
                Arrays.asList(
                    new TransactionInfo(walletGeorge, -1)
                )
            );
            Transaction newTransactionF1 = walletJohn.createTransaction(transactionInfoF1, anotherBlock);
            anotherBlock.addTransaction(newTransactionF1);
            
            // [FAIL] Amount greater than a balance
            ArrayList<TransactionInfo> transactionInfoF2 = new ArrayList<TransactionInfo>(
                Arrays.asList(
                    new TransactionInfo(walletGeorge, 27)
                )
            );
            Transaction newTransactionF2 = walletJohn.createTransaction(transactionInfoF2, anotherBlock);
            anotherBlock.addTransaction(newTransactionF2);
            
            // [FAIL] Eve does not have PTC yet (block not confirmed (not in the blockchain))
            ArrayList<TransactionInfo> transactionInfoF3 = new ArrayList<TransactionInfo>(
                Arrays.asList(
                    new TransactionInfo(walletGeorge, 1)
                )
            );
            Transaction newTransactionF3 = walletEve.createTransaction(transactionInfoF3, anotherBlock);
            anotherBlock.addTransaction(newTransactionF3);
            
            // Finally confirm an another block
            blockchain.confirmBlock(anotherBlock);
*/

/////////// An attempt to double spend transactions (cofirm an already confirmed block)
/** This code raises an exception (expected). Uncomment to see the actual behaviour
            blockchain.confirmBlock(newBlock);
*/
        } catch (CannotCreateTransactionException ex) {
            Logger.getLogger(PUTCoin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PUTCoin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConfirmBlockException ex) {
            Logger.getLogger(PUTCoin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
