import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import borrow.BorrowItem;
import borrow.BorrowingSlip;
import enums.DeviceStatus;
import exceptions.BorrowLimitExceededException;
import exceptions.BorrowingException;
import exceptions.DeviceAlreadyReturnedException;
import exceptions.DeviceNotInSlipException;
import exceptions.DeviceUnavailableException;
import exceptions.DuplicateDeviceException;
import exceptions.InvalidBorrowPeriodException;
import exceptions.InvalidReturnDateException;
import models.AirConditionerRemote;
import models.Lecturer;
import models.Microphone;
import models.ProjectorRemote;
import models.TeachingAssistant;
import rooms.Classroom;

/**
 * Lớp chạy chương trình chính (Main) thực hiện đầy đủ kịch bản kiểm thử:
 * - 11.1 Luồng thành công (Happy Path)
 * - 11.2 Luồng phát sinh ngoại lệ (7 tình huống kiểm thử với try-catch-finally)
 */
public class Main {

    public static void main(String[] args) {
        printBanner("CHƯƠNG TRÌNH QUẢN LÝ THIẾT BỊ VÀ PHIẾU MƯỢN - OOP W06");

        // Minh họa tính năng quản lý phòng học và tính chi phí bảo trì đa hình
        demoClassroomManagement();

        // 11.1 Luồng thành công
        demoSuccessFlow();

        // 11.2 Luồng phát sinh ngoại lệ (7 tình huống)
        demoExceptionFlows();

        printBanner("KẾT THÚC CHƯƠNG TRÌNH KIỂM THỬ THÀNH CÔNG");
    }

    // =========================================================================
    // 0. MINH HỌA QUẢN LÝ PHÒNG HỌC & ĐA HÌNH
    // =========================================================================
    private static void demoClassroomManagement() {
        printSectionHeader("0. QUẢN LÝ PHÒNG HỌC & TÍNH CHI PHÍ BẢO TRÌ (ĐA HÌNH)");

        Classroom room = new Classroom("A101", "Phòng Đa phương tiện A101", 60);

        ProjectorRemote pr = new ProjectorRemote("PR-01", "Điều khiển máy chiếu Sony", 2023, 600_000, 3);
        AirConditionerRemote ac = new AirConditionerRemote("AC-01", "Điều khiển Panasonic", 2018, 400_000, 5); // Đã
                                                                                                               // dùng 8
                                                                                                               // năm (>
                                                                                                               // 5 năm)
        Microphone mic = new Microphone("MIC-01", "Micro không dây Shure", 2024, 1_500_000, 1);
        Microphone micOld = new Microphone("MIC-02", "Micro cũ bảo trì", 2021, 1_200_000, 1);
        micOld.markAsUnderMaintenance(); // Đang UnderMaintenance

        room.addDevice(pr);
        room.addDevice(ac);
        room.addDevice(mic);
        room.addDevice(micOld);

        System.out.println("Thông tin phòng học: " + room);
        System.out.println("\nChi tiết các thiết bị trong phòng:");
        for (var d : room.getDevices()) {
            System.out.printf("  - %s (Chi phí bảo trì/năm: %,.0f đ)\n", d, d.calculateAnnualMaintenanceCost());
        }

        System.out.printf("\n=> TỔNG CHI PHÍ BẢO TRÌ HẰNG NĂM (Đa hình): %,.0f đ\n",
                room.calculateAnnualMaintenanceCost());

        System.out.println("\nDanh sách thiết bị cần bảo dưỡng (trên 5 năm hoặc UnderMaintenance):");
        for (var d : room.getDevicesRequiringMaintenance()) {
            System.out.printf("  [!] %s - Tuổi đời: %d năm | Trạng thái: %s\n",
                    d.getDeviceName(), d.getYearsOfUse(), d.getStatus());
        }
    }

