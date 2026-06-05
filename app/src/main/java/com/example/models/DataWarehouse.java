package com.example.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class DataWarehouse {
    private static ArrayList<Category> cachedCategories = null;
    private static ArrayList<Employee> cachedEmployees = null;
    private static ArrayList<Customer> cachedCustomers = null;
    private static ArrayList<Order> cachedOrders = null;
    private static ArrayList<Product> cachedProducts = null;
    private static ArrayList<OrderDetail> cachedOrderDetails = null;

    public static ArrayList<Category> getCategories() {
        if (cachedCategories != null) {
            return cachedCategories;
        }
        ArrayList<Category> categories = new ArrayList<>();

        Category c1 = new Category("C1", "Electronics", "Devices and Gadgets", "https://cdn-icons-png.flaticon.com/512/689/689307.png");
        c1.getProducts().add(new Product("P1", "Laptop Dell XPS", "DELL10", 1500.0, 10, "10%", "High performance laptop for work", "C1"));
        c1.getProducts().add(new Product("P2", "iPhone 15 Pro", "APPLE5", 1200.0, 15, "10%", "Latest iPhone from Apple", "C1"));
        c1.getProducts().add(new Product("P3", "Sony WH-1000XM5", "SONY20", 350.0, 30, "10%", "Top-tier noise cancelling headphones", "C1"));
        categories.add(c1);

        Category c2 = new Category("C2", "Fashion", "Clothing and Accessories", "https://cdn-icons-png.flaticon.com/512/3050/3050239.png");
        c2.getProducts().add(new Product("P4", "T-Shirt Uniqlo", "UNI10", 25.0, 100, "5%", "Comfortable cotton t-shirt", "C2"));
        c2.getProducts().add(new Product("P5", "Levi's 501 Jeans", "LEVIS", 80.0, 50, "5%", "Classic straight fit jeans", "C2"));
        c2.getProducts().add(new Product("P6", "Nike Windrunner", "NIKE15", 100.0, 40, "5%", "Lightweight running jacket", "C2"));
        categories.add(c2);

        Category c3 = new Category("C3", "Food", "Groceries and Snacks", "https://cdn-icons-png.flaticon.com/512/706/706164.png");
        c3.getProducts().add(new Product("P7", "Organic Apples", "FRESH", 5.0, 200, "0%", "Bag of fresh organic apples", "C3"));
        c3.getProducts().add(new Product("P8", "Whole Grain Bread", "BAKE", 3.0, 60, "0%", "Freshly baked whole grain bread", "C3"));
        c3.getProducts().add(new Product("P9", "Greek Yogurt", "DAIRY", 4.5, 80, "0%", "High protein greek yogurt", "C3"));
        categories.add(c3);

        cachedCategories = categories;
        return categories;
    }

    public static ArrayList<Employee> getEmployee() {
        if (cachedEmployees != null) {
            return cachedEmployees;
        }
        ArrayList<Employee> employees = new ArrayList<>();
        employees.add(new Employee("E1", "Nguyen Van A", "0901234567", 1995, "HCMC"));
        employees.add(new Employee("E2", "Tran Thi B", "0912345678", 1998, "Ha Noi"));
        employees.add(new Employee("E3", "Le Van C", "0923456789", 1992, "Da Nang"));
        employees.add(new Employee("E4", "Pham Thi D", "0934567890", 1997, "Can Tho"));
        employees.add(new Employee("E5", "Hoang Van E", "0945678901", 1994, "Hue"));
        employees.add(new Employee("E6", "Vu Thi F", "0956789012", 1996, "Hai Phong"));
        employees.add(new Employee("E7", "Dang Van G", "0967890123", 1993, "Binh Duong"));
        employees.add(new Employee("E8", "Bui Thi H", "0978901234", 1999, "Dong Nai"));
        employees.add(new Employee("E9", "Do Van I", "0989012345", 1991, "Quang Ninh"));
        employees.add(new Employee("E10", "Ngo Thi K", "0990123456", 2000, "Vung Tau"));
        cachedEmployees = employees;
        return employees;
    }

    public static ArrayList<Customer> getCustomers() {
        if (cachedCustomers != null) {
            return cachedCustomers;
        }
        ArrayList<Customer> customers = new ArrayList<>();
        String[] firstNames = {"Nguyen", "Tran", "Le", "Pham", "Hoang", "Vu", "Dang", "Bui", "Do", "Ngo"};
        String[] middleNames = {"Van", "Thi", "Minh", "Hoang", "Gia", "Duc", "An", "Phuong", "Ngoc", "Anh"};
        String[] lastNames = {"An", "Binh", "Chinh", "Dung", "Em", "Giang", "Hung", "Kiet", "Linh", "Minh"};
        String[] addresses = {"HCMC", "Ha Noi", "Da Nang", "Can Tho", "Hue", "Hai Phong", "Vung Tau", "Nha Trang", "Da Lat", "Binh Duong"};

        Calendar calendar = Calendar.getInstance();

        for (int i = 1; i <= 100; i++) {
            String cusID = "CUS" + String.format("%03d", i);
            // Sử dụng index i để chọn dữ liệu cố định thay vì Random
            int idx = i - 1;
            String name = firstNames[idx % firstNames.length] + " " +
                          middleNames[idx % middleNames.length] + " " +
                          lastNames[idx % lastNames.length];

            String phone = "09" + String.format("%08d", 10000000 + i);
            String email = "customer" + i + "@example.com";

            // Birthday cố định dựa trên i: 1960 + (0..50)
            int year = 1960 + (idx % 51);
            int month = idx % 12;
            int day = 1 + (idx % 28);
            calendar.set(year, month, day);
            Date birthday = calendar.getTime();

            String address = addresses[idx % addresses.length];

            customers.add(new Customer(cusID, name, phone, email, birthday, address));
        }
        cachedCustomers = customers;
        return customers;
    }
    public static ArrayList<Order> getOrders() {
        if (cachedOrders != null) {
            return cachedOrders;
        }
        ArrayList<Order> orders = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // Start date: 2023-01-01
        calendar.set(2023, 0, 1, 8, 0, 0);

        for (int i = 1; i <= 1000; i++) {
            String orderID = "ORD" + String.format("%04d", i);
            
            // Phân bổ khách hàng: lặp lại từ CUS001 đến CUS100
            String customerID = "CUS" + String.format("%03d", ((i - 1) % 100) + 1);
            
            // Phân bổ nhân viên: lặp lại từ E1 đến E10 để đảm bảo đồng đều (mỗi người 100 hóa đơn)
            String employeeID = "E" + (((i - 1) % 10) + 1);
            
            // Ngày hóa đơn: mỗi hóa đơn cách nhau 6 tiếng để trải dài trong năm
            calendar.add(Calendar.HOUR, 6);
            Date orderDate = calendar.getTime();
            
            OrderStatus status = OrderStatus.values()[1 + ((i - 1) % (OrderStatus.values().length - 1))];
            orders.add(new Order(orderID, customerID, employeeID, orderDate, status));
        }
        cachedOrders = orders;
        return orders;
    }

    public static ArrayList<Product> getProducts() {
        if (cachedProducts != null) {
            return cachedProducts;
        }
        ArrayList<Product> products = new ArrayList<>();
        for (Category category : getCategories()) {
            products.addAll(category.getProducts());
        }
        cachedProducts = products;
        return products;
    }

    public static ArrayList<OrderDetail> getOrderDetail() {
        if (cachedOrderDetails != null) {
            return cachedOrderDetails;
        }
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        ArrayList<Order> orders = getOrders();
        ArrayList<Product> products = getProducts();

        int detailCounter = 1;
        for (Order order : orders) {
            String orderID = order.getOrderID();
            
            // Lấy số từ mã đơn hàng (ví dụ: "ORD0001" -> 1) để tính toán cố định
            int orderNum = 1;
            try {
                orderNum = Integer.parseInt(orderID.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                // Bỏ qua nếu lỗi
            }

            // Mỗi đơn hàng có từ 1 đến 10 chi tiết đơn hàng (cố định)
            int numItems = (orderNum % 10) + 1;
            
            for (int j = 0; j < numItems; j++) {
                Product product = products.get((orderNum + j) % products.size());
                String orderDetailId = "OD" + String.format("%05d", detailCounter++);
                
                // Số lượng sản phẩm mua cố định từ 1 đến 5
                int qty = ((orderNum + j) % 5) + 1;
                double price = product.getPrice();
                
                // Phân tích tỉ lệ giảm giá từ mã coupon (ví dụ: "DELL10" -> 0.1)
                double couponVal = 0.0;
                String couponStr = product.getCoupon();
                if (couponStr != null) {
                    StringBuilder digits = new StringBuilder();
                    for (char c : couponStr.toCharArray()) {
                        if (Character.isDigit(c)) {
                            digits.append(c);
                        }
                    }
                    if (digits.length() > 0) {
                        couponVal = Double.parseDouble(digits.toString()) / 100.0;
                    }
                }
                
                // Phân tích tỉ lệ VAT (ví dụ: "10%" -> 0.1)
                double vatVal = 0.0;
                String vatStr = product.getVAT();
                if (vatStr != null) {
                    String cleanVat = vatStr.replace("%", "").trim();
                    if (!cleanVat.isEmpty()) {
                        vatVal = Double.parseDouble(cleanVat) / 100.0;
                    }
                }
                
                orderDetails.add(new OrderDetail(orderDetailId, orderID, product.getProductID(), qty, price, couponVal, vatVal));
            }
        }
        cachedOrderDetails = orderDetails;
        return orderDetails;
    }
}
