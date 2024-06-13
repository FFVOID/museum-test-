package com.museum.entity;

import java.util.ArrayList;
import java.util.List;

import com.museum.dto.NewItemDto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="item")
@Getter
@ToString
public class Item extends BaseEntity{
	
	//전시 식별자 아이디
	@Id
	@Column(name="item_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	//전시 이름
	@Column(nullable = false)
	private String itemNm;
	
	//전시 기간
	@Column(nullable = false)
	private String itemDate;
	
	//남은인원
	private Integer stock;
	
	//전시 상세설명
	@Lob
	@Column(nullable = false, columnDefinition = "longtext")
	private String itemDetail;
	
	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
	private List<ItemImg> itemImgs = new ArrayList<>();
	
	public Item(String itemNm, String itemDetail, String itemDate, Integer stock) {
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.itemDate = itemDate;
        this.stock = stock;
    }
	
	//수정된 전시 정보를 업데이트
	public void updateItem(NewItemDto newItemDto) {
		this.itemNm = newItemDto.getItemNm();
		this.itemDetail = newItemDto.getItemDetail();
		this.itemDate = newItemDto.getItemDate();
		this.stock = newItemDto.getStock();
	
	}
	
	public Item() {}
	
	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getStock() {
        return stock; // 기본값을 0으로 설정
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
	
	//예약관리
    public int newStock(int count) {
        if (stock >= count) {
            stock -= count;
        } else {
           
        }
        
        return stock;
    }
    
    public void updateStock(int count) {
    	this.stock = stock + count;
    }
	
	
}