package com.museum.test;

import com.museum.dto.NewItemDto;
import com.museum.entity.Item;
import com.museum.repository.ItemRepository;
import com.museum.service.ItemService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
@EnableCaching
public class CacheTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setup() {
        // 캐시를 초기화
        cacheManager.getCache("itemDetails").clear();
    }

    @Test
    public void testGetItemDtlCaching() {
        Long itemId = 1L;

        // Mock 데이터 설정
        Item mockItem = new Item();
        mockItem.setId(itemId);
        given(itemRepository.findById(anyLong())).willReturn(java.util.Optional.of(mockItem));

        // 첫 번째 호출 - 캐시 미스
        NewItemDto firstCall = itemService.getItemDtl(itemId);
        assertThat(firstCall).isNotNull();
        System.out.println("First call (cache miss): " + firstCall);

        // 두 번째 호출 - 캐시 히트
        NewItemDto secondCall = itemService.getItemDtl(itemId);
        assertThat(secondCall).isNotNull();
        System.out.println("Second call (cache hit): " + secondCall);

        // Repository 메서드 호출이 한 번만 발생했는지 확인
        Mockito.verify(itemRepository, Mockito.times(1)).findById(anyLong());
    }
}
