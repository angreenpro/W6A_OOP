package rooms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import enums.DeviceStatus;
import models.Device;

/**
 * Lớp đại diện cho phòng học (Classroom).
 * Sử dụng quan hệ kết tập (Aggregation) chứa danh sách Device.
 */
public class Classroom {
    private final String code;
    private String name;
    private int capacity;
    private final List<Device> devices;

    public Classroom(String code, String name, int capacity) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng không được rỗng.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phòng không được rỗng.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Sức chứa phòng phải lớn hơn 0.");
        }

        this.code = code.trim();
        this.name = name.trim();
        this.capacity = capacity;
        this.devices = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phòng không được rỗng.");
        }
        this.name = name.trim();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Sức chứa phòng phải lớn hơn 0.");
        }
        this.capacity = capacity;
    }

    public List<Device> getDevices() {
        return Collections.unmodifiableList(devices);
    }

    /**
     * Thêm thiết bị vào phòng học.
     * Quy tắc: Không chấp nhận null, không trùng mã thiết bị.
     */
    public void addDevice(Device device) {
        if (device == null) {
            throw new IllegalArgumentException("Thiết bị không được null.");
        }
        if (findDevice(device.getDeviceId()) != null) {
            throw new IllegalArgumentException("Thiết bị với mã " + device.getDeviceId() 
                    + " đã tồn tại trong phòng " + code + ".");
        }
        devices.add(device);
    }

    /**
     * Xóa thiết bị khỏi phòng học theo mã.
     */
    public boolean removeDevice(String deviceId) {
        if (deviceId == null) {
            return false;
        }
        return devices.removeIf(d -> d.getDeviceId().equalsIgnoreCase(deviceId.trim()));
    }

    /**
     * Tìm thiết bị trong phòng theo mã.
     */
    public Device findDevice(String deviceId) {
        if (deviceId == null) {
            return null;
        }
        for (Device d : devices) {
            if (d.getDeviceId().equalsIgnoreCase(deviceId.trim())) {
                return d;
            }
        }
        return null;
    }

    /**
     * Tính tổng chi phí bảo trì hằng năm của tất cả thiết bị trong phòng bằng tính đa hình.
     * Tuyệt đối không kiểm tra bằng if/switch theo kiểu cụ thể.
     */
    public double calculateAnnualMaintenanceCost() {
        double totalCost = 0.0;
        for (Device device : devices) {
            totalCost += device.calculateAnnualMaintenanceCost();
        }
        return totalCost;
    }

    /**
     * Trả về danh sách thiết bị cần bảo dưỡng:
     * - Đang ở trạng thái UnderMaintenance; hoặc
     * - Có thời gian sử dụng trên 5 năm.
     */
    public List<Device> getDevicesRequiringMaintenance() {
        List<Device> result = new ArrayList<>();
        for (Device device : devices) {
            if (device.getStatus() == DeviceStatus.UnderMaintenance || device.getYearsOfUse() > 5) {
                result.add(device);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Phòng học [%s - %s, Sức chứa: %d, Số thiết bị: %d, Chi phí bảo trì năm: %,.0f đ]",
                code, name, capacity, devices.size(), calculateAnnualMaintenanceCost());
    }
}