    // =========================================================================
    // 11.1 LUỒNG THÀNH CÔNG (HAPPY PATH)
    // =========================================================================
    private static void demoSuccessFlow() {
        printSectionHeader("11.1. LUỒNG THÀNH CÔNG (HAPPY PATH)");

        try {
            // Bước 1: Tạo một giảng viên
            System.out.println("--- Bước 1: Khởi tạo Giảng viên ---");
            Lecturer lecturer = new Lecturer("GV001", "TS. Nguyễn Văn An", "Công nghệ Thông tin");
            System.out.println("Đã tạo: " + lecturer);

            // Bước 2: Tạo một điều khiển máy chiếu, một điều khiển điều hòa, một micro
            // không dây
            System.out.println("\n--- Bước 2: Khởi tạo 3 thiết bị giảng dạy ---");
            ProjectorRemote pr = new ProjectorRemote("PR101", "Điều khiển máy chiếu Epson", 2023, 500_000, 3);
            AirConditionerRemote ac = new AirConditionerRemote("AC101", "Điều khiển Daikin", 2022, 350_000, 5);
            Microphone mic = new Microphone("MIC101", "Micro AKG không dây", 2024, 1_000_000, 1);

            System.out.println("1. " + pr);
            System.out.println("2. " + ac);
            System.out.println("3. " + mic);

            // Bước 3: Tạo phiếu mượn cho một phòng dạy
            System.out.println("\n--- Bước 3: Tạo phiếu mượn cho phòng B202 ---");
            LocalDate borrowDate = LocalDate.of(2026, 10, 10);
            BorrowingSlip slip1 = new BorrowingSlip("PHIEU-001", lecturer, "B202", borrowDate);
            System.out.println("Khởi tạo phiếu: " + slip1.getSlipId() + " cho " + slip1.getBorrower().getFullName());

            // Bước 4: Thêm điều khiển máy chiếu (mượn 2 ngày) và micro (mượn 1 ngày) vào
            // phiếu
            System.out.println("\n--- Bước 4: Thêm thiết bị vào phiếu mượn ---");
            slip1.addDevice(pr, 2); // Hạn trả: 2026-10-12
            System.out.printf("Đã thêm: %s (Mượn 2 ngày, hạn trả: %s)\n", pr.getDeviceName(), borrowDate.plusDays(2));

            slip1.addDevice(mic, 1); // Hạn trả: 2026-10-11
            System.out.printf("Đã thêm: %s (Mượn 1 ngày, hạn trả: %s)\n", mic.getDeviceName(), borrowDate.plusDays(1));

            System.out.println("\nTrạng thái phiếu mượn sau khi thêm thiết bị:");
            System.out.println(slip1);

            // Bước 5: Trả điều khiển máy chiếu đúng hạn
            System.out.println("--- Bước 5: Trả điều khiển máy chiếu đúng hạn ---");
            LocalDate prReturnDate = LocalDate.of(2026, 10, 12); // Đúng hạn ngày 12
            double prLateFee = slip1.returnDevice("PR101", prReturnDate);
            System.out.printf("-> Trả thiết bị PR101 ngày %s thành công. Phí trễ: %,.0f đ | Trạng thái thiết bị: %s\n",
                    prReturnDate, prLateFee, pr.getStatus());

            // Bước 6: Trả micro muộn 2 ngày
            System.out.println("\n--- Bước 6: Trả micro muộn 2 ngày ---");
            // Hạn trả là 2026-10-11 -> Trả ngày 2026-10-13 (muộn 2 ngày)
            LocalDate micReturnDate = LocalDate.of(2026, 10, 13);
            double micLateFee = slip1.returnDevice("MIC101", micReturnDate);
            System.out.printf(
                    "-> Trả thiết bị MIC101 ngày %s thành công (Quá hạn 2 ngày). Phí trễ: %,.0f đ | Trạng thái thiết bị: %s\n",
                    micReturnDate, micLateFee, mic.getStatus());

            // Bước 7: Hiển thị các phiếu trả muộn, chưa trả
            System.out.println("\n--- Bước 7: Hiển thị các phiếu trả muộn / chưa trả ---");

            // Tạo thêm 1 phiếu thứ hai chưa trả để hiển thị danh sách đa dạng
            TeachingAssistant ta = new TeachingAssistant("TA002", "Lê Văn Cường");
            BorrowingSlip slip2 = new BorrowingSlip("PHIEU-002", ta, "C303", LocalDate.of(2026, 10, 14));
            slip2.addDevice(ac, 3); // ac đang được mượn, chưa trả

            List<BorrowingSlip> allSlips = new ArrayList<>();
            allSlips.add(slip1);
            allSlips.add(slip2);

            LocalDate today = LocalDate.of(2026, 10, 17);
            System.out.println("Ngày xét duyệt hiện tại: " + today);

            System.out.println("\n>> DANH SÁCH PHIẾU CÓ THIẾT BỊ TRẢ MUỘN / QUÁ HẠN:");
            for (BorrowingSlip slip : allSlips) {
                if (slip.hasOverdueItems(today)) {
                    System.out.println(slip);
                }
            }

            System.out.println(">> DANH SÁCH PHIẾU CÒN THIẾT BỊ CHƯA TRẢ:");
            for (BorrowingSlip slip : allSlips) {
                if (slip.hasUnreturnedItems()) {
                    System.out.println(slip);
                }
            }

        } catch (BorrowingException e) {
            System.err.println("Lỗi không mong muốn trong luồng thành công: " + e.getMessage());
        }
    }

