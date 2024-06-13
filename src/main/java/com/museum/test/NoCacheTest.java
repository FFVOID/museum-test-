package com.museum.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.museum.dto.NewItemDto;
import com.museum.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NoCacheTest {

    @Autowired
    private ItemService itemService;

    private Long itemId = 352L;

    @Test
    public void testWithoutCache() {
        long totalDuration = 0;
        for (int i = 0; i < 1000; i++) {
            //캐시 비활성화
            itemService.evictAllCaches();

            long startTime = System.currentTimeMillis();
            NewItemDto item = itemService.getItemDtl(itemId);
            long endTime = System.currentTimeMillis();
            totalDuration += (endTime - startTime);

            assertNotNull(item);
        }
        System.out.println("NoCachetest 걸린시간: " + (totalDuration / 10) + " ms");
    }
}