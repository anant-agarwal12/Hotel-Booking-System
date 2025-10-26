CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

CREATE TABLE IF NOT EXISTS booking (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    room_type VARCHAR(50),
    check_in DATE,
    check_out DATE,
    price_per_night DOUBLE,
    extra_charges DOUBLE,
    total_amount DOUBLE
);
