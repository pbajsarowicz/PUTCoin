/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author piotrbajsarowicz
 */
public class Utils {
    
    public static void log(String message) {
        if (PUTCoin.LOG_TO_FILE) {
            try (
                Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream("ptc.log", true), "utf-8")
                )
            ) {
               writer.write(message + '\n');
            } catch (IOException ex) {
                Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
