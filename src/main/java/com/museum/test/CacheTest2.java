package com.museum.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.museum.dto.NewItemDto;
import com.museum.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CacheTest2 {
	
	@Autowired
    private ItemService itemService;

    private Long itemId = 303L;

    @BeforeEach
    public void setUp() {
        //필요한 경우 초기 설정 => 아이템 2개로 테스트해본결과 캐시에 저장이 잘됐다
    }

    @Test
    public void testWithCache() {
        // 캐시를 사용하기 위해 첫 번째 호출
        itemService.getItemDtl(itemId);
        
        long totalDuration = 0;
        for (int i = 0; i < 1000; i++) {
            long startTime = System.currentTimeMillis();
            NewItemDto item = itemService.getItemDtl(itemId);
            long endTime = System.currentTimeMillis();
            totalDuration += (endTime - startTime);

            assertNotNull(item);
        }
        System.out.println("Cachetest 걸린시간: " + (totalDuration / 10) + " ms");
    }
}
