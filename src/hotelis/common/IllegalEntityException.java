package hotelis.common;

/**
 * 
 * @author Karel Vaculik
 */
public class IllegalEntityException extends RuntimeException {

    public IllegalEntityException() {
    }

    public IllegalEntityException(String msg) {
        super(msg);
    }

    public IllegalEntityException(String message, Throwable cause) {
        super(message, cause);
    }
        
}
