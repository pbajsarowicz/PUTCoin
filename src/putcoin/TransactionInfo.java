/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

/**
 *
 * @author piotrbajsarowicz
 */
public class TransactionInfo {
    private Wallet sender;
    private Wallet receiver;
    private int amount;
    private Block targetBlock;

    public TransactionInfo(Wallet sender, Wallet receiver, int amount, Block targetBlock) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.targetBlock = targetBlock;
    }

    /**
     * @return the sender
     */
    public Wallet getSender() {
        return sender;
    }
    
    /**
     * @return the receiver
     */
    public Wallet getReceiver() {
        return receiver;
    }

    /**
     *
     * @param receiver
     */
    public void setReceiver(Wallet receiver) {
        this.receiver = receiver;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }
    
    /**
     * @return the targetBlock
     */
    public Block getTargetBlock() {
        return targetBlock;
    }
}
