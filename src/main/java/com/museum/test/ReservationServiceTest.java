package com.museum.test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.museum.dto.ReservedDto;
import com.museum.entity.Item;
import com.museum.entity.Member;
import com.museum.entity.Reserved;
import com.museum.repository.ItemRepository;
import com.museum.repository.MemberRepository;
import com.museum.repository.ReservedRepository;
import com.museum.service.ReservationService;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
	
 	@InjectMocks
    private ReservationService reservationService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReservedRepository reservedRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReserved() {
        // Given
        ReservedDto reservedDto = new ReservedDto();
        reservedDto.setItemId(1L);
        reservedDto.setCount(1);
        reservedDto.setReservedNm("Test Reservation");
        
        String dateString = "2024-05-01";
        LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        reservedDto.setDate(localDate);

        Item item = new Item();
        item.setId(1L);
        item.setStock(10);

        Member member = new Member();
        member.setUserId("testUser");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(memberRepository.findByUserId("testUser")).thenReturn(member);

        // When
        Long reservationId = reservationService.reserved(reservedDto, "testUser");

        // Then
        assertNotNull(reservationId);
        verify(reservedRepository, times(1)).save(any(Reserved.class));
        
        System.out.println("성공");
    }

}
