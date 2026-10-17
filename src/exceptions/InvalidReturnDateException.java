package exceptions;

import java.time.LocalDate;

/**
 * Ngoại lệ bổ sung: phát sinh khi ngày trả trước ngày mượn.
 * Lý do bổ sung: đề yêu cầu xử lý "ngày trả không hợp lệ" nhưng chưa có ngoại lệ riêng,
 * tách riêng giúp Main bắt và thông báo lỗi chính xác.
 */
public class InvalidReturnDateException extends BorrowingException {
    private final String deviceId;
    private final LocalDate borrowedDate;
    private final LocalDate returnedDate;

    public InvalidReturnDateException(String deviceId, LocalDate borrowedDate, LocalDate returnedDate) {
        super("Ngày trả " + returnedDate + " của thiết bị " + deviceId
                + " không hợp lệ (trước ngày mượn " + borrowedDate + ").");
        this.deviceId = deviceId;
        this.borrowedDate = borrowedDate;
        this.returnedDate = returnedDate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }
}
