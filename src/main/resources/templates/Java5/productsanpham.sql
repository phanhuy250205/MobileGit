CREATE TABLE san_pham (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ten_san_pham NVARCHAR(255) NOT NULL,
    mo_ta NVARCHAR(MAX),
    gia DECIMAL(10,2) NOT NULL,
    so_luong INT NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    anh NVARCHAR(MAX),
    phan_loai_id BIGINT FOREIGN KEY REFERENCES phan_loai_hang(id),
    giam_gia DECIMAL(5,2) DEFAULT 0
);

CREATE TABLE khach_hang (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ten_khach_hang NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) UNIQUE NOT NULL,
    so_dien_thoai NVARCHAR(50),
    dia_chi NVARCHAR(MAX),
    anh NVARCHAR(MAX)
);

CREATE TABLE don_hang (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    khach_hang_id BIGINT FOREIGN KEY REFERENCES khach_hang(id),
    ngay_dat DATETIME DEFAULT GETDATE(),
    tong_tien DECIMAL(10,2) NOT NULL
);

CREATE TABLE chi_tiet_don_hang (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    don_hang_id BIGINT FOREIGN KEY REFERENCES don_hang(id),
    san_pham_id BIGINT FOREIGN KEY REFERENCES san_pham(id),
    so_luong INT NOT NULL,
    gia DECIMAL(10,2) NOT NULL
);

CREATE TABLE danh_gia (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    san_pham_id BIGINT FOREIGN KEY REFERENCES san_pham(id),
    khach_hang_id BIGINT FOREIGN KEY REFERENCES khach_hang(id),
    noi_dung NVARCHAR(MAX) NOT NULL,
    danh_gia INT CHECK (danh_gia >= 1 AND danh_gia <= 5),
    ngay_danh_gia DATETIME DEFAULT GETDATE()
);

CREATE TABLE hoa_don (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    khach_hang_id BIGINT FOREIGN KEY REFERENCES khach_hang(id),
    ngay_lap DATETIME DEFAULT GETDATE(),
    tong_tien DECIMAL(10,2) NOT NULL,
    nhan_vien_id BIGINT FOREIGN KEY REFERENCES nhan_vien(id)
);

CREATE TABLE chi_tiet_hoa_don (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    hoa_don_id BIGINT FOREIGN KEY REFERENCES hoa_don(id),
    san_pham_id BIGINT FOREIGN KEY REFERENCES san_pham(id),
    so_luong INT NOT NULL,
    gia DECIMAL(10,2) NOT NULL
);

CREATE TABLE nhan_vien (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ten_nhan_vien NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) UNIQUE NOT NULL,
    so_dien_thoai NVARCHAR(50),
    dia_chi NVARCHAR(MAX),
    ngay_tao DATETIME DEFAULT GETDATE(),
    chuc_vu_id BIGINT FOREIGN KEY REFERENCES phan_loai_chuc_vu(id)
);

CREATE TABLE phan_loai_hang (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ten_phan_loai NVARCHAR(255) NOT NULL,
    mo_ta NVARCHAR(MAX)
);

CREATE TABLE phan_loai_chuc_vu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ten_chuc_vu NVARCHAR(255) NOT NULL,
    mo_ta NVARCHAR(MAX)
);
ALTER TABLE san_pham 
ADD thuong_hieu NVARCHAR(255),
    model NVARCHAR(255),
    cau_hinh NVARCHAR(MAX),
    trang_thai NVARCHAR(50) DEFAULT 'Còn hàng'; -- Trạng thái sản phẩm (Còn hàng, Hết hàng, Ngừng kinh doanh)


	select * from san_pham



	-- Insert sample data into phan_loai_hang (Product Categories)
INSERT INTO phan_loai_hang (ten_phan_loai, mo_ta) VALUES
('Smartphones', 'Mobile phones with advanced features.'),
('Laptops', 'Portable computers with high performance.'),
('Headphones', 'Audio devices for listening to music or calls.');

-- Insert sample data into phan_loai_chuc_vu (Employee Roles)
INSERT INTO phan_loai_chuc_vu (ten_chuc_vu, mo_ta) VALUES
('Sales Associate', 'Responsible for assisting customers and selling products.'),
('Admin', 'Oversees daily operations and manages staff.'),
('Nhanvien', 'Handles technical issues and product repairs.');

