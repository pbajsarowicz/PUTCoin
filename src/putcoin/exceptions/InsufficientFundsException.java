/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package putcoin.exceptions;

/**
 *
 * @author piotrbajsarowicz
 */
public class InsufficientFundsException extends Exception {

   @Override
    public String getMessage() {
        return "Insufficient funds to process the transaction";
    }
}
