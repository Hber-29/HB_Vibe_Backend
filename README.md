# HB_Vibe_Backend
### 🔄 Cập nhật cấu hình Kong Gateway (DB-less Mode)

Trong chế độ không sử dụng Database, Kong sẽ không tự động nhận diện những thay đổi mới. 
Mỗi khi bạn chỉnh sửa hoặc thêm mới Route/Service trong file `kong.yml`, hãy chạy lệnh sau để ép Kong nạp lại cấu hình:

```bash
docker compose restart kong
### 🐞 Xem nhật ký (Logs) của Kong Gateway

Sử dụng lệnh này để kiểm tra xem Kong đã nạp file cấu hình `kong.yml` thành công chưa, hoặc để debug nguyên nhân khi hệ thống báo lỗi (ví dụ: lỗi sai dấu cách/thụt lề trong file YAML).

```bash
docker logs hb_kong_gateway
