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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import putcoin.exceptions.InsufficientFundsException;
import sun.misc.BASE64Decoder;

/**
 *
 * @author piotrbajsarowicz
 */

public final class Transaction {
    private ArrayList<Input> inputs = new ArrayList<Input>();
    private ArrayList<Output> outputs = new ArrayList<Output>();
    private Wallet sender;
    private Wallet confirmingWallet;
    private ArrayList<TransactionInfo> transactionInfo;
    private int change;
    private String hash;
    private String signature;
    private Boolean reward = false;
    private static int COMMISION_PERCENTAGE = 10;
    
    /**
    * Defines input of a transaction.
    */
    class Input {
        private String originTransactionHash;
        private Output originOutput;
        private String originSignature;

        public Input(Output output) {
            this.originTransactionHash = output.transactionHash;
            this.originSignature = output.signature;
            this.originOutput = output;
        }

        public String getOriginTransactionHash() {
            return originTransactionHash;
        }

        public String getOriginSignature() {
            return originSignature;
        }
        
        public Output getOriginOutput() {
            return originOutput;
        }
    }
    
    /**
    * Defines output of a transaction.
    */
    class Output {
        private String transactionHash;
        private int amount;
        private String signature;
        private Wallet receiver;
        private Boolean isSpent = false;
        // Indicates an output that is a bonus for a wallet confirming a block
        // in the case when total amount of inputs > outputs
        private Boolean isCommission = false;  

        public Output(TransactionInfo transactionInfo) {
            this.transactionHash = hash;
            this.receiver = transactionInfo.getReceiver();
            this.amount = transactionInfo.getAmount();
            
            if (!isGenesisTransaction()) {
                sign();
            }
        }
        
        public Output(Wallet wallet, int amount) {
            this.transactionHash = hash;
            this.signature = signature;
            this.receiver = wallet;
            this.amount = amount;
        }
        
        public Output(TransactionInfo transactionInfo, Boolean isCommission) {
            this(transactionInfo);
            this.isCommission = isCommission;
        }
        
        public String getTransactionHash() {
            return transactionHash;
        }
        
        public int getAmount() {
            return amount;
        }
        
        public Wallet getReceiver() {
            return receiver;
        }
        
        public Boolean isCommission() {
            return isCommission;
        }
        
        
        public Boolean isSpent() {
            return isSpent;
        }
        
        public void spend() {
            this.isSpent = true;
        }
        
        public Wallet getSender() {
            return sender;
        }
        
        public Boolean isReward() {
            return reward;
        }
        
        public String getRawMessage() {
            return sender.getPubKey() + ":" +
                   receiver.getPubKey() + ":" +
                   amount;
        }
        
        public void sign() {
            String transactionRaw = this.getRawMessage();
            this.signature = sender.sign(transactionRaw);
        }
    }

    /**
     * Initializes a transaction object. Sets inputs and outpus.
     * Generates a hash of the transaction.
     * 
     * @param transactionInfo
     * @throws NoSuchAlgorithmException
     * @throws InsufficientFundsException
     */
    public Transaction(ArrayList<TransactionInfo> transactionInfo) throws NoSuchAlgorithmException, InsufficientFundsException {
        this.sender = transactionInfo.get(0).getSender();
        this.transactionInfo = transactionInfo;
        Block targetBlock = transactionInfo.get(0).getTargetBlock();
        
        this.hash = generateHash();
        
        if (sender != null) {
            // Takes a targetBlock to enable generating transactions to be confirmed that depend on
            // each other, e.g.: two transactions in one package (block) to be confirmed:
            // A ==[1 PTC]==> X
            // X ==[1 PTC]==> B
            setInputs(targetBlock);
            setOutputs();
            sign();
        } else {
            setOutputs();
        }
    }
    /**
     * Handles commission transaction for confirming a block.
     */
    public Transaction(ArrayList<TransactionInfo> transactionInfo, Wallet confirmingWallet) throws NoSuchAlgorithmException, InsufficientFundsException {
        this.confirmingWallet = confirmingWallet;
        this.sender = transactionInfo.get(0).getSender();
        this.transactionInfo = transactionInfo;
        Block targetBlock = transactionInfo.get(0).getTargetBlock();
        
        this.hash = generateHash();
        
        if (sender != null) {
            // Takes a targetBlock to enable generating transactions to be confirmed that depend on
            // each other, e.g.: two transactions in one package (block) to be confirmed:
            // A ==[1 PTC]==> X
            // X ==[1 PTC]==> B
            setInputs(targetBlock);
            setOutputs();
            sign();
        } else {
            setOutputs();
        }
    }
    