    // =========================================================================
    // 11.2 LUỒNG PHÁT SINH NGOẠI LỆ (7 TÌNH HUỐNG KIỂM THỬ)
    // =========================================================================
    private static void demoExceptionFlows() {
        printSectionHeader("11.2. LUỒNG PHÁT SINH NGOẠI LỆ (TRY - CATCH - FINALLY)");

        testCase1_DeviceUnavailable();
        testCase2_BorrowLimitExceeded();
        testCase3_DuplicateDevice();
        testCase4_InvalidBorrowPeriod();
        testCase5_DeviceNotInSlip();
        testCase6_DeviceAlreadyReturned();
        testCase7_InvalidReturnDate();
    }

    /**
     * Tình huống 1: Mượn điều khiển điều hòa đang UnderMaintenance
     */
    private static void testCase1_DeviceUnavailable() {
        System.out.println("\n[Test Case 1] Mượn thiết bị đang ở trạng thái UnderMaintenance:");
        AirConditionerRemote ac = new AirConditionerRemote("AC-ERR1", "Điều hòa LG", 2021, 300_000, 5);
        ac.markAsUnderMaintenance(); // Đang bảo trì

        Lecturer lecturer = new Lecturer("GV002", "TS. Hoàng Minh", "Điện tử");
        BorrowingSlip slip = new BorrowingSlip("SLIP-ERR-01", lecturer, "A201", LocalDate.now());

        try {
            System.out.printf("Thử mượn thiết bị %s (Trạng thái: %s)...\n", ac.getDeviceId(), ac.getStatus());
            slip.addDevice(ac, 3);
            System.out.println("THẤT BẠI: Lẽ ra phải ném DeviceUnavailableException!");
        } catch (DeviceUnavailableException e) {
            System.out.println("BẮT ĐƯỢC NGOẠI LỆ: " + e.getMessage());
            System.out.printf("-> Chi tiết: Mã TB = %s, Trạng thái TB = %s\n", e.getDeviceId(), e.getCurrentStatus());
        } catch (BorrowingException e) {
            System.out.println("Bắt ngoại lệ BorrowingException: " + e.getMessage());
        } finally {
            System.out.printf(
                    "FINALLY (Kiểm tra tính nhất quán): Phiếu có %d thiết bị, Thiết bị vẫn có trạng thái: %s\n",
                    slip.getItems().size(), ac.getStatus());
        }
    }

