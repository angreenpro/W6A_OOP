package exceptions;

/**
 * Biến thể tên chuẩn tiếng Anh (BorrowLimitExceededException).
 * Kế thừa từ BorrowLimitExceedException để tương thích cả 2 cách đặt tên.
 */
public class BorrowLimitExceededException extends BorrowLimitExceedException {
    public BorrowLimitExceededException(String borrowerId, int maximumDevices) {
        super(borrowerId, maximumDevices);
    }
}
