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
public class CannotCreateTransactionException extends Exception {

    @Override
    public String getMessage() {
        return "Encountered an ussue while creating a transaction";
    }
    
}