-- Insert sample data into san_pham (Products)
INSERT INTO san_pham (ten_san_pham, mo_ta, gia, so_luong, anh, phan_loai_id, giam_gia, thuong_hieu, model, cau_hinh, trang_thai) VALUES
('Smartphone XYZ', 'A high-end smartphone with a 6.5-inch display, 128GB storage, and 48MP camera.', 15000.00, 100, 'image_link_1.jpg', 1, 10, 'XYZ Corp', 'XYZ123', '6.5-inch display, 128GB storage, 48MP camera', 'Còn hàng'),
('Laptop ABC', 'A powerful laptop with Intel i7 processor, 16GB RAM, and 512GB SSD.', 25000.00, 50, 'image_link_2.jpg', 2, 15, 'ABC Tech', 'ABC456', 'Intel i7, 16GB RAM, 512GB SSD', 'Còn hàng'),
('Headphones PQR', 'Noise-cancelling headphones with 20 hours of battery life and Bluetooth connectivity.', 2000.00, 200, 'image_link_3.jpg', 3, 5, 'PQR Audio', 'PQR789', 'Noise-cancelling, Bluetooth', 'Còn hàng');

-- Insert sample data into khach_hang (Customers)
INSERT INTO khach_hang (ten_khach_hang, email, so_dien_thoai, dia_chi, anh) VALUES
('Nguyen Viet Hoang', 'hoang.nguyen@email.com', '0987654321', 'Hanoi, Vietnam', 'customer_image_1.jpg'),
('Tran Minh Thu', 'minhthu@email.com', '0912345678', 'Hanoi, Vietnam', 'customer_image_2.jpg'),
('Pham Hoang Nam', 'hoangnam@email.com', '0923456789', 'Ho Chi Minh City, Vietnam', 'customer_image_3.jpg');

-- Insert sample data into don_hang (Orders)
INSERT INTO don_hang (khach_hang_id, ngay_dat, tong_tien) VALUES
(1, GETDATE(), 20000.00),
(2, GETDATE(), 30000.00),
(3, GETDATE(), 5000.00);

-- Insert sample data into chi_tiet_don_hang (Order Details)
INSERT INTO chi_tiet_don_hang (don_hang_id, san_pham_id, so_luong, gia) VALUES
(1, 1, 1, 15000.00),
(2, 2, 1, 25000.00),
(3, 3, 2, 2000.00);

-- Insert sample data into danh_gia (Product Reviews)
INSERT INTO danh_gia (san_pham_id, khach_hang_id, noi_dung, danh_gia, ngay_danh_gia) VALUES
(1, 1, 'Excellent smartphone with great performance!', 5, GETDATE()),
(2, 2, 'Powerful laptop, but a bit expensive.', 4, GETDATE()),
(3, 3, 'Good sound quality, but a bit bulky.', 3, GETDATE());

-- Insert sample data into nhan_vien (Employees)
INSERT INTO nhan_vien (ten_nhan_vien, email, so_dien_thoai, dia_chi, chuc_vu_id) VALUES
('Nguyen Thi Lan', 'lan.nguyen@email.com', '0945678910', 'Hanoi, Vietnam', 1),
('Pham Minh Tu', 'minhtu@email.com', '0938765432', 'Ho Chi Minh City, Vietnam', 2),
('Hoang Anh Tu', 'anh.tu@email.com', '0912345678', 'Da Nang, Vietnam', 3);

-- Insert sample data into hoa_don (Invoices)
INSERT INTO hoa_don (khach_hang_id, tong_tien, nhan_vien_id) VALUES
(1, 20000.00, 1),
(2, 30000.00, 2),
(3, 5000.00, 3);

-- Insert sample data into chi_tiet_hoa_don (Invoice Details)
INSERT INTO chi_tiet_hoa_don (hoa_don_id, san_pham_id, so_luong, gia) VALUES
(1, 1, 1, 15000.00),
(2, 2, 1, 25000.00),
(3, 3, 2, 2000.00);
