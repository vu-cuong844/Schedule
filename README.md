# 📅 Schedule Project

Đây là dự án thiết kế hệ thống **microservice** cho bài toán **lập thời khóa biểu cho hệ tín chỉ**. Hệ thống cung cấp chức năng:

- Lập lịch giảng dạy cho phòng đào tạo
- Xem lịch cho giảng viên hoặc giáo viên

---

## 🎥 Video Demo

[📹 Xem demo trên Google Drive](https://drive.google.com/file/d/1iW8o2BGfQ0uHuMs9WwFCVBnW0owFO7AD/view?usp=drive_link)

---

## 📚 Mục lục

- [Cài đặt](#cài-đặt)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Hướng dẫn sử dụng](#hướng-dẫn-sử-dụng)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)

---

## ⚙️ Cài đặt

Dự án bao gồm 4 microservice:

- `eureka` — Service Registry
- `auth` — Authentication & Authorization Service
- `timetabling` — Business Logic Service
- `gateway` — API Gateway

### Yêu cầu môi trường

- JDK 17 trở lên  
- Maven 3.8+  
- Git  
- NodeJS + npm  

### Bước 1: Clone dự án

```bash
git clone https://github.com/vu-cuong844/Schedule.git
cd Schedule
```
Khởi tạo database:
- Mở MySQL Workbend
- File > Open SQL Script...
- Chọn file init.sql từ thư mục dự án: .\mysql\init\int.sql

Cấu hình username và password cho database:
- vào file .env.example
- Sủa lại 3 trường: 
    MYSQL_ROOT_USERNAME=your_mysql_username
    MYSQL_ROOT_PASSWORD=your_mysql_password
    MYSQL_DATABASE=your_database_name


## 📁 Cấu trúc dự án

```bash
Schedule/
├── authentication/            # Service xác thực & phân quyền
├── gateway/         # API Gateway
├── eureka/          # Service Registry
├── timetabling/     # Service xử lý lập lịch
├── my-app/          # Frontend (React)
├── mysql/init/      # File SQL khởi tạo CSDL
└── .env.example     # Mẫu file môi trường
```


## Hướng dẫn sử dụng

Chạy các service theo thứ tự:
```bash
cd my-app
npm install
cd ..\eureka
/.mvnw spring-boot:run
cd ..\authentication
/.mvnw spring-boot:run
cd ..\timetabling
/.mvnw spring-boot:run
cd ..\gateway
/.mvnw spring-boot:run
```

Kiểm tra: http://localhost:8761, xem đã có 3 dịch vụ đăng ký chưa. Nếu chưa đợi tầm 60s để các dịch vụ đăng ký.

```bash
cd ..\my-app
npm start
```
Kiểm tra: http://localhost:3000.

Login với vai trò PDT:
- username: banhabang@example.com
- password: SamplePass123

Login với vai trò TEACHER:
- username: anhbuithimai@example.com
- password: SamplePass123


## 🧰 Công nghệ sử dụng

### 💻 Backend – Microservices (Java Spring Boot)

| Service     | Mô tả ngắn                          | Công nghệ sử dụng                         |
|-------------|-------------------------------------|-------------------------------------------|
| `auth`      | Xác thực, phân quyền người dùng     | Spring Boot, Spring Security, JWT         |
| `timetabling`  | Quản lý lập lịch, xem lịch  | Spring Boot, GG-Ortools             |
| `gateway`   | Giao tiếp client ↔ service nội bộ   | Spring Cloud Gateway                      |
| `eureka`    | Service Discovery (đăng ký & tìm kiếm) | Spring Cloud Netflix Eureka           |

**Các thư viện & công nghệ bổ trợ:**

- Spring Web, Spring Data JPA
- MySQL
- Lombok
- Hỗ trợ biến môi trường qua `.env`

---

### 🖥️ Frontend 

- ReactJS (hoặc Vue / Angular tùy dự án)
- [`axios`](https://www.npmjs.com/package/axios) – Gửi request HTTP
- [`xlsx`](https://www.npmjs.com/package/xlsx) – Đọc và ghi file Excel
- [`file-saver`](https://www.npmjs.com/package/file-saver) – Lưu file từ trình duyệt
---

### 🔧 DevOps & Công cụ

- Git, GitHub
- MySQL Workbench – Quản lý cơ sở dữ liệu
- Postman – Kiểm thử API
- Maven – Quản lý dependencies
- dotenv – Quản lý biến môi trường (`.env`)

---

> 📌 **Lưu ý:** File `.env.example` đã có sẵn trong repo, cần copy thành `.env` và chỉnh sửa thông tin kết nối DB trước khi chạy ứng dụng.




