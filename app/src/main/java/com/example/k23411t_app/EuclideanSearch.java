package com.example.k23411t_app;

import java.text.Normalizer;

/**
 * Tìm kiếm "mờ" dựa trên khoảng cách Euclid giữa hai vector tần suất ký tự.
 *
 * <p>Ý tưởng: mỗi chuỗi được biểu diễn thành một vector đếm số lần xuất hiện của
 * từng ký tự (a-z, 0-9). Khoảng cách Euclid giữa vector của từ khoá và vector
 * của một đoạn văn bản cho biết hai chuỗi "khác nhau" bao nhiêu ký tự:
 * <ul>
 *   <li>Trùng khớp tuyệt đối → 0.0</li>
 *   <li>Thay 1 ký tự (mất 1, thêm 1) → sqrt(1² + 1²) = 1.41</li>
 *   <li>Thiếu/thừa 1 ký tự → 1.0</li>
 * </ul>
 *
 * <p>Vì tên môn học gồm nhiều từ, ta trượt một "cửa sổ" gồm các từ liên tiếp và
 * lấy khoảng cách nhỏ nhất, nhờ vậy từ khoá ngắn vẫn khớp tốt với cụm từ trong
 * tên môn (ví dụ "kinh te" khớp cụm "Kinh tế" → 0.0).
 *
 * <p>Dấu tiếng Việt được lược bỏ trước khi so khớp nên "Toán" và "toan" là tương
 * đương.
 */
public final class EuclideanSearch {

    // Bảng chữ cái: 26 chữ a-z + 10 số 0-9.
    private static final int DIM = 36;

    private EuclideanSearch() {
        // Lớp tiện ích, không cho khởi tạo.
    }

    /**
     * Tính khoảng cách Euclid nhỏ nhất giữa từ khoá và một cửa sổ từ trong văn bản.
     *
     * @param query từ khoá người dùng nhập
     * @param text  văn bản đích (ví dụ: tên môn học, có thể kèm mã môn)
     * @return khoảng cách (càng nhỏ càng khớp; 0.0 = khớp tuyệt đối).
     *         Trả về {@link Double#MAX_VALUE} nếu không so khớp được.
     */
    public static double distance(String query, String text) {
        if (query == null || query.trim().isEmpty()) {
            return 0.0;
        }
        String q = normalize(query).replaceAll("\\s+", "");
        if (q.isEmpty()) {
            return 0.0;
        }
        int[] qv = vector(q);

        String[] words = normalize(text).split("\\s+");
        double best = Double.MAX_VALUE;

        for (int i = 0; i < words.length; i++) {
            StringBuilder window = new StringBuilder();
            for (int j = i; j < words.length; j++) {
                window.append(words[j]);
                // Không nối thêm khi cửa sổ đã dài hơn hẳn từ khoá.
                if (window.length() > q.length() + 4) {
                    break;
                }
                double d = euclid(qv, vector(window.toString()));
                if (d < best) {
                    best = d;
                }
            }
        }
        return best;
    }

    /**
     * Chuẩn hoá chuỗi: bỏ dấu tiếng Việt, đưa về chữ thường, đổi 'đ' → 'd' và chỉ
     * giữ lại chữ cái/số/khoảng trắng.
     */
    private static String normalize(String s) {
        if (s == null) {
            return "";
        }
        String n = s.toLowerCase();
        n = Normalizer.normalize(n, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}+", "");   // bỏ các dấu thanh/dấu mũ
        n = n.replace('đ', 'd');
        n = n.replaceAll("[^a-z0-9\\s]", " ");
        return n.trim();
    }

    /**
     * Dựng vector tần suất ký tự (a-z → 0..25, 0-9 → 26..35).
     */
    private static int[] vector(String s) {
        int[] v = new int[DIM];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'z') {
                v[c - 'a']++;
            } else if (c >= '0' && c <= '9') {
                v[26 + (c - '0')]++;
            }
        }
        return v;
    }

    private static double euclid(int[] a, int[] b) {
        long sum = 0;
        for (int i = 0; i < DIM; i++) {
            int d = a[i] - b[i];
            sum += (long) d * d;
        }
        return Math.sqrt(sum);
    }
}
