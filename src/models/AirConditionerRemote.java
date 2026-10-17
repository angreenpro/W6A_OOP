package models;

/**
 * Điều khiển điều hòa: chi phí bảo trì = 5% giá mua + 20.000 tiền pin.
 */
public class AirConditionerRemote extends TeachingDevice {
    private static final double BATTERY_COST = 20_000;
    private static final double LATE_FEE_PER_DAY = 10_000;

    public AirConditionerRemote(String deviceId, String deviceName, int yearInUse, double purchasePrice, int maxBorrowDays) {
        super(deviceId, deviceName, yearInUse, purchasePrice, maxBorrowDays);
    }

    @Override
    public double calculateAnnualMaintenanceCost() {
        return getPurchasePrice() * 0.05 + BATTERY_COST;
    }

    @Override
    public double getLateFeePerDay() {
        return LATE_FEE_PER_DAY;
    }
}
