package exceptions;

/**
 * Phát sinh khi số ngày mượn không dương hoặc vượt giới hạn của thiết bị.
 */
public class InvalidBorrowPeriodException extends BorrowingException {
    private final String deviceId;
    private final int requestedDays;
    private final int maximumDays;

    public InvalidBorrowPeriodException(String deviceId, int requestedDays, int maximumDays) {
        super("Thời hạn mượn không hợp lệ cho thiết bị " + deviceId + ": yêu cầu " + requestedDays
                + " ngày, cho phép từ 1 đến " + maximumDays + " ngày.");
        this.deviceId = deviceId;
        this.requestedDays = requestedDays;
        this.maximumDays = maximumDays;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getRequestedDays() {
        return requestedDays;
    }

    public int getMaximumDays() {
        return maximumDays;
    }
}
