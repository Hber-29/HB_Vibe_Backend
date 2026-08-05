# HB_Vibe_Backend
### 🔄 Cập nhật cấu hình Kong Gateway (DB-less Mode)

Trong chế độ không sử dụng Database, Kong sẽ không tự động nhận diện những thay đổi mới. 
Mỗi khi bạn chỉnh sửa hoặc thêm mới Route/Service trong file `kong.yml`, hãy chạy lệnh sau để ép Kong nạp lại cấu hình:

```bash
docker compose restart kong