    /**
     * Handles reward transaction for confirming a block.
     * 
     * @param transactionInfo
     * @param isReward 
     * @throws NoSuchAlgorithmException
     * @throws InsufficientFundsException
     */
    public Transaction(ArrayList<TransactionInfo> transactionInfo, Boolean isReward) throws NoSuchAlgorithmException, InsufficientFundsException {
        this(transactionInfo);
        this.reward = isReward;
    }
    
    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return the sender
     */
    public Wallet getSender() {
        return sender;
    }
    
    /**
     * @return the inputs
     */
    public ArrayList<Input> getInputs() {
        return inputs;
    }

    /**
     * @return the outputs
     */
    public ArrayList<Output> getOutputs() {
        return outputs;
    }
    
    /**
     * @return the reward
     */
    public Boolean isReward() {
        return reward;
    }
    
    /**
     *
     * @return if it's a genesis transaction
     */
    public Boolean isGenesisTransaction() {
        return sender == null;
    }
    
    /**
     * Signs a transaction with a sender's key.
     */
    public void sign() {
        String transactionRaw = getRawMessage();
        this.signature = sender.sign(transactionRaw);
    }
    
    /**
     * Calculates overall amount of a transaction.
     * 
     * @return an expected amount of a transaction
     */
    public int getAmount() {
        int amount = 0;
        
        for (TransactionInfo transactionInfoItem : transactionInfo) {
            amount += transactionInfoItem.getAmount();
        }
        
        return amount;
    }
    
    /**
     * Calculates a total amount (a sum of amounts of origins of inputs (UTXO).
     * Used for calculating a change.
     * 
     * @return a total amount of a transaction on input.
     */
    public int getTotalInputAmount() {
        int totalInputAmount = 0;
        
        for (Input input : inputs) {
            totalInputAmount += input.getOriginOutput().getAmount();
        }
        
        return totalInputAmount;
    }
    
    
    /**
     * Sets inputs of a transaction.
     * Chooses UTXO to spend (creates inputs based on them) and links them
     * with the inputs.
     * Validates if a sender has enough funds to process the transaction.
     * 
     * It operates within a given block, so it includes transactions to be spent
     * while choosing UTXO and validating a transaction's amount.
     *
     * @param targetBlock a block that a transaction is meant to be added to.
     * @throws InsufficientFundsException
     */
    public final void setInputs(Block targetBlock) throws InsufficientFundsException {                
        ArrayList<Output> UTXO = sender.getUTXOInBlock(targetBlock);
        ArrayList<Input> potentialInputs = new ArrayList<Input>();
        int totalInputAmount = 0;
        int transactionAmount = getAmount();
        
        for (Output output : UTXO) {
            totalInputAmount += output.amount;
            potentialInputs.add(new Input(output));
            
            if (totalInputAmount >= transactionAmount) {
                break;
            }
        }
        
        if (!(totalInputAmount >= transactionAmount)) {
            throw new InsufficientFundsException();
        }
        
        this.inputs = potentialInputs;
    }
    
