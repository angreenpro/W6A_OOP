package models;

/**
 * Thiết bị phục vụ giảng dạy, có thể được mượn qua phiếu mượn.
 */
public abstract class TeachingDevice extends Device {

    protected TeachingDevice(String deviceId, String deviceName, int yearInUse, double purchasePrice, int maxBorrowDays) {
        super(deviceId, deviceName, yearInUse, purchasePrice, maxBorrowDays);
    }

    /** Phí phạt cho mỗi ngày trả muộn – mỗi loại thiết bị tự định nghĩa. */
    public abstract double getLateFeePerDay();
}
