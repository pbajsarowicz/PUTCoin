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
public class ConfirmBlockException extends Exception {

    @Override
    public String getMessage() {
        return (
            "Cannot confirm a block. Cannot verify signature or the block " +
            "already confirmed"
        );
    }
    
}
