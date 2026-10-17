package exceptions;

/**
 * Ngoại lệ cơ sở cho mọi lỗi nghiệp vụ mượn/trả thiết bị.
 */
public class BorrowingException extends Exception {
    public BorrowingException(String message) {
        super(message);
    }

    public BorrowingException(String message, Throwable cause) {
        super(message, cause);
    }
}