    /**
     * Tình huống 2: Trợ giảng mượn thiết bị thứ ba (Vượt hạn mức tối đa 2 thiết bị)
     */
    private static void testCase2_BorrowLimitExceeded() {
        System.out.println("\n[Test Case 2] Trợ giảng mượn vượt quá hạn mức (tối đa 2 thiết bị):");
        TeachingAssistant ta = new TeachingAssistant("TA-003", "Trợ giảng Phạm Thu");
        BorrowingSlip slip = new BorrowingSlip("SLIP-ERR-02", ta, "Lab-1", LocalDate.now());

        ProjectorRemote p1 = new ProjectorRemote("PR-T1", "Điều khiển chiếu 1", 2023, 400_000, 3);
        ProjectorRemote p2 = new ProjectorRemote("PR-T2", "Điều khiển chiếu 2", 2023, 400_000, 3);
        Microphone mic3 = new Microphone("MIC-T3", "Micro trợ giảng", 2024, 800_000, 2);

        try {
            slip.addDevice(p1, 1);
            System.out.println("Mượn thiết bị 1 thành công: " + p1.getDeviceId());
            slip.addDevice(p2, 1);
            System.out.println("Mượn thiết bị 2 thành công: " + p2.getDeviceId());

            System.out.println("Thử mượn thiết bị thứ 3 (vượt quá hạn mức 2 của trợ giảng)...");
            slip.addDevice(mic3, 1);
            System.out.println("THẤT BẠI: Lẽ ra phải ném BorrowLimitExceededException!");
        } catch (BorrowLimitExceededException e) {
            System.out.println("BẮT ĐƯỢC NGOẠI LỆ: " + e.getMessage());
            System.out.printf("-> Chi tiết: Mã người mượn = %s, Hạn mức tối đa = %d\n", e.getBorrowerId(),
                    e.getMaximumDevices());
        } catch (BorrowingException e) {
            System.out.println("Bắt ngoại lệ BorrowingException: " + e.getMessage());
        } finally {
            System.out.printf(
                    "FINALLY (Kiểm tra tính nhất quán): Số thiết bị trong phiếu: %d (không vượt quá 2), Trạng thái mic3: %s\n",
                    slip.getItems().size(), mic3.getStatus());
        }
    }

    /**
     * Tình huống 3: Thêm cùng một micro hai lần vào cùng một phiếu
     */
    private static void testCase3_DuplicateDevice() {
        System.out.println("\n[Test Case 3] Thêm cùng một thiết bị hai lần vào cùng một phiếu:");
        Lecturer lecturer = new Lecturer("GV003", "PGS. Đỗ Văn Nam", "Toán Tin");
        BorrowingSlip slip = new BorrowingSlip("SLIP-ERR-03", lecturer, "C101", LocalDate.now());
        Microphone mic = new Microphone("MIC-DUP", "Micro Shure Kẹp áo", 2024, 1_000_000, 2);

        try {
            slip.addDevice(mic, 1);
            System.out.println("Thêm micro lần 1 thành công: " + mic.getDeviceId());

            System.out.println("Thử thêm lại chính micro đó lần 2 vào cùng phiếu...");
            slip.addDevice(mic, 1);
            System.out.println("THẤT BẠI: Lẽ ra phải ném DuplicateDeviceException!");
        } catch (DuplicateDeviceException e) {
            System.out.println("BẮT ĐƯỢC NGOẠI LỆ: " + e.getMessage());
            System.out.printf("-> Chi tiết: Mã thiết bị trùng = %s, Mã phiếu = %s\n", e.getDeviceId(), e.getSlipId());
        } catch (BorrowingException e) {
            System.out.println("Bắt ngoại lệ BorrowingException: " + e.getMessage());
        } finally {
            System.out.printf("FINALLY (Kiểm tra tính nhất quán): Số lượng thiết bị trong phiếu vẫn là %d\n",
                    slip.getItems().size());
        }
    }

