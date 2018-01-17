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
    private Wallet wallet;
    private int amount;

    public TransactionInfo(Wallet wallet, int amount) {
        this.wallet = wallet;
        this.amount = amount;
    }

    /**
     * @return the wallet
     */
    public Wallet getWallet() {
        return wallet;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }    
}
