/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 *
 * @author piotrbajsarowicz
 */
public class PUTCoin {
    
    public static Boolean LOG_TO_FILE = false;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
            Blockchain blockchain = Blockchain.getInstance();
            BlockingQueue transfers = new ArrayBlockingQueue(128);

            TransactionGenerator transactionGenerator = new TransactionGenerator(3, transfers);
            TransactionHandler transactionHandler = new TransactionHandler(transactionGenerator, transfers);
            Thread thread1 = new Thread(transactionGenerator);
            Thread thread2 = new Thread(transactionHandler);
            Visualizer visualizer = new Visualizer(transactionHandler);
            thread1.start();
            thread2.start();
            visualizer.start();
    }
    
}
