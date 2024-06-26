package com.museum.dto;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.museum.entity.Item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class NewItemDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	@NotBlank(message = "전시명은 필수 입력 값입니다")
	private String itemNm;
	
	@NotBlank(message = "전시설명은 필수 입력 값입니다")
	private String itemDetail;
	
	@NotBlank(message = "전시날짜는 필수 입력값입니다")
	private String itemDate;
	
	@NotNull(message = "재고는 필수 입력 값입니다")
    private Integer stock = 100;
	
	//전시소장품 이미지 정보를 저장 
	private List<ItemImgDto> itemImgDtoList = new ArrayList<>();
	
	//전시소장품 이미지 아이디를 저장 -> 수정시에 이미지 아이디를 담아둘 용도
	private List<Long> itemImgIds = new ArrayList<>();
	
	private static ModelMapper modelMapper = new ModelMapper();
		
	public Item toItem() {
	    return new Item(this.itemNm, this.itemDetail, this.itemDate, this.stock);
	}
	
	//엔티티를 dto로
	public static NewItemDto of (Item item) {
		return modelMapper.map(item, NewItemDto.class);
	}
	
}