package borrow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.BorrowLimitExceededException;
import exceptions.BorrowingException;
import exceptions.DeviceNotInSlipException;
import exceptions.DeviceUnavailableException;
import exceptions.DuplicateDeviceException;
import exceptions.InvalidBorrowPeriodException;
import interfaces.IBorrower;
import models.TeachingDevice;

/**
 * Phiếu mượn thiết bị (BorrowingSlip).
 * Sở hữu danh sách BorrowItem (Composition) và tham chiếu IBorrower (Aggregation).
 */
public class BorrowingSlip {
    private final String slipId;
    private final IBorrower borrower;
    private final String teachingRoom;
    private final LocalDate borrowedDate;
    private final List<BorrowItem> items;

    public BorrowingSlip(String slipId, IBorrower borrower, String teachingRoom, LocalDate borrowedDate) {
        if (slipId == null || slipId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu không được rỗng.");
        }
        if (borrower == null) {
            throw new IllegalArgumentException("Người mượn không được null.");
        }
        if (teachingRoom == null || teachingRoom.trim().isEmpty()) {
            throw new IllegalArgumentException("Phòng dạy không được rỗng.");
        }
        if (borrowedDate == null) {
            throw new IllegalArgumentException("Ngày mượn không được null.");
        }

        this.slipId = slipId.trim();
        this.borrower = borrower;
        this.teachingRoom = teachingRoom.trim();
        this.borrowedDate = borrowedDate;
        this.items = new ArrayList<>();
    }

    public String getSlipId() {
        return slipId;
    }

    public IBorrower getBorrower() {
        return borrower;
    }

    public String getTeachingRoom() {
        return teachingRoom;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public List<BorrowItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Thêm thiết bị vào phiếu mượn.
     * Áp dụng nguyên tắc Atomicity/Consistency: validate toàn bộ trước khi thay đổi trạng thái.
     */
    public void addDevice(TeachingDevice device, int borrowDays) throws BorrowingException {
        // 1. Kiểm tra device null
        if (device == null) {
            throw new IllegalArgumentException("Thiết bị không được null.");
        }

        // 2. Kiểm tra thời hạn mượn
        if (borrowDays <= 0 || borrowDays > device.getMaxBorrowDays()) {
            throw new InvalidBorrowPeriodException(device.getDeviceId(), borrowDays, device.getMaxBorrowDays());
        }

        // 3. Kiểm tra thiết bị đã tồn tại trong phiếu chưa
        for (BorrowItem item : items) {
            if (item.getDevice().getDeviceId().equalsIgnoreCase(device.getDeviceId())) {
                throw new DuplicateDeviceException(device.getDeviceId(), slipId);
            }
        }

        // 4. Kiểm tra giới hạn số lượng thiết bị của người mượn
        if (items.size() >= borrower.getMaximumDevices()) {
            throw new BorrowLimitExceededException(borrower.getBorrowerId(), borrower.getMaximumDevices());
        }

        // 5. Kiểm tra trạng thái thiết bị có sẵn sàng (Available) không
        if (!device.isAvailable()) {
            throw new DeviceUnavailableException(device.getDeviceId(), device.getStatus());
        }

        // === Chỉ khi tất cả hợp lệ mới thực hiện thay đổi trạng thái ===
        LocalDate dueDate = borrowedDate.plusDays(borrowDays);
        BorrowItem item = new BorrowItem(device, borrowedDate, dueDate);
        device.markAsBorrowed();
        items.add(item);
    }

    /**
     * Trả một thiết bị trong phiếu mượn.
     * @return Phí trễ hạn nếu có (0 nếu đúng hạn)
     */
    public double returnDevice(String deviceId, LocalDate returnedDate) throws BorrowingException {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã thiết bị không được rỗng.");
        }

        // 1. Tìm BorrowItem theo mã thiết bị
        BorrowItem foundItem = null;
        for (BorrowItem item : items) {
            if (item.getDevice().getDeviceId().equalsIgnoreCase(deviceId.trim())) {
                foundItem = item;
                break;
            }
        }

        // 2. Nếu không tìm thấy trong phiếu
        if (foundItem == null) {
            throw new DeviceNotInSlipException(deviceId.trim(), slipId);
        }

        // 3. Giao cho BorrowItem thực hiện kiểm tra đã trả/ngày trả và cập nhật trạng thái
        return foundItem.returnItem(returnedDate);
    }

    /**
     * Tính tổng phí trễ hạn của tất cả các thiết bị trong phiếu.
     */
    public double calculateTotalLateFee() {
        double total = 0.0;
        for (BorrowItem item : items) {
            total += item.calculateLateFee();
        }
        return total;
    }

    /**
     * Kiểm tra phiếu có thiết bị chưa trả hay không.
     */
    public boolean hasUnreturnedItems() {
        for (BorrowItem item : items) {
            if (!item.isReturned()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Kiểm tra phiếu có thiết bị bị trễ hạn hay không (đã trả muộn hoặc quá hạn chưa trả).
     */
    public boolean hasOverdueItems(LocalDate asOfDate) {
        for (BorrowItem item : items) {
            if (item.isOverdue(asOfDate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Phiếu mượn: %s ===\n", slipId));
        sb.append(String.format("Người mượn : %s (%s - Tối đa %d thiết bị)\n", 
                borrower.getFullName(), borrower.getBorrowerId(), borrower.getMaximumDevices()));
        sb.append(String.format("Phòng học  : %s | Ngày mượn: %s\n", teachingRoom, borrowedDate));
        sb.append(String.format("Số thiết bị: %d\n", items.size()));
        for (int i = 0; i < items.size(); i++) {
            sb.append(String.format("  [%d] %s\n", i + 1, items.get(i)));
        }
        double totalLateFee = calculateTotalLateFee();
        if (totalLateFee > 0) {
            sb.append(String.format("  -> Tổng phí trễ hạn: %,.0f đ\n", totalLateFee));
        }
        return sb.toString();
    }
}
