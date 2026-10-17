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
├── run.ps1                    # Script tự động biên dịch và chạy trên PowerShell (UTF-8)
├── run.bat                    # Script tự động biên dịch và chạy trên CMD (UTF-8)
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

### 6.1. Yêu cầu môi trường
- **JDK 17+** (đã kiểm tra tương thích tốt với JDK 17, 21, 27).
- Kiểm tra cài đặt Java trên máy:
  ```powershell
  java -version
  javac -version
  ```

### 6.2. Các cách chạy chương trình

#### Cách 1: Sử dụng Script tự động (Khuyên dùng - Nhanh nhất & Không lỗi font)
Mở terminal tại thư mục gốc `W6` (nơi chứa file script và thư mục `src/`), chạy file script tương ứng với terminal bạn đang dùng:

- **Trên PowerShell** (Terminal mặc định trong VS Code):
  ```powershell
  .\run.ps1
  ```
- **Trên Command Prompt (CMD)**:
  ```bat
  run.bat
  ```

> [!TIP]
> Script đã tự động cấu hình bảng mã UTF-8 (`[Console]::OutputEncoding`, `chcp 65001`), biên dịch toàn bộ các lớp vào thư mục `out/` và thực thi chương trình.

---

#### Cách 2: Chạy thủ công bằng dòng lệnh

**Windows – PowerShell**
```powershell
# 1. Cấu hình bảng mã UTF-8 cho PowerShell (bắt buộc để không bị lỗi font tiếng Việt)
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001

# 2. Biên dịch toàn bộ mã nguồn vào thư mục out với encoding UTF-8
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName

# 3. Chạy chương trình
java -cp out Main
```

**Windows – CMD**
```bat
chcp 65001
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
del sources.txt
java -cp out Main
```

**macOS / Linux (Bash / Zsh)**
```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main
```

---

#### Cách 3: Chạy trực tiếp trên Visual Studio Code (GUI)
1. Cài đặt tiện ích mở rộng: **Extension Pack for Java** (Microsoft).
2. Mở thư mục dự án `W6` trong VS Code.
3. Mở file [src/Main.java](file:///d:/VS%20Code/OOP/W6/src/Main.java).
4. Nhấn nút **Run** (biểu tượng ▶️ xuất hiện ngay phía trên dòng khai báo hàm `public static void main`) hoặc nhấn phím tắt **F5** / **Ctrl + F5**.

> [!NOTE]
> - Trong mã nguồn [src/Main.java](file:///d:/VS%20Code/OOP/W6/src/Main.java), chương trình đã tích hợp sẵn khối `static` tự động ép kiểu `PrintStream` sang UTF-8 cho `System.out` và `System.err`.
> - **Lưu ý riêng trên Windows PowerShell**: Khi gõ lệnh thủ công trên PowerShell, lệnh `chcp 65001` chỉ áp dụng cho tiến trình Win32 ngoài. Cần chạy thêm `[Console]::OutputEncoding = [System.Text.Encoding]::UTF8` để PowerShell dịch đúng các byte UTF-8 từ Java mà không bị vỡ font.