    /**
     * Tình huống 4: Mượn micro trong 3 ngày dù giới hạn cho phép là 1 ngày
     */
    private static void testCase4_InvalidBorrowPeriod() {
        System.out.println("\n[Test Case 4] Mượn micro 3 ngày khi giới hạn tối đa chỉ cho phép 1 ngày:");
        Lecturer lecturer = new Lecturer("GV004", "TS. Vũ Hải", "CNTT");
        BorrowingSlip slip = new BorrowingSlip("SLIP-ERR-04", lecturer, "D101", LocalDate.now());
        Microphone mic = new Microphone("MIC-LIMIT", "Micro Hội trường", 2023, 1_200_000, 1); // Max 1 ngày

        try {
            System.out.printf("Thử mượn thiết bị %s (Giới hạn: %d ngày) trong thời hạn 3 ngày...\n",
                    mic.getDeviceId(), mic.getMaxBorrowDays());
            slip.addDevice(mic, 3);
            System.out.println("THẤT BẠI: Lẽ ra phải ném InvalidBorrowPeriodException!");
        } catch (InvalidBorrowPeriodException e) {
            System.out.println("BẮT ĐƯỢC NGOẠI LỆ: " + e.getMessage());
            System.out.printf("-> Chi tiết: Mã TB = %s, Số ngày yêu cầu = %d, Giới hạn tối đa = %d\n",
                    e.getDeviceId(), e.getRequestedDays(), e.getMaximumDays());
        } catch (BorrowingException e) {
            System.out.println("Bắt ngoại lệ BorrowingException: " + e.getMessage());
        } finally {
            System.out.printf(
                    "FINALLY (Kiểm tra tính nhất quán): Phiếu có %d thiết bị, Thiết bị vẫn ở trạng thái: %s\n",
                    slip.getItems().size(), mic.getStatus());
        }
    }

    /**
     * Tình huống 5: Trả một điều khiển máy chiếu không thuộc phiếu
     */
    private static void testCase5_DeviceNotInSlip() {
        System.out.println("\n[Test Case 5] Trả thiết bị không tồn tại trong phiếu mượn:");
        Lecturer lecturer = new Lecturer("GV005", "ThS. Mai Hoa", "Ngoại ngữ");
        BorrowingSlip slip = new BorrowingSlip("SLIP-ERR-05", lecturer, "E101", LocalDate.now());

        Microphone mic = new Microphone("MIC-OK", "Micro để bàn", 2024, 700_000, 2);

        try {
            slip.addDevice(mic, 1);
            System.out.println("Phiếu chỉ chứa thiết bị: " + mic.getDeviceId());

            System.out.println("Thử gọi trả thiết bị PR-UNKNOWN (không có trong phiếu)...");
            slip.returnDevice("PR-UNKNOWN", LocalDate.now());
            System.out.println("THẤT BẠI: Lẽ ra phải ném DeviceNotInSlipException!");
        } catch (DeviceNotInSlipException e) {
            System.out.println("BẮT ĐƯỢC NGOẠI LỆ: " + e.getMessage());
            System.out.printf("-> Chi tiết: Thiết bị không thuộc phiếu = %s, Phiếu = %s\n", e.getDeviceId(),
                    e.getSlipId());
        } catch (BorrowingException e) {
            System.out.println("Bắt ngoại lệ BorrowingException: " + e.getMessage());
        } finally {
            System.out.printf("FINALLY (Kiểm tra tính nhất quán): Thiết bị %s trong phiếu vẫn giữ trạng thái: %s\n",
                    mic.getDeviceId(), mic.getStatus());
        }
    }

