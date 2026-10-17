package models;

import interfaces.IBorrower;

/**
 * Trợ giảng – được mượn tối đa 2 thiết bị trên một phiếu.
 */
public class TeachingAssistant implements IBorrower {
    private static final int MAXIMUM_DEVICES = 2;

    private final String taId;
    private final String fullName;

    public TeachingAssistant(String taId, String fullName) {
        if (taId == null || taId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã trợ giảng không được rỗng.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên trợ giảng không được rỗng.");
        }
        this.taId = taId.trim();
        this.fullName = fullName.trim();
    }

    @Override
    public String getBorrowerId() {
        return taId;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public int getMaximumDevices() {
        return MAXIMUM_DEVICES;
    }

    @Override
    public String toString() {
        return "Trợ giảng [ID: " + taId + ", Tên: " + fullName + "]";
    }
}
