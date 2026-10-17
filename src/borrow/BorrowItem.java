package borrow;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import exceptions.BorrowingException;
import exceptions.DeviceAlreadyReturnedException;
import exceptions.InvalidReturnDateException;
import models.TeachingDevice;

/**
 * Mục thiết bị trong phiếu mượn (BorrowItem).
 * Được sở hữu bởi BorrowingSlip (Composition), tham chiếu đến TeachingDevice (Aggregation).
 */
public class BorrowItem {
    private final TeachingDevice device;
    private final LocalDate borrowedDate;
    private final LocalDate dueDate;
    private LocalDate returnedDate;
    private boolean isReturned;

    public BorrowItem(TeachingDevice device, LocalDate borrowedDate, LocalDate dueDate) {
        if (device == null) {
            throw new IllegalArgumentException("Thiết bị không được null.");
        }
        if (borrowedDate == null) {
            throw new IllegalArgumentException("Ngày mượn không được null.");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Hạn trả không được null.");
        }
        if (dueDate.isBefore(borrowedDate)) {
            throw new IllegalArgumentException("Hạn trả không được trước ngày mượn.");
        }

        this.device = device;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = null;
        this.isReturned = false;
    }

    public TeachingDevice getDevice() {
        return device;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    /**
     * Trả thiết bị và tính phí trễ hạn (nếu có).
     */
    public double returnItem(LocalDate returnDate) throws BorrowingException {
        if (this.isReturned) {
            throw new DeviceAlreadyReturnedException(device.getDeviceId(), this.returnedDate);
        }
        if (returnDate == null || returnDate.isBefore(this.borrowedDate)) {
            throw new InvalidReturnDateException(device.getDeviceId(), this.borrowedDate, returnDate);
        }

        this.returnedDate = returnDate;
        this.isReturned = true;
        this.device.markAsAvailable();

        return calculateLateFee();
    }

    /**
     * Tính phí trễ hạn dựa trên số ngày trễ thực tế hoặc hiện tại.
     */
    public double calculateLateFee() {
        LocalDate actualReturn = isReturned ? returnedDate : LocalDate.now();
        if (actualReturn.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, actualReturn);
            return daysLate * device.getLateFeePerDay();
        }
        return 0.0;
    }

    /**
     * Kiểm tra mục mượn có bị trễ hạn hay không (so với ngày mốc).
     */
    public boolean isOverdue(LocalDate asOfDate) {
        if (isReturned) {
            return returnedDate.isAfter(dueDate);
        }
        return asOfDate.isAfter(dueDate);
    }

    public long getDaysOverdue(LocalDate asOfDate) {
        LocalDate checkDate = isReturned ? returnedDate : asOfDate;
        if (checkDate.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, checkDate);
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Mục [%s - %s] | Mượn: %s | Hạn: %s | Trạng thái: %s",
                device.getDeviceId(), device.getDeviceName(), borrowedDate, dueDate,
                isReturned ? ("Đã trả ngày " + returnedDate) : "Đang mượn"));
        if (calculateLateFee() > 0) {
            sb.append(String.format(" | Trễ: %d ngày (Phạt: %,.0f đ)", 
                    getDaysOverdue(isReturned ? returnedDate : LocalDate.now()), calculateLateFee()));
        }
        return sb.toString();
    }
}
