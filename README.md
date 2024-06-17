
<div>
  <h2> 프로젝트 소개</h2>
	스프링 부트(MVC패턴) + JPA + Mysql + api를 이용한 박물관 사이트
</div>

<div align="left">
  <h2>사용기술</h2>
	<h4>통합개발환경(IDE) : <img src="https://img.shields.io/badge/Eclipse IDE-2C2255?style=flat&logo=Eclipse IDE&logoColor=white" /></h4> 
	<h4>프로그래밍 언어 : <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Java&logoColor=white" /></h4> 
	<h4>웹 프레임워크 : <img src="https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=Spring&logoColor=white" /></h4> 
	<h4>웹 기술 : <img src="https://img.shields.io/badge/HTML5-E34F26?style=flat&logo=HTML5&logoColor=white" />
		<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=JavaScript&logoColor=white" />
	 	<img src="https://img.shields.io/badge/jQuery-0769AD?style=flat&logo=jQuery&logoColor=white" /></h4> 
	<h4>프론트 : <img src="https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=CSS3&logoColor=white" />
  		<img src="https://img.shields.io/badge/Bootstrap-7952B3?style=flat&logo=Bootstrap&logoColor=white" />
  		<img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=flat&logo=Thymeleaf&logoColor=white" /></h4> 
	<h4>데이터베이스 :  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=MySQL&logoColor=white" /></h4>
	<h4>클라우드서비스 : <img src="https://img.shields.io/badge/AWS-232F3E?style=flat&logo=Amazon AWS&logoColor=white" /></h4>
</div>

<div>
  <h2>주요기능</h2>
	  <P>1. 회원가입(html폼을 이용한 회원가입 + 중복가입을 막기위해 이메일 인증을 활용)</P>
	  <P>2. 카카오api(OAuth2)를 이용한 회원가입</P>
	  <P>3. 전시등록,관리 기능(관리자용)</P>
	  <P>4. 전시예약,수정,취소 기능 - 예약기능에 redis lock 적용</P>
	  <P>5. 전시상세정보,리스트,검색 기능 - 상세정보 페이지에 redis cache 적용 </P>
	  <P>6. 게시판(CRUD) 글쓰기,수정,삭제 기능(이미지포함)</P>
	  <p>7. 박물관API를 이용한 검색기능(검색을 통해 소장품 정보를 제공)</p>
	  <P>8. 회원탈퇴</P>
</div>

### ERD 설계도
![ERD](https://github.com/FFVOID/museum-test-/assets/130435247/8af0c471-7a84-4509-ba0b-4537eb558559)

### 유스케이스 설계도
![유스케이스](https://github.com/FFVOID/museum-test-/assets/130435247/ee81cb9a-e422-4615-bcf9-efbb0870ec0a)

### 회원가입(유저는 이메일, 비밀번호를 입력하여 가입)
![회원가입1](https://github.com/FFVOID/museum-test-/assets/130435247/6b8c7297-fcce-4426-891e-69e1abe8a006)

### API를 이용한 회원가입(카카오 로그인을 통해 간편가입)
![회원가입2](https://github.com/FFVOID/museum-test-/assets/130435247/cb45e48b-a2f1-419c-9793-8b7557479a68)

### 전시등록,관리(관리자 기능)
![전시관리](https://github.com/FFVOID/museum-test-/assets/130435247/71edef49-dfc9-42ab-a962-5c0e8197bc8c)

### 전시예약,취소,수정
![전시예약,취소,수정](https://github.com/FFVOID/museum-test-/assets/130435247/65492a0d-e1cd-4380-af40-4832a4edf994)

###  전시리스트,검색
![전시리스트,검색](https://github.com/FFVOID/museum-test-/assets/130435247/7aa4dfc5-f55f-423c-885d-6a3e7a8201cd)

### 게시판 글쓰기,수정,삭제 기능(이미지,댓글포함)
![게시판글쓰기,수정,삭제(이미지포함,댓글)](https://github.com/FFVOID/museum-test-/assets/130435247/3486d335-1efc-4af0-940c-69bdab1e37f3)

### 검색 기능
![검색](https://github.com/FFVOID/museum-test-/assets/130435247/4a5b5a52-8154-4f47-800c-f28a1d74a552)

### 회원탈퇴
![회원탈퇴](https://github.com/FFVOID/museum-test-/assets/130435247/08d7063c-1e10-4584-a3b7-a590573aa262)


************

<h2>프로젝트 문제해결</h2>

 + Redis 분산락 적용을 통해 동시성 문제 해결

```ruby
public Long reserved(ReservedDto reservedDto , String userId) {
		String lockKey = "lock:reservation:" + reservedDto.getItemId();
		String lockValue =  UUID.randomUUID().toString();
		
		//락을 이용해 동시성 제어
		if(redisLock.lock(lockKey, lockValue)) {
			try {
				Item item = itemRepository.findById(reservedDto.getItemId())
						.orElseThrow(EntityNotFoundException::new);
				Member member = memberRepository.findByUserId(userId);
				List<Reservation> reservationItemList = new ArrayList<>();

				if (item.getStock() <= 0) {
					throw new RuntimeException("예약 인원이 다차서 예약이 불가능합니다");
				}
				item.newStock(reservedDto.getCount());
				
				Reservation reservationItem = Reservation.createReservation(item, reservedDto.getReservedNm(),reservedDto.getDate(), reservedDto.getCount());
				reservationItemList.add(reservationItem);
				
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
```

 + 테스트 결과 사진
	
   ![동시성테스트(레디스락)](https://github.com/FFVOID/museum-test-/assets/130435247/eca26cb4-a699-46ef-86c7-1ff534e5c2ce)

***************

+ Redis 캐시를 이용한 조회 성능 향상 시도

```ruby
@Cacheable(value = "itemDetails", key = "#itemId")
	public NewItemDto getItemDtl(Long itemId) {
		
		List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
		
		List<ItemImgDto> itemImgDtoList = new ArrayList<>();
		
		for(ItemImg itemImg : itemImgList) {
			ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
			
			itemImgDtoList.add(itemImgDto);
		}
		
		Item item = itemRepository.findById(itemId)
								  .orElseThrow(EntityNotFoundException::new);
		
		NewItemDto newItemDto = NewItemDto.of(item);
		
		newItemDto.setItemImgDtoList(itemImgDtoList);
		
		return newItemDto;
	}
```

+ 테스트 결과 사진

레디스 서버에 캐시가 잘 저장되었습니다.
![test3(redis-cli)키확인](https://github.com/FFVOID/museum-test-/assets/130435247/b4987d4b-f0d1-42b1-ae66-0293c0e1e5f1)

캐시 사용 전 평균 테스트 시간: 413.72 ms</br>
캐시 사용 후 평균 테스트 시간: 135.97 ms

413.72ms → 135.97ms 약 60%의 성능을 향상 시켰습니다.

![ntest1](https://github.com/FFVOID/museum-test-/assets/130435247/19d6ef3d-6024-4dcb-be99-a844cc9845a1)
![ntest2](https://github.com/FFVOID/museum-test-/assets/130435247/795c3c89-6498-46a8-b6f6-78dcf1f2ef31)
