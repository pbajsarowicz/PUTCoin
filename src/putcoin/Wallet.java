/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
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
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

/**
 *
 * @author piotrbajsarowicz
 */
public class Wallet {
    private String displayName;
    private PublicKey pubKey;
    private PrivateKey privKey;
    private int RSA_KEY_SIZE = 2048;    
    private KeyPairGenerator keyPairGen = null;

    public Wallet(String displayName) throws NoSuchAlgorithmException {
        this.keyPairGen = KeyPairGenerator.getInstance("RSA");
        this.keyPairGen.initialize(RSA_KEY_SIZE);
        KeyPair keyPair = keyPairGen.genKeyPair();
        this.pubKey = keyPair.getPublic();
        this.privKey = keyPair.getPrivate();
        this.displayName = displayName;
    }

    /**
     * @return the pubKey
     */
    public PublicKey getPubKey() {
        return pubKey;
    }
    
    /**
     * @return the displatName
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Generates a hash based on a given message for purposes of blocks
     * and transactions.
     * 
     * @param message
     * @return a transaction/block hash
     */
    public String generateHash(String message) {
        try {
            MessageDigest md;
            byte[] digest = null;
            
            md = MessageDigest.getInstance("SHA-256");
            md.update(pubKey.getEncoded());
            digest = md.digest(message.getBytes(StandardCharsets.UTF_8));
            
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    /**
     * Signs a given message.
     * 
     * @param message
     * @return a signed message.
     */
    public String sign(String message) {
        try {
            byte[] messageData = message.getBytes("UTF8");
            Signature sig = Signature.getInstance("SHA1WithRSA");
            sig.initSign(privKey);
            sig.update(messageData);
            byte[] signatureBytes = sig.sign();
            
            return new BASE64Encoder().encode(signatureBytes);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    /**
     * Verifies a signature. Used while confirming transactions and blocks.
     * 
     * @param signature
     * @param message
     * @param pubKey
     * @return whether a message is signed with a signature
     */
    public boolean verify(String signature, String message, PublicKey pubKey) {
        try {
            Signature sig = Signature.getInstance("SHA1WithRSA");
            sig.initVerify(pubKey);
            byte[] messageData = message.getBytes("UTF8");
            sig.update(messageData);
            byte[] signatureBytes = new BASE64Decoder().decodeBuffer(signature);
            
            return sig.verify(signatureBytes);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    /**
     * Returns actual Unspent Transaction Outputs (UTXO)
     * (actual - in the scope of the blockchain).
     * 
     * @return UTXO
     */
    public ArrayList<Transaction.Output> getUTXOInBlockchain() {
        Blockchain blockchain = Blockchain.getInstance();
        ArrayList<Transaction.Output> UTXO = new ArrayList<Transaction.Output>();
        
        for (Block block : blockchain.getBlocks()) {
            for (Transaction transaction : block.getTransactions()) {
                for (Transaction.Output output : transaction.getOutputs()) {
                    if (
                        output.getReceiver().getPubKey() == getPubKey() &&
                        !output.isSpent()
                    ) {
                        UTXO.add(output);
                    }
                }
            }
        }
        
        return UTXO;
    }
    
    /**
     * Returns Unspent Transaction Outputs (UTXO) in the scope of a block.
     * It enables generating transactions to be confirmed that depend on each
     * other, e.g.: two transactions in one package (block) to be confirmed:
     * A ==[1 PTC]==> X
     * X ==[1 PTC]==> B
     * 
     * @return UTXO
     */
    
    public ArrayList<Transaction.Output> getUTXOInBlock(Block block) {
        ArrayList<Transaction.Output> UTXO = getUTXOInBlockchain();
        

        for (Transaction transaction : block.getTransactions()) {
            // Add new UTXO (new outputs coming from new transactions)
            for (Transaction.Output output : transaction.getOutputs()) {
                if (
                    output.getReceiver().getPubKey() == getPubKey() 
                ) {
                    UTXO.add(output);  
                }
            }
            
            // Remove outputs associated with inputs for new transactions
            for (Transaction.Input input : transaction.getInputs()) {
                if (
                    input.getOriginOutput().getReceiver().getPubKey() == getPubKey()
                ) {
                    UTXO.remove(input.getOriginOutput());
                }
            }
        }

        return UTXO;
    }
    
    /**
     * Extra method that enables printing a balance to the console.
     * Should be consider default.
     * 
     * @return a balance
     */
    public int getBalance() {
        return getBalance(true);
    }
    
    /**
     * Returns a balance. Allows printing a result to the console.
     * 
     * @param prompt
     * @return a balance
     */
    public int getBalance(boolean prompt) {
        ArrayList<Transaction.Output> transactionsOutputsToSpend = getUTXOInBlockchain();
        int balance = 0;
        
        for (Transaction.Output output : transactionsOutputsToSpend) {
            balance += output.getAmount();
        }
        
        return balance;
    }
    
    public int getBalanceForBlock(Block block) {
        ArrayList<Transaction.Output> transactionsOutputsToSpend = getUTXOInBlock(block);
        
        int balance = 0;
        
        for (Transaction.Output output : transactionsOutputsToSpend) {
            balance += output.getAmount();
        }
        
        return balance;
    }
    
    public String getMessage(PublicKey receiverPubKey, int amount) {
        return getPubKey() + ":" +
               receiverPubKey + ":" +
               amount;
    }
    
    
    
    /**
     * Creates a transaction object.
     * Prints extra info about transaction's members.
     * 
     * @param transactionInfo
     * @return
     * @throws CannotCreateTransactionException
     */
    public Transaction createTransaction(ArrayList<TransactionInfo> transactionInfo) throws CannotCreateTransactionException, InsufficientFundsException {
        for (TransactionInfo transactionInfoItem : transactionInfo) {
            Utils.log(getDisplayName() + " ==[" + transactionInfoItem.getAmount() + " PTC]==> " + transactionInfoItem.getReceiver().getDisplayName());
        }
    
        try {
            return new Transaction(transactionInfo);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new CannotCreateTransactionException();
    }
    
    private void reward(Block block) {
        try {
            ArrayList<TransactionInfo> rewardTransactionInfo = new ArrayList<TransactionInfo>(
                Arrays.asList(
                    new TransactionInfo(null, this, Blockchain.REWARD, block)
                )
            );
            Transaction rewardTransaction = new Transaction(rewardTransactionInfo, true);
            block.addTransaction(rewardTransaction);
            Utils.log("REWARD ==[" + Blockchain.REWARD + " PTC]==> " + getDisplayName() + "(for BLOCK CONFIRMATION)");
        
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PUTCoin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InsufficientFundsException ex) {
            Logger.getLogger(PUTCoin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void confirmBlock(Block block) throws ConfirmBlockException {
        Blockchain blockchain = Blockchain.getInstance();
        
        reward(block);
        
        blockchain.addBlock(block);
    }
}
