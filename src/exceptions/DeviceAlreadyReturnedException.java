package exceptions;

import java.time.LocalDate;

/**
 * Phát sinh khi thiết bị trong phiếu đã được trả trước đó.
 */
public class DeviceAlreadyReturnedException extends BorrowingException {
    private final String deviceId;
    private final LocalDate returnedDate;

    public DeviceAlreadyReturnedException(String deviceId, LocalDate returnedDate) {
        super("Thiết bị " + deviceId + " đã được trả vào ngày " + returnedDate + ".");
        this.deviceId = deviceId;
        this.returnedDate = returnedDate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }
}
