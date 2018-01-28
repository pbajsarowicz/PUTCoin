/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 *
 * @author piotrbajsarowicz
 */
public class Visualizer extends Thread {
    ArrayList<Wallet> wallets;

    public Visualizer(TransactionHandler transactionHandler) {
        wallets = transactionHandler.getWallets();
    }
    
    public static String fixedLengthString(String string, int length) {
        int stringLength = string.length();
        
        if (stringLength % 2 != 0) {
            length -= 1;
        }
        
        int length_ = (length - stringLength)/2;
//        String padding = String.format("%0" + length_ + "d", 0).replace("0", " ");
        String padding = String.join("", Collections.nCopies(length_, " "));

        return padding + string + padding;
    }
    
    public void blockchain() {
        String msg = "";
        Blockchain blockchain = Blockchain.getInstance();
        for (Block block : blockchain.getBlocks()) {
            System.out.println(" " + String.join("", Collections.nCopies(9, "~")));
            System.out.println("|" + fixedLengthString(block.getDisplayName(), 10) + "|" + "  " + block.getBlockHash());
            System.out.println(" " + String.join("", Collections.nCopies(9, "~")));
            System.out.println(fixedLengthString("||", 12));
            
            for (Transaction transaction : block.getTransactions()) {
                for (Transaction.Output output : transaction.getOutputs()) {
                    msg = fixedLengthString("||", 12) + " ";
                    
                    if (output.getSender() == null) {
                        if (output.isReward()) {
                            msg += "[" + output.getTransactionHash() + "] REWARD ==[" + Blockchain.REWARD + " PTC]==> " + output.getReceiver().getDisplayName();
                        } else {
                            msg += "[" + output.getTransactionHash() + "] Genesis Transaction ==[" + Blockchain.REWARD + "PUTCoins]==> " + output.getReceiver().getDisplayName();
                        }
                    }  else if (output.isCommission()) {
                        msg += "[" + output.getTransactionHash() + "][COMMISSION] " + output.getSender().getDisplayName() + " ==[" + output.getAmount() + " PTC]==> " + output.getReceiver().getDisplayName();
                    } else {
                        msg += "[" + output.getTransactionHash() + "] " + output.getSender().getDisplayName() + " ==[" + output.getAmount() + " PTC]==> " + output.getReceiver().getDisplayName();          
                    }
                    
                    System.out.println(msg);
                }
            }
            System.out.println(fixedLengthString("||", 12));
            System.out.println(fixedLengthString("\\/", 12));
            
        }
    
    }
    
    public void balances() {
        System.out.println("Stany kont ______________________________________");
        for (Wallet wallet : wallets) {
            System.out.println("| [" + wallet.getDisplayName() + "] " + wallet.getBalance());
        }
        System.out.println("_________________________________________________");
    }
    
    public void UTXO() {
        Blockchain blockchain = Blockchain.getInstance();
        String msg = "";
        
        System.out.println("UTXO ____________________________________________");
        for (Wallet wallet : wallets) {
            System.out.println(wallet.getDisplayName() + ":");
            for (Transaction.Output output : wallet.getUTXOInBlockchain()) {
                msg = fixedLengthString("", 12) + " ";

                if (output.getSender() == null) {
                    if (output.isReward()) {
                        msg += "[" + output.getTransactionHash() + "] REWARD ==[" + Blockchain.REWARD + " PTC]==> " + output.getReceiver().getDisplayName();
                    } else {
                        msg += "[" + output.getTransactionHash() + "] Genesis Transaction ==[" + Blockchain.REWARD + "PUTCoins]==> " + output.getReceiver().getDisplayName();
                    }
                }  else if (output.isCommission()) {
                    msg += "[" + output.getTransactionHash() + "][COMMISSION] " + output.getSender().getDisplayName() + " ==[" + output.getAmount() + " PTC]==> " + output.getReceiver().getDisplayName();
                } else {
                    msg += "[" + output.getTransactionHash() + "] " + output.getSender().getDisplayName() + " ==[" + output.getAmount() + " PTC]==> " + output.getReceiver().getDisplayName();

                }

                System.out.println(msg);
            }
            System.out.println();
        }
        System.out.println("_________________________________________________");
    }
    
    public void transactionsToBeConfirmed() {
        String msg = "";
        Blockchain blockchain = Blockchain.getInstance();
        
        System.out.println("Unconfirmed _____________________________________");
        for (Block block : blockchain.getUnconfirmedBlocks()) {
           ArrayList<Transaction> blockTransactions = block.getTransactions();
           
           if (blockTransactions.isEmpty()) {
               continue;
           }

           System.out.println(" " + String.join("", Collections.nCopies(9, "~")));
           System.out.println("|" + fixedLengthString(block.getDisplayName(), 10) + "|");
           System.out.println(" " + String.join("", Collections.nCopies(9, "~")));

           for (Transaction transaction : block.getTransactions()) {
               for (Transaction.Output output : transaction.getOutputs()) {
                    msg = fixedLengthString("", 12) + " ";

                    if (output.getSender() == null) {
                        if (output.isReward()) {
                           msg += "[" + output.getTransactionHash() + "] REWARD ==[" + Blockchain.REWARD + " PTC]==> " + output.getReceiver().getDisplayName();
                        } else {
                           msg += "[" + output.getTransactionHash() + "] Genesis Transaction ==[" + Blockchain.REWARD + "PUTCoins]==> " + output.getReceiver().getDisplayName();
                        }
                    }  else if (output.isCommission()) {
                        msg += "[" + output.getTransactionHash() + "][COMMISSION] " + output.getSender().getDisplayName() + " ==[" + output.getAmount() + " PTC]==> " + output.getReceiver().getDisplayName();
                    } else {
                        msg += "[" + output.getTransactionHash() + "] " + output.getSender().getDisplayName() + " ==[" + output.getAmount() + " PTC]==> " + output.getReceiver().getDisplayName();
                    }

                    System.out.println(msg);
               }
           }
           System.out.println();

       }
        System.out.println("_________________________________________________");
    }
    
    @Override
    public void run() {
        System.out.println("#################################################");
        System.out.println("Wprowadź numer: ");
        System.out.println("(1) Podejrzyj blok");
        System.out.println("(2) Zlecone transakcje");
        System.out.println("(3) Niewydane transakcje");
        System.out.println("(4) Sprawdz stan portfeli");
        Scanner reader = new Scanner(System.in);
        
        while(reader.hasNext() && !reader.equals("exit")) { 
            int option = reader.nextInt();
            switch (option) {
                case 1: blockchain();
                        break;
                case 2: transactionsToBeConfirmed();
                        break;
                case 3: UTXO();
                        break;
                case 4: balances();
                        break;
                default: break;
            }
            
            System.out.println("#################################################");
            System.out.println("Wprowadź numer: ");
            System.out.println("(1) Podejrzyj blockchain");
            System.out.println("(2) Zlecone transakcje");
            System.out.println("(3) Niewydane transakcje");
            System.out.println("(4) Sprawdz stan portfeli");
        }
    }
}
