package models;

/**
 * Điều khiển máy chiếu: chi phí bảo trì = 8% giá mua.
 */
public class ProjectorRemote extends TeachingDevice {
    private static final double LATE_FEE_PER_DAY = 15_000;

    public ProjectorRemote(String deviceId, String deviceName, int yearInUse, double purchasePrice, int maxBorrowDays) {
        super(deviceId, deviceName, yearInUse, purchasePrice, maxBorrowDays);
    }

    @Override
    public double calculateAnnualMaintenanceCost() {
        return getPurchasePrice() * 0.08;
    }

    @Override
    public double getLateFeePerDay() {
        return LATE_FEE_PER_DAY;
    }
}
