package exceptions;

/**
 * Phát sinh khi yêu cầu trả một thiết bị không thuộc phiếu.
 */
public class DeviceNotInSlipException extends BorrowingException {
    private final String deviceId;
    private final String slipId;

    public DeviceNotInSlipException(String deviceId, String slipId) {
        super("Thiết bị " + deviceId + " không thuộc phiếu " + slipId + ".");
        this.deviceId = deviceId;
        this.slipId = slipId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getSlipId() {
        return slipId;
    }
}