    /**
     * Tình huống 6: Trả cùng một thiết bị hai lần
     */
    private static void testCase6_DeviceAlreadyReturned() {
        System.out.println("\n[Test Case 6] Trả cùng một thiết bị hai lần:");
        Lecturer lecturer = new Lecturer("GV006", "TS. Đặng Tuấn", "Cơ khí");
        LocalDate borrowDate = LocalDate.of(2026, 10, 10);
        BorrowingSlip slip = new BorrowingSlip("SLIP-ERR-06", lecturer, "F201", borrowDate);

        ProjectorRemote pr = new ProjectorRemote("PR-RET2", "Điều khiển Panasonic", 2023, 450_000, 3);

        try {
            slip.addDevice(pr, 2);
            System.out.println("Đã mượn thiết bị: " + pr.getDeviceId());

            // Lần 1: Trả bình thường
            slip.returnDevice("PR-RET2", LocalDate.of(2026, 10, 12));
            System.out.println("Trả lần 1 thành công vào ngày 2026-10-12.");

            // Lần 2: Thử trả tiếp lần nữa
            System.out.println("Thử gọi trả lần 2 cho cùng thiết bị PR-RET2...");
            slip.returnDevice("PR-RET2", LocalDate.of(2026, 10, 13));
            System.out.println("THẤT BẠI: Lẽ ra phải ném DeviceAlreadyReturnedException!");
        } catch (DeviceAlreadyReturnedException e) {
            System.out.println("BẮT ĐƯỢC NGOẠI LỆ: " + e.getMessage());
            System.out.printf("-> Chi tiết: Mã thiết bị = %s, Đã trả vào ngày = %s\n", e.getDeviceId(),
                    e.getReturnedDate());
        } catch (BorrowingException e) {
            System.out.println("Bắt ngoại lệ BorrowingException: " + e.getMessage());
        } finally {
            BorrowItem item = slip.getItems().get(0);
            System.out.printf("FINALLY (Kiểm tra tính nhất quán): Mục mượn đã trả = %b, Ngày trả ghi nhận vẫn là: %s\n",
                    item.isReturned(), item.getReturnedDate());
        }
    }

    /**
     * Tình huống 7: Trả thiết bị với ngày trả trước ngày mượn
     */
    private static void testCase7_InvalidReturnDate() {
        System.out.println("\n[Test Case 7] Trả thiết bị với ngày trả trước ngày mượn:");
        Lecturer lecturer = new Lecturer("GV007", "PGS. Bùi Hữu Lộc", "Vật lý");
        LocalDate borrowDate = LocalDate.of(2026, 10, 15);
        BorrowingSlip slip = new BorrowingSlip("SLIP-ERR-07", lecturer, "H101", borrowDate);

        Microphone mic = new Microphone("MIC-DATE", "Micro Bosch", 2024, 900_000, 2);

        try {
            slip.addDevice(mic, 2);
            System.out.printf("Đã mượn thiết bị %s vào ngày %s\n", mic.getDeviceId(), borrowDate);

            // Thử trả vào ngày 2026-10-10 (trước ngày mượn 2026-10-15)
            LocalDate invalidDate = LocalDate.of(2026, 10, 10);
            System.out.printf("Thử trả vào ngày không hợp lệ: %s (trước ngày mượn)...\n", invalidDate);
            slip.returnDevice("MIC-DATE", invalidDate);
            System.out.println("THẤT BẠI: Lẽ ra phải ném InvalidReturnDateException!");
        } catch (InvalidReturnDateException e) {
            System.out.println("BẮT ĐƯỢC NGOẠI LỆ: " + e.getMessage());
            System.out.printf("-> Chi tiết: Mã TB = %s, Ngày mượn = %s, Ngày trả nhập = %s\n",
                    e.getDeviceId(), e.getBorrowedDate(), e.getReturnedDate());
        } catch (BorrowingException e) {
            System.out.println("Bắt ngoại lệ BorrowingException: " + e.getMessage());
        } finally {
            BorrowItem item = slip.getItems().get(0);
            System.out.printf(
                    "FINALLY (Kiểm tra tính nhất quán): Mục mượn đã trả = %b, Thiết bị vẫn ở trạng thái: %s\n",
                    item.isReturned(), mic.getStatus());
        }
    }

    // =========================================================================
    // TIỆN ÍCH ĐỊNH DẠNG XUẤT CONSOLE
    // =========================================================================
    private static void printBanner(String title) {
        System.out.println("\n" + "=".repeat(75));
        System.out.println("   " + title);
        System.out.println("=".repeat(75));
    }

    private static void printSectionHeader(String sectionTitle) {
        System.out.println("\n" + "#".repeat(70));
        System.out.println("#  " + sectionTitle);
        System.out.println("#".repeat(70));
    }
}
