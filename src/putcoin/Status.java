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
public class Status {
    private String reason;
    private boolean ok;

    public Status(boolean ok, String reason) {
        this.ok = ok;
        this.reason = reason;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return the ok
     */
    public boolean isOk() {
        return ok;
    }
}
