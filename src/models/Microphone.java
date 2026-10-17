package models;

/**
 * Micro không dây: chi phí bảo trì = 10% giá mua.
 */
public class Microphone extends TeachingDevice {
    private static final double LATE_FEE_PER_DAY = 20_000;

    public Microphone(String deviceId, String deviceName, int yearInUse, double purchasePrice, int maxBorrowDays) {
        super(deviceId, deviceName, yearInUse, purchasePrice, maxBorrowDays);
    }

    @Override
    public double calculateAnnualMaintenanceCost() {
        return getPurchasePrice() * 0.10;
    }

    @Override
    public double getLateFeePerDay() {
        return LATE_FEE_PER_DAY;
    }
}
