/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author piotrbajsarowicz
 */
public class Genesis {
    private Transaction genesisTransaction;
            
//    public String generateHash(String message) throws NoSuchAlgorithmException {
//        MessageDigest md;
//        byte[] digest = null;
//        
//        md = MessageDigest.getInstance("SHA-256");
//        digest = md.digest();
//
//        StringBuffer hexString = new StringBuffer();
//        for (int i = 0; i < digest.length; i++) {
//            hexString.append(Integer.toHexString(0xFF & digest[i]));
//        }
//        
//        return hexString.toString();
//    }
    
    public void createGenesisTransaction(Wallet receiver, int amount) throws NoSuchAlgorithmException {        
        String signature = "0";
        
        System.out.println("Genesis Transaction ==[" + amount + "PUTCoins]==> " + receiver.getPubKey().hashCode());
        
        this.genesisTransaction = new Transaction(null, receiver, amount, signature);
    }
    
    public Block createGenesisBlock() throws NoSuchAlgorithmException {
        try {
            Block genesisBlock = new Block(null, "GENESIS");
            genesisBlock.addTransaction(this.genesisTransaction);

            return genesisBlock;
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Genesis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Genesis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(Genesis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
