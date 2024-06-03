package com.museum.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.museum.dto.NewItemDto;
import com.museum.dto.ReservationDto;
import com.museum.dto.ReservedDto;
import com.museum.dto.ReservedHistDto;
import com.museum.entity.Item;
import com.museum.entity.Member;
import com.museum.entity.Reservation;
import com.museum.entity.Reserved;
import com.museum.redis.RedisLock;
import com.museum.repository.ItemRepository;
import com.museum.repository.MemberRepository;
import com.museum.repository.ReservedRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {
	
	private RedisLock redisLock = new RedisLock();
	
	private final ItemRepository itemRepository;
	private final MemberRepository memberRepository;
	private final ReservedRepository reservedRepository;
	
	//예약
	public Long reserved(ReservedDto reservedDto , String userId) {
		String lockKey = "lock:reservation:" + reservedDto.getItemId();
		String lockValue =  UUID.randomUUID().toString();
		
		//락을 이용해 동시성 제어
		if(redisLock.lock(lockKey, lockValue)) {
			try {
				
				//전시 조회
				Item item = itemRepository.findById(reservedDto.getItemId())
						.orElseThrow(EntityNotFoundException::new);
				
				//현재 로그인한 회원의 아이디와 회원 정보를 조회
				Member member = memberRepository.findByUserId(userId);
				
				//예약할 전시 엔티티(item)를 이용해 예약전시(reservation)엔티티를 작성 
				List<Reservation> reservationItemList = new ArrayList<>();
				
				if (item.getStock() <= 0) {
					throw new RuntimeException("예약 인원이 다차서 예약이 불가능합니다");
				}
				
				item.newStock(reservedDto.getCount());
				
				Reservation reservationItem = Reservation.createReservation(item, reservedDto.getReservedNm(),reservedDto.getDate(), reservedDto.getCount());
				reservationItemList.add(reservationItem);
				
				//회원 정보와 예약할 전시 리스트 정보를 이용하여 예약 엔티티를 생성
				Reserved reserved = Reserved.createReserved(member, reservationItemList);
				reservedRepository.save(reserved);
				
				return reserved.getId();
				
			} finally {
				redisLock.unlock(lockKey, lockValue); //항상 락을 해제 시켜야한다 데드락 방지(무한정 대기하는 블로킹 상태가 발생할수있다)
			}
		} else {
			throw new RuntimeException("예약을 위한 락획득 불가");
		}
		
	}
	
	//예약페이지에 전시목록 가져오기
	@Transactional(readOnly = true)
	public List<NewItemDto> getItemList(){
		
		List<Item> itemList = itemRepository.findAll();
		
		List<NewItemDto> itemDtoList = new ArrayList<>();
		
		for(Item itmes : itemList) {
			NewItemDto newItemDto = NewItemDto.of(itmes);
			
			itemDtoList.add(newItemDto);
		}
		
		return itemDtoList;
	}
	
	//예약목록 가져오기
	@Transactional(readOnly = true)
	public Page<ReservedHistDto> getReservedList(String userId, Pageable pageable) {
	    List<ReservedHistDto> reservedHistDtoList = reservedRepository.findReserveds(userId, pageable)
	            .stream()
	            .map(reserved -> {
	                ReservedHistDto reservedHistDto = new ReservedHistDto(reserved);
	                List<ReservationDto> reservationDtoList = reserved.getReservationList()
	                        .stream()
	                        .map(ReservationDto::new) 
	                        .collect(Collectors.toList());
	                reservedHistDto.setReservationDtoList(reservationDtoList);
	                return reservedHistDto;
	            })
	            .collect(Collectors.toList());

	    Long totalCount = reservedRepository.countReserveds(userId);

	    return new PageImpl<>(reservedHistDtoList, pageable, totalCount);
	}
	
	//예약 정보(상세내역용) 가져오기 
	@Transactional(readOnly = true)
	public List<ReservedHistDto> getReservedHistDtoList(Long reservedId){
		
		List<ReservedHistDto> reservedHistDtoList = reservedRepository.findReservedId(reservedId)
				.stream()
				.map(reserved -> {
					ReservedHistDto reservedHistDto = new ReservedHistDto(reserved);
					List<ReservationDto> reservationDtoList = reserved.getReservationList()
						.stream()
						.map(ReservationDto::new)
						.collect(Collectors.toList());
					reservedHistDto.setReservationDtoList(reservationDtoList);
					return reservedHistDto;
					
				})
				.collect(Collectors.toList());
		
		return reservedHistDtoList;
	}
	
	
	//수정페이지에 수정할 예약 정보가져오기
	@Transactional(readOnly = true)
	public ReservedDto getReservedItem(Long reservedId){
		
		Reservation reservation = reservedRepository.findReservation(reservedId);
		
		ReservedDto reservedDto = ReservedDto.of(reservation);
		
		return reservedDto;
	}
	
	//예약 수정
	@Transactional
	public Long updateReserved(ReservedHistDto reservedHistDto ,ReservedDto reservedDto) throws Exception {
		
		Reservation reservation = reservedRepository.findReservation(reservedHistDto.getReservedId());
		
		int preCount = reservation.getCount();
	    int currentCount = reservedDto.getCount();
	    int reCount = currentCount - preCount;
		
	    Item item = reservation.getItem();
	    int Stock = item.getStock();
	    if (Stock < 0) {
	        throw new RuntimeException("예약이 불가능합니다");
	    }
	    
	    item.newStock(reCount);
	    
	    itemRepository.save(item);
	    
		reservation.updateReservation(reservedDto);
		
		return reservation.getId();
		
	}
	
	//예약취소
	public void deleteReservation(Long reservedId) {
		Reserved reserved = reservedRepository.findById(reservedId)
											.orElseThrow(EntityNotFoundException::new);
		
		Reservation reservation = reservedRepository.findReservation(reservedId);
		
		Item item = reservation.getItem();
		
		int preCount = reservation.getCount();
	    
	    item.updateStock(preCount);
		
		itemRepository.save(item);
		
		reservedRepository.delete(reserved);
	}
	
}
