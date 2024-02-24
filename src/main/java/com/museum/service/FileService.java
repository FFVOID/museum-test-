package com.museum.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Service
@Log
public class FileService {
	//파일 업로드
	public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{
		UUID uuid = UUID.randomUUID();
		
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
		
		String savedFileName = uuid.toString() + extension;
		
		String fileUploadFullUrl = uploadPath + "/" + savedFileName;
		
		FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
		fos.write(fileData);
		fos.close();
		
		return savedFileName;
	}
	
	public void deleteFile(String filePath) throws Exception {
		File deleteFile = new File(filePath);
		
		if(deleteFile.exists()) { //해당경로에 파일이 있으면
			deleteFile.delete();
			log.info("파일을 삭제하였습니다"); //로그 기록을 저장
		} else {
			log.info("파일이 존재하지 않습니다");
		}
	}
}
