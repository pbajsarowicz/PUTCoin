/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 *
 * @author piotrbajsarowicz
 */

public class Transaction {
    private ArrayList input = new ArrayList();
    private ArrayList output = new ArrayList();
    private int amount;
    private String hash;
    private String signature;

    public Transaction(ArrayList input, ArrayList output, int amount, String hash, String signature) {
        this.input = input;
        this.output = output;
        this.amount = amount;
        this.hash = hash;
        this.signature = signature;
    }
    
    /**
     * @return the input
     */
    public ArrayList getInput() {
        return input;
    }

    /**
     * @return the output
     */
    public ArrayList getOutput() {
        return output;
    }

    /**
     * @param input the input to set
     */
    public void setInput(ArrayList input) {
        this.input = input;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(ArrayList output) {
        this.output = output;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
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
}
