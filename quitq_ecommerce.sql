-- =============================================================================
-- QuitQ E-Commerce Full-Stack Application Database Schema
-- Reference: QuitQ - Ecom Project Specification & Architecture Workflow
-- Compatible with MySQL 8.0+ / MySQL Workbench
-- =============================================================================

CREATE DATABASE IF NOT EXISTS quitq_db;
USE quitq_db;

-- -----------------------------------------------------------------------------
-- 1. Table: users (Authentication, Identity & Role-Based Access)
-- Flow 1: Registration & Login (Customer, Seller, Admin)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS order_tracking;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS shipping_address;
DROP TABLE IF EXISTS seller;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL COMMENT 'ROLE_CUSTOMER, ROLE_SELLER, ROLE_ADMIN',
    is_active BIT(1) NOT NULL DEFAULT b'1',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 2. Table: customer (Customer Profile Details)
-- Flow 1: Registration & Flow 2/3: Browse, Cart & Checkout
-- -----------------------------------------------------------------------------
CREATE TABLE customer (
    id INT PRIMARY KEY AUTO_INCREMENT,
    users_id INT NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(20),
    contact_number VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_users FOREIGN KEY (users_id) 
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3. Table: seller (Seller / Merchant Profile)
-- Flow 1: Registration & Flow 6/7: Seller Product & Order Processing
-- -----------------------------------------------------------------------------
CREATE TABLE seller (
    id INT PRIMARY KEY AUTO_INCREMENT,
    users_id INT NOT NULL UNIQUE,
    business_name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    city VARCHAR(100),
    address VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seller_users FOREIGN KEY (users_id) 
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 4. Table: shipping_address (Saved Delivery Addresses)
-- Flow 4 & 5: Enter / Select Shipping Address
-- -----------------------------------------------------------------------------
CREATE TABLE shipping_address (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    is_default TINYINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_shipping_address_customer FOREIGN KEY (customer_id) 
        REFERENCES customer (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 5. Table: category (Product Categories e.g. Electronics, Fashion, Mobile, etc.)
-- Flow 2: Browse & Pick Category & Flow 8: Admin Manage Categories
-- -----------------------------------------------------------------------------
CREATE TABLE category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    seq INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 6. Table: product (Catalog & Inventory)
-- Flow 2: Browse, Search, Brand/Price Filter & Flow 6: Seller Product Add/Edit
-- -----------------------------------------------------------------------------
CREATE TABLE product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT NOT NULL,
    seller_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(100),
    description MEDIUMTEXT,
    price DOUBLE NOT NULL,
    stock_qty INT NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'IN_STOCK' COMMENT 'IN_STOCK, OUT_OF_STOCK',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) 
        REFERENCES category (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_product_seller FOREIGN KEY (seller_id) 
        REFERENCES seller (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 7. Table: cart (Shopping Cart Header for Customer)
-- Flow 3: Cart Management & Live Total
-- -----------------------------------------------------------------------------
CREATE TABLE cart (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_customer FOREIGN KEY (customer_id) 
        REFERENCES customer (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 8. Table: cart_item (Items in Cart with Quantity & Snapshot Price)
-- Flow 3: Update Qty, Remove Item, Proceed
-- -----------------------------------------------------------------------------
CREATE TABLE cart_item (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DOUBLE NOT NULL,
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) 
        REFERENCES cart (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) 
        REFERENCES product (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 9. Table: orders (Customer Orders Header)
-- Flow 4 & 5: Confirm Order, Generate Order ID & Order Confirmation
-- -----------------------------------------------------------------------------
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(64) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    shipping_address_id INT NOT NULL,
    total_amount DOUBLE NOT NULL,
    order_status VARCHAR(30) NOT NULL DEFAULT 'PLACED' 
        COMMENT 'PLACED, PROCESSING, SHIPPED, DELIVERED, CANCELLED',
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) 
        REFERENCES customer (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_orders_shipping_address FOREIGN KEY (shipping_address_id) 
        REFERENCES shipping_address (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 10. Table: order_item (Order Line Items)
-- Flow 6 & 7: Seller Orders Received Screen & Status Forwarding
-- -----------------------------------------------------------------------------
CREATE TABLE order_item (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    seller_id INT NOT NULL,
    quantity INT NOT NULL,
    price_per_unit DOUBLE NOT NULL,
    total_price DOUBLE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PLACED',
    CONSTRAINT fk_order_item_orders FOREIGN KEY (order_id) 
        REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) 
        REFERENCES product (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_order_item_seller FOREIGN KEY (seller_id) 
        REFERENCES seller (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 11. Table: payment (Payment Processing & Records)
-- Flow 4 & 5: Choose Payment Method & Secure Processing
-- -----------------------------------------------------------------------------
CREATE TABLE payment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL UNIQUE,
    payment_method VARCHAR(50) NOT NULL COMMENT 'CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, COD',
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, COMPLETED, FAILED',
    transaction_id VARCHAR(100),
    amount DOUBLE NOT NULL,
    payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_orders FOREIGN KEY (order_id) 
        REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 12. Table: order_tracking (Timeline & Status History)
-- Flow 4 & 5: Status Timeline: Placed -> Processing -> Shipped -> Delivered
-- -----------------------------------------------------------------------------
CREATE TABLE order_tracking (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    status VARCHAR(50) NOT NULL COMMENT 'Placed, Processing, Shipped, Delivered',
    status_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remarks VARCHAR(255),
    CONSTRAINT fk_order_tracking_orders FOREIGN KEY (order_id) 
        REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- Triggers for Business Rules (Inventory & Order Lifecycle)
-- =============================================================================

-- Trigger 1: Re-check available stock before an order item is placed
DELIMITER $$
CREATE TRIGGER trg_check_stock_before_order
BEFORE INSERT ON order_item
FOR EACH ROW
BEGIN
    DECLARE v_stock INT;

    SELECT stock_qty INTO v_stock
    FROM product
    WHERE id = NEW.product_id;

    IF v_stock IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Product does not exist';
    ELSEIF v_stock < NEW.quantity THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Insufficient stock for product';
    END IF;
END$$
DELIMITER ;

-- Trigger 2: Automatically decrement stock and update status when order item is created
DELIMITER $$
CREATE TRIGGER trg_reduce_stock_after_order
AFTER INSERT ON order_item
FOR EACH ROW
BEGIN
    UPDATE product
    SET stock_qty = stock_qty - NEW.quantity,
        status = CASE WHEN (stock_qty - NEW.quantity) <= 0 THEN 'OUT_OF_STOCK' ELSE 'IN_STOCK' END
    WHERE id = NEW.product_id;
END$$
DELIMITER ;

-- Trigger 3: Automatically record order creation in order_tracking timeline
DELIMITER $$
CREATE TRIGGER trg_order_initial_tracking
AFTER INSERT ON orders
FOR EACH ROW
BEGIN
    INSERT INTO order_tracking (order_id, status, status_time, remarks)
    VALUES (NEW.id, NEW.order_status, NOW(), 'Order placed successfully');
END$$
DELIMITER ;

-- =============================================================================
-- Seed Data for Testing & Verification
-- =============================================================================

-- 1. Users
INSERT INTO users (id, username, email, password, role) VALUES
(1, 'admin', 'admin@quitq.com', '$2a$10$SS0PwJbooKCmTecFCWE0.ucG3KjR5m3rstUgT5OQ3zqEH7HxoVYd.', 'ROLE_ADMIN'),
(2, 'techstore', 'seller1@quitq.com', '$2a$10$LIY8Yjkr1W8Q.o7Dyfpu3ea9enS9cI7NJJLzirgyjltbFzFtSs.vu', 'ROLE_SELLER'),
(3, 'fashionhub', 'seller2@quitq.com', '$2a$10$W0Vnr2tJPbSdrWy/GwNj/.KKG.tiJCXyjfwy/WbkQ8M3jSCFVMTCu', 'ROLE_SELLER'),
(4, 'john_doe', 'john@example.com', '$2a$10$tKepZPeKGwdCX5iBmXAdHOQ7jRNjroh486ehi/m6P71ykUhBJuS1e', 'ROLE_CUSTOMER');

-- 2. Customer Profile
INSERT INTO customer (id, users_id, name, gender, contact_number) VALUES
(1, 4, 'John Doe', 'Male', '+91 9876543210');

-- 3. Seller Profiles
INSERT INTO seller (id, users_id, business_name, contact_number, city, address) VALUES
(1, 2, 'TechStore Electronics', '+91 9123456780', 'Bangalore', '12 Electronic City, Phase 1'),
(2, 3, 'FashionHub Retail', '+91 9123456781', 'Mumbai', '45 Linking Road, Bandra');

-- 4. Shipping Address
INSERT INTO shipping_address (id, customer_id, recipient_name, phone, street_address, city, state, postal_code, is_default) VALUES
(1, 1, 'John Doe', '+91 9876543210', 'Flat 402, Green Valley Apts', 'Chennai', 'Tamil Nadu', '600001', 1);

-- 5. Categories
INSERT INTO category (id, name, description, seq) VALUES
(1, 'Electronics', 'Laptops, Headphones, Smart devices and Accessories', 1),
(2, 'Mobile', 'Smartphones, Tablets and Mobile Accessories', 2),
(3, 'Fashion', 'Men, Women and Kids apparel and footwear', 3),
(4, 'Home & Furniture', 'Living room, Bedroom and Kitchen essentials', 4);

-- 6. Products
INSERT INTO product (id, category_id, seller_id, name, brand, description, price, stock_qty, image_url, status) VALUES
(1, 1, 1, 'Sony WH-1000XM5 Wireless Headphones', 'Sony', 'Industry Leading Noise Canceling with 2 Processors', 24999.00, 25, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSmRsKZhTjZ3TAT4aF2-mI3gYs2V0yq-PT62EJRRToT9A&s=10', 'IN_STOCK'),
(2, 1, 1, 'Dell XPS 15 Laptop', 'Dell', '15.6 inch OLED, Intel Core i7, 16GB RAM, 1TB SSD', 125000.00, 10, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjP6-vU9CKKNokP4ks9wqmsqQotCeR4Pj3ESiyg9W0Xg&s=10', 'IN_STOCK'),
(3, 2, 1, 'Apple iPhone 15 Pro 128GB', 'Apple', 'Titanium design, A17 Pro chip, 48MP main camera', 134900.00, 15, 'https://fdn2.gsmarena.com/vv/pics/apple/apple-iphone-15-pro-1.jpg', 'IN_STOCK'),
(4, 3, 2, 'Cotton Casual Slim Fit Shirt', 'Peter England', 'Pure breathable cotton shirt for everyday wear', 1499.00, 50, 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c', 'IN_STOCK'),
(5, 4, 2, 'Ergonomic Mesh Office Chair', 'Green Soul', 'High back ergonomic chair with lumbar support', 7999.00, 20, 'https://images.unsplash.com/photo-1688578735427-994ecdea3ea4', 'IN_STOCK'),
(9, 2, 1, 'Apple iPhone 17 Pro Max 256GB', 'Apple', 'Apple iPhone 17 Pro Max with a 6.9-inch Super Retina XDR display and Ceramic Shield protection. Powered by the A19 Pro chip with a 48MP Fusion Pro camera system and all-day battery life. 256GB storage option in Deep Blue.', 144900.00, 14, 'https://fdn2.gsmarena.com/vv/pics/apple/apple-iphone-17-pro-max-1.jpg', 'IN_STOCK'),
(10, 1, 1, 'Dell Inspiron 15 3511 Laptop', 'Dell', 'Dell Inspiron 15 3511 thin-and-light laptop with a 15.6-inch FHD display. Intel Core i5-1035G1 processor @ 1.00GHz, 8GB RAM (7.77GB usable), Intel UHD Graphics (128MB) and a 256GB SSD, with a 64-bit operating system (x64-based processor). No pen or touch input is available for this display.', 42990.00, 18, 'https://m.media-amazon.com/images/I/41LsSJQiwAL.jpg', 'IN_STOCK'),
(11, 2, 1, 'Samsung Galaxy S25 Ultra 5G 256GB', 'Samsung', 'Samsung Galaxy S25 Ultra 5G with 12GB RAM and 256GB storage, featuring a 6.9-inch QHD+ Dynamic AMOLED 2X 120Hz display and the Snapdragon 8 Elite for Galaxy processor. Pro-grade 200MP camera system with 5x optical zoom, Galaxy AI features and a built-in S Pen. 5000mAh battery with 45W fast charging.', 114999.00, 12, 'https://upload.wikimedia.org/wikipedia/commons/b/b0/Samsung_Galaxy_S25_Ultra.jpg', 'IN_STOCK'),
(12, 4, 2, 'Atomberg Renesa+ 1200mm BLDC Ceiling Fan with Remote', 'Atomberg', 'Atomberg Renesa+ 1200mm BLDC ceiling fan with remote control and LED speed indicators. The energy-efficient BLDC motor draws just 28W at speed 5 - up to 65% less than a conventional fan - and keeps running longer on inverter backup. Dust-resistant coating with a metallic finish and a compact motor design.', 4599.00, 25, 'https://atomberg.com/cdn/shop/files/brown2_1_1_45b12dbf-4ad6-4bed-b5fc-370ffe2515be.jpg', 'IN_STOCK'),
(13, 1, 1, 'LG 55UR7500PSC 55-inch 4K UHD Smart TV', 'LG', 'LG 139cm (55 inch) 4K UHD smart LED TV (model 55UR7500PSC) with an alpha5 AI Processor 4K Gen6 for sharp upscaling and dynamic tone mapping. Runs webOS with ThinQ AI and Wi-Fi built in, plus HDR10, FILMMAKER MODE and Game Optimizer with HGiG. Slim bezel design with 3 HDMI and 2 USB ports and 20W sound output.', 45080.00, 8, 'https://m.media-amazon.com/images/I/71PTegoIJGL._SL1500_.jpg', 'IN_STOCK');

-- 7. Cart
INSERT INTO cart (id, customer_id) VALUES
(1, 1);

-- 8. Cart Item
INSERT INTO cart_item (id, cart_id, product_id, quantity, price) VALUES
(1, 1, 1, 1, 24999.00);
