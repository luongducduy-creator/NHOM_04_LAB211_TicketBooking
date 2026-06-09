⚽ Football Match Ticket Booking System – Hệ thống Quản lý Đặt Vé Bóng Đá

Đồ án môn học LAB211 (OOP with Java) — Nhóm 3

🎯 1. RESEARCH QUESTION (Câu hỏi Nghiên cứu)

"Làm thế nào để xây dựng một hệ thống đặt vé bóng đá sử dụng kiến trúc MVC và lưu trữ dữ liệu bằng CSV, đảm bảo tính nhất quán của dữ liệu ghế ngồi, vé và giao dịch trong quá trình vận hành?"

Dự án này không chỉ nhằm mục đích xây dựng một hệ thống bán vé bóng đá đơn thuần mà còn tập trung vào việc quản lý ghế ngồi, giao dịch và dữ liệu trận đấu một cách hiệu quả. Hệ thống mô phỏng quy trình đặt vé thực tế tại các sân vận động, từ lựa chọn trận đấu, chọn ghế đến quản lý vé và doanh thu.

🚀 2. TÍNH NĂNG NỔI BẬT

* Quản lý trận đấu bóng đá.
* Quản lý sân vận động và ghế ngồi.
* Đặt vé và hủy vé.
* Quản lý người hâm mộ (Fan) và nhân viên (Staff).
* Theo dõi trạng thái ghế: Available, Reserved, Sold.
* Quản lý giao dịch mua vé.
* Thống kê doanh thu và số lượng vé đã bán.
* Lưu trữ và quản lý dữ liệu bằng File CSV.

⚙️ 3. KIẾN TRÚC & CÔNG NGHỆ

Kiến trúc: 100% chuẩn MVC (Model - View - Controller). Toàn bộ logic nghiệp vụ được xử lý tại Controller và Repository, giúp hệ thống dễ bảo trì và mở rộng.

Cấu trúc Dữ liệu: Lưu trữ thuần bằng File Text (CSV) cho các Entity: Fan, Staff, Stadium, Match, Seat, Ticket và Transaction.

Ngôn ngữ & Thư viện: Java Core (JDK 8+), sử dụng OOP, Collections Framework và File I/O. Không sử dụng thư viện bên thứ ba (Pure Java).

👨‍💻 4. THÀNH VIÊN NHÓM (GROUP 3)

| STT | Họ và Tên             | Vai trò                                 |
| --- | --------------------- | --------------------------------------- |
| 1   | Lương Đức Duy         | Dev A: Data & Model Lead                |
| 2   | Phan Văn Bảo          | Dev B: Repository & CSV Lead            |
| 3   | Võ Xuân Long          | Dev C: Controller & Business Logic Lead |
| 4   | Trương Huỳnh Anh Tuấn | Dev D: View & Integration Lead          |

