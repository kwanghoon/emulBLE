package emul.bluetooth.model;

/**
 * Created by khChoi on 2017-08-16.
 */

public class BLEStateException extends RuntimeException {
    private String exnMsg;
    public BLEStateException(String exnMsg) {
        this.exnMsg = exnMsg;
    }

    public String exnMsg() {
        return exnMsg;
    }
}
