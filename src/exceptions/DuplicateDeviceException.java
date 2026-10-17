package exceptions;

/**
 * Phát sinh khi cùng một thiết bị đã tồn tại trong phiếu.
 */
public class DuplicateDeviceException extends BorrowingException {
    private final String deviceId;
    private final String slipId;

    public DuplicateDeviceException(String deviceId, String slipId) {
        super("Thiết bị " + deviceId + " đã có trong phiếu " + slipId + ".");
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
