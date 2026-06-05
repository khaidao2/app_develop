package com.example.k23411t_app;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.models.DataWarehouse;
import com.example.models.OrderDetail;
import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetOrderDetails() {
        ArrayList<OrderDetail> details = DataWarehouse.getOrderDetail();
        assertNotNull(details);
        assertFalse(details.isEmpty());
        
        // Print first 5 details for visual verification
        for (int i = 0; i < Math.min(5, details.size()); i++) {
            System.out.println(details.get(i));
        }
        
        // Check structure
        OrderDetail first = details.get(0);
        assertEquals("OD00001", first.getOrderDetailId());
        assertEquals("ORD0001", first.getOrderID());
    }
}