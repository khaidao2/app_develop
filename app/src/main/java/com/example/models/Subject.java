package com.example.models;

/**
 * Một môn học trong chương trình đào tạo của một ngành.
 * Trường {@code distance} là điểm khoảng cách Euclid so với từ khoá tìm kiếm
 * hiện tại (0.0 = trùng khớp tuyệt đối). Đây là giá trị tạm thời, chỉ dùng để
 * sắp xếp/hiển thị trong lúc tìm kiếm nên không được lưu vào CSDL.
 */
public class Subject {
    private final String code;
    private final String name;
    private final String credits;
    private final String type;

    // Điểm khoảng cách Euclid với từ khoá tìm kiếm hiện tại (transient).
    private double distance = Double.MAX_VALUE;

    public Subject(String code, String name, String credits, String type) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCredits() {
        return credits;
    }

    public String getType() {
        return type;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * @return true nếu là môn bắt buộc, false nếu là môn tự chọn.
     */
    public boolean isCompulsory() {
        return type != null && type.toLowerCase().contains("bắt buộc");
    }
}
