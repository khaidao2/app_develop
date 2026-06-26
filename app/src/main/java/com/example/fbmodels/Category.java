package com.example.fbmodels;

/**
 * Map node "categories/{id}": { categoryName, description }.
 * Firebase cần constructor rỗng + getter/setter public, tên trùng key JSON.
 */
public class Category {
    private String categoryName;
    private String description;

    public Category() {
    }

    public Category(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /** Dòng tóm tắt hiển thị trong ListView. */
    public String summary() {
        return categoryName + "\n" + description;
    }
}
