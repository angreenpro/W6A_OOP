# W06 – Hệ thống quản lý thiết bị & phiếu mượn

**Lập trình hướng đối tượng**
> Ngôn ngữ: **Java**

## 1. Tổng quan bài toán

Một trường đại học cần quản lý các thiết bị phục vụ giảng dạy. Ban đầu hệ thống có ba loại thiết bị:

- Điều khiển điều hòa (`AirConditionerRemote`)
- Micro (`Microphone`)
- Điều khiển máy chiếu (`ProjectorRemote`)

Mỗi thiết bị có mã, tên, năm đưa vào sử dụng, giá mua, trạng thái và số tiết/số ngày được phép mượn tối đa.
Một **phiếu mượn** thuộc về một **người mượn** (giảng viên hoặc trợ giảng) và có thể chứa nhiều thiết bị.

Mọi tình huống bất thường đều được báo bằng **ngoại lệ (exception)**. Khi một thao tác thất bại,
trạng thái của thiết bị và phiếu mượn phải **được giữ nhất quán** (không thay đổi một phần).


## 2. Kiến thức OOP được áp dụng

| Khái niệm | Thể hiện trong bài |
|---|---|
| **Enum** | `DeviceStatus` (`Available`, `Borrowed`, `UnderMaintenance`) |
| **Lớp trừu tượng** | `Device` với phương thức trừu tượng tính chi phí bảo trì hằng năm |
| **Kế thừa & đa hình** | Các thiết bị cụ thể ghi đè cách tính chi phí bảo trì; `Classroom` tính tổng mà không dùng `if`/`switch` theo kiểu |
| **Đóng gói** | Mã thiết bị chỉ gán khi khởi tạo; kiểm tra dữ liệu hợp lệ (mã không rỗng, giá > 0, năm ≤ năm hiện tại) |
| **Interface** | `IBorrower` được hiện thực bởi `Lecturer` (tối đa 3 thiết bị) và `TeachingAssistant` (tối đa 2 thiết bị) |
| **Kết tập (Aggregation)** | `Classroom` chứa danh sách `Device` |
| **Hợp thành (Composition)** | `BorrowingSlip` sở hữu các `BorrowItem`; `BorrowItem` chỉ tham chiếu `TeachingDevice` |
| **Ngoại lệ tự định nghĩa** | Cây ngoại lệ kế thừa từ `BorrowingException` |

## 3. Cấu trúc thư mục

```text
W6/
├── README.md
├── AI_prompt_logs.md          # Nhật ký tương tác với AI
├── lab w06.pdf / lab w06.txt  # Đề bài
└── src/
    ├── Main.java              # Điểm chạy chương trình, kịch bản kiểm thử
    ├── enums/
    │   └── DeviceStatus.java
    ├── models/                # Thiết bị và người mượn
    │   ├── Device.java
    │   ├── TeachingDevice.java
    │   ├── AirConditionerRemote.java
    │   ├── Microphone.java
    │   ├── Lecturer.java
    │   └── TeachingAssistant.java
    ├── interfaces/
    │   └── IBorrower.java
    ├── borrow/
    │   ├── BorrowItem.java
    │   └── BorrowingSlip.java
    ├── rooms/
    │   └── Classroom.java
    └── exceptions/
        ├── BorrowingException.java
        ├── BorrowLimitExceedException.java
        ├── DeviceAlreadyReturnedException.java
        ├── DeviceNotInSlipException.java
        ├── DeviceUnavailableException.java
        ├── DuplicateDeviceException.java
        └── InvalidBorrowPeriodException.java
```

## 4. Các thao tác nghiệp vụ chính

### 4.1. Thêm thiết bị vào phiếu – `BorrowingSlip.addDevice(device, borrowDays)`
Kiểm tra **trước** khi thay đổi trạng thái:
1. `device` khác `null`.
2. `borrowDays > 0` và không vượt giới hạn của thiết bị → `InvalidBorrowPeriodException`.
3. Thiết bị chưa có trong phiếu → `DuplicateDeviceException`.
4. Người mượn chưa đạt giới hạn → `BorrowLimitExceededException`.
5. Thiết bị đang `Available` → `DeviceUnavailableException`.

Chỉ khi hợp lệ mới: tạo `BorrowItem` → chuyển thiết bị sang `Borrowed` → thêm vào danh sách.

### 4.2. Trả thiết bị – `BorrowingSlip.returnDevice(deviceId, returnedDate)`
1. Tìm `BorrowItem` theo mã → không thấy: `DeviceNotInSlipException`.
2. Đã trả rồi → `DeviceAlreadyReturnedException`.
3. Ngày trả trước ngày mượn → ngoại lệ ngày không hợp lệ.
4. Ghi nhận ngày trả, chuyển thiết bị về `Available`, trả về phí trễ hạn (nếu có).

### 4.3. Quản lý phòng học – `Classroom`
- `addDevice`, `removeDevice`, `findDevice` (không trùng mã, không nhận `null`).
- `calculateAnnualMaintenanceCost()` – cộng dồn bằng đa hình.
- `getDevicesRequiringMaintenance()` – thiết bị `UnderMaintenance` hoặc đã dùng **trên 5 năm**.

## 5. Kịch bản kiểm thử (trong `Main`)

**Luồng thành công**
1. Tạo giảng viên; tạo điều khiển máy chiếu, điều khiển điều hòa, micro.
2. Tạo phiếu mượn cho một phòng, thêm điều khiển máy chiếu và micro.
3. Trả điều khiển máy chiếu đúng hạn, trả micro muộn 2 ngày.
4. Hiển thị các phiếu trả muộn / chưa trả.

**Luồng ngoại lệ** (bắt bằng `try – catch – finally`)
1. Mượn điều khiển điều hòa đang `UnderMaintenance`.
2. Trợ giảng mượn thiết bị thứ ba.
3. Thêm cùng một micro hai lần.
4. Mượn micro 3 ngày khi giới hạn là 1 ngày.
5. Trả điều khiển máy chiếu không thuộc phiếu.
6. Trả cùng một thiết bị hai lần.
7. Trả thiết bị với ngày trả trước ngày mượn.

## 6. Hướng dẫn chạy chương trình

### 6.1. Yêu cầu
- **JDK 17+** (đã kiểm tra với JDK 27). Kiểm tra bằng:
  ```bash
  java -version
  javac -version
  ```

### 6.2. Chạy bằng dòng lệnh
Mở terminal tại thư mục gốc `W6` (thư mục chứa `src`).

**Windows – PowerShell**
```powershell
# Biên dịch toàn bộ file .java vào thư mục out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName

# Chạy chương trình
java -cp out Main
```

**Windows – CMD**
```bat
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
java -cp out Main
```

**macOS / Linux**
```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main
```

> [!NOTE]
> - `-d out` đặt các file `.class` vào thư mục `out/` theo đúng cấu trúc package, tránh làm bẩn `src`.
> - `-encoding UTF-8` giúp hiển thị đúng tiếng Việt trong chuỗi.
> - Nếu tiếng Việt bị lỗi font trên console Windows, chạy `chcp 65001` trước khi `java`.

### 6.3. Chạy bằng VS Code
1. Cài extension **Extension Pack for Java** (Microsoft).
2. Mở thư mục `W6` bằng VS Code.
3. Mở `src/Main.java` → bấm **Run** phía trên hàm `main` (hoặc nhấn `F5`).