    /**
     * Initializes outputs of a transactions.
     * Sets amounts and receivers of transactions.
     */
    public final void setOutputs() {        
        int totalInputAmount = getTotalInputAmount();
        int transactionAmount = getAmount();
        change = totalInputAmount - transactionAmount;
        
        if (change > 0) {
            // Prepare commission output
            int commission = (int)(change*(COMMISION_PERCENTAGE/100.0f));
            
            if (commission > 0) {
                change -= commission;
                Block targetBlock = transactionInfo.get(0).getTargetBlock();
                TransactionInfo commissionTransactionInfo = new TransactionInfo(
                    sender, confirmingWallet, commission, targetBlock
                );

                outputs.add(new Output(commissionTransactionInfo, true));
            }
        }
        
        for (TransactionInfo transactionInfoItem : transactionInfo) {
            outputs.add(new Output(transactionInfoItem));
        }
        
        // Keep the change
        if (sender != null) {
            outputs.add(new Output(sender, change));
        }
    }
    
    /**
     * Returns a raw message that will be used for generating
     * a transaction's hash.
     * 
     * @return a message to be hashed
     */
    public String getRawMessage() {      
        ArrayList<Output> outputs = getOutputs();
        String message = "";
        
        for (Output output : outputs) {
            message += output.getRawMessage() + ":";
        }
        
        if (isGenesisTransaction()) {
            return "genesis:" + message;
        } else {
            return this.sender.getPubKey() + ":" + message;
        }
    }
    
    /**
     * Generates a hash of a transaction.
     * 
     * @return a block's hash
     * @throws NoSuchAlgorithmException
     */
    public String generateHash() throws NoSuchAlgorithmException {
        String valueToHash = this.getRawMessage() + ":" + this.signature;
        
        MessageDigest md;
        byte[] digest = null;
        
        md = MessageDigest.getInstance("SHA-256");
        
        if (!isGenesisTransaction()) {
            md.update(sender.getPubKey().getEncoded());
        }
        
        digest = md.digest(valueToHash.getBytes(StandardCharsets.UTF_8));

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & digest[i]));
        }
        
        return hexString.toString();
    }
    
    /**
     * 
     * @return if a signature is valid
     */
    public boolean verifySignature() {
        try {
            Signature sig = Signature.getInstance("SHA1WithRSA");
            sig.initVerify(sender.getPubKey());
            
            byte[] messageData = getRawMessage().getBytes("UTF8");
            sig.update(messageData);
            
            return sig.verify(new BASE64Decoder().decodeBuffer(signature));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    /**
     * Verfies if a sender has enough funds to process a transaction.
     * It does it in the scope of a target block.
     * 
     * @return if an amount is correct
     */
    public boolean verifyAmount(Block block) {
        int amount = getAmount();
        
        if (isGenesisTransaction()) {
            return amount > 0;
        } else {
            return (
                amount > 0 &&
                sender.getBalanceForBlock(block) >= amount 
            );
        }
    }
    
    /**
     * 
     * @return status of a verification of transaction
     */
    public Status verify(Block block) {
        boolean ok;
        int amount = getAmount();
        String reason = "Verified transaction for " + amount + " PTC";
        boolean isAmountCorrect = verifyAmount(block);

        if (isGenesisTransaction()) {
            ok = isAmountCorrect;
        } else {
            ok = (
                isAmountCorrect &&
                verifySignature()
            );
        }
        
        if (!ok) {
            if (!isAmountCorrect) {
                reason = "Amount " + amount + " PTC is incorrect (too low/high or an insufficient balance of a sender's wallet).";
            } else {
                reason = "Failed due to signature verification";
            }
        }
        
        return new Status(ok, reason);
    }
    
    /**
     * Protects against double spending.
     */
    public Boolean isSpent() {
        for (Transaction.Input input : getInputs()) {
            Transaction.Output output = input.getOriginOutput();
            
            if (output.isSpent()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Spends origins (outputs) of the transaction's inputs.
     */
    public void spend() {
        for (Transaction.Input input : getInputs()) {
            Transaction.Output output = input.getOriginOutput();
            output.spend();
        }
    }
}
