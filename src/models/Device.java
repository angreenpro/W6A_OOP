package models;

import java.time.Year;

import enums.DeviceStatus;

/**
 * Lớp trừu tượng cho mọi thiết bị trong trường.
 */
public abstract class Device {
    private final String deviceId;          // chỉ gán khi khởi tạo
    private String deviceName;
    private int yearInUse;
    private double purchasePrice;
    private int maxBorrowDays;
    private DeviceStatus status;

    protected Device(String deviceId, String deviceName, int yearInUse, double purchasePrice, int maxBorrowDays) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã thiết bị không được rỗng.");
        }
        this.deviceId = deviceId.trim();
        setDeviceName(deviceName);
        setYearInUse(yearInUse);
        setPurchasePrice(purchasePrice);
        setMaxBorrowDays(maxBorrowDays);
        this.status = DeviceStatus.Available;
    }

    // ===== Getters / Setters (có kiểm tra hợp lệ) =====
    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        if (deviceName == null || deviceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thiết bị không được rỗng.");
        }
        this.deviceName = deviceName.trim();
    }

    public int getYearInUse() {
        return yearInUse;
    }

    public void setYearInUse(int yearInUse) {
        if (yearInUse > Year.now().getValue()) {
            throw new IllegalArgumentException("Năm đưa vào sử dụng không được lớn hơn năm hiện tại.");
        }
        this.yearInUse = yearInUse;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        if (purchasePrice <= 0) {
            throw new IllegalArgumentException("Giá mua phải lớn hơn 0.");
        }
        this.purchasePrice = purchasePrice;
    }

    public int getMaxBorrowDays() {
        return maxBorrowDays;
    }

    public void setMaxBorrowDays(int maxBorrowDays) {
        if (maxBorrowDays <= 0) {
            throw new IllegalArgumentException("Số ngày mượn tối đa phải lớn hơn 0.");
        }
        this.maxBorrowDays = maxBorrowDays;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == DeviceStatus.Available;
    }

    /** Số năm thiết bị đã được sử dụng tính đến năm hiện tại. */
    public int getYearsOfUse() {
        return Year.now().getValue() - yearInUse;
    }

    // ===== Chuyển trạng thái =====
    public void markAsBorrowed() {
        this.status = DeviceStatus.Borrowed;
    }

    public void markAsAvailable() {
        this.status = DeviceStatus.Available;
    }

    public void markAsUnderMaintenance() {
        this.status = DeviceStatus.UnderMaintenance;
    }

    public void markAsRetired() {
        this.status = DeviceStatus.Retired;
    }

    /** Chi phí bảo trì dự kiến trong một năm – mỗi loại thiết bị tự định nghĩa (đa hình). */
    public abstract double calculateAnnualMaintenanceCost();

    @Override
    public String toString() {
        return String.format("%s [ID: %s, Tên: %s, Năm SD: %d, Giá: %,.0f, Mượn tối đa: %d ngày, Trạng thái: %s]",
                getClass().getSimpleName(), deviceId, deviceName, yearInUse, purchasePrice, maxBorrowDays, status);
    }
}
