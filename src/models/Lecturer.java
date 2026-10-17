package models;

import interfaces.IBorrower;

/**
 * Giảng viên – được mượn tối đa 3 thiết bị trên một phiếu.
 */
public class Lecturer implements IBorrower {
    private static final int MAXIMUM_DEVICES = 3;

    private final String lecturerId;
    private final String fullName;
    private String faculty;

    public Lecturer(String lecturerId, String fullName, String faculty) {
        if (lecturerId == null || lecturerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã giảng viên không được rỗng.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên giảng viên không được rỗng.");
        }
        this.lecturerId = lecturerId.trim();
        this.fullName = fullName.trim();
        this.faculty = faculty;
    }

    @Override
    public String getBorrowerId() {
        return lecturerId;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public int getMaximumDevices() {
        return MAXIMUM_DEVICES;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    @Override
    public String toString() {
        return "Giảng viên [ID: " + lecturerId + ", Tên: " + fullName + ", Khoa: " + faculty + "]";
    }
}
