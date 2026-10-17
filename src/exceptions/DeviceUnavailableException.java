package exceptions;

import enums.DeviceStatus;

/**
 * Phát sinh khi thiết bị không ở trạng thái Available.
 */
public class DeviceUnavailableException extends BorrowingException {
    private final String deviceId;
    private final DeviceStatus currentStatus;

    public DeviceUnavailableException(String deviceId, DeviceStatus currentStatus) {
        super("Thiết bị " + deviceId + " không sẵn sàng (trạng thái hiện tại: " + currentStatus + ").");
        this.deviceId = deviceId;
        this.currentStatus = currentStatus;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public DeviceStatus getCurrentStatus() {
        return currentStatus;
    }
}
