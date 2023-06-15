package com.ssafy.enjoytrip.hotplace.model.service;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ssafy.enjoytrip.board.model.mapper.BoardMapper;
import com.ssafy.enjoytrip.hotplace.model.HotplaceDto;
import com.ssafy.enjoytrip.hotplace.model.HotplaceFileDto;
import com.ssafy.enjoytrip.hotplace.model.HotplaceScrapDto;
import com.ssafy.enjoytrip.hotplace.model.mapper.HotplaceMapper;
import com.ssafy.enjoytrip.util.SizeConstant;

@Service("HotplaceServiceImpl")
public class HotplaceServiceImpl implements HotplaceService{
	
	private HotplaceMapper hotplaceMapper;
	
	public HotplaceServiceImpl(HotplaceMapper hotplaceMapper) {
		super();
		this.hotplaceMapper = hotplaceMapper;
	}

	@Override
	public void writeArticle(HotplaceDto hotplaceDto) throws Exception {
		System.out.println("serviceImpl >>>>>>>>>>>>" + hotplaceDto);
		hotplaceMapper.writeArticle(hotplaceDto);
		List<HotplaceFileDto> fileInfos = hotplaceDto.getFileInfos();
		System.out.println("serviceImpl >>>>>>>>>>>>" + fileInfos);
		if(fileInfos != null && !fileInfos.isEmpty()) {
			hotplaceMapper.registerFile(hotplaceDto);
		}
	}

	@Override
	public List<HotplaceDto> listArticle(Map<String, String> map) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		return hotplaceMapper.listArticle(param);
	}

	@Override
	public HotplaceDto getArticle(int articleno) throws Exception {
		return hotplaceMapper.getArticle(articleno);
	}

	@Override
	public void modifyArticle(HotplaceDto hotplaceDto) throws Exception {
		hotplaceMapper.modifyArticle(hotplaceDto);
	}

	@Override
	public void deleteArticle(int articleno, String path) throws Exception {
		System.out.println("delete serviceimpl >>>>>>>>>>>" + articleno);
		List<HotplaceFileDto> fileList = hotplaceMapper.fileInfoList(articleno);
		System.out.println(fileList);
		hotplaceMapper.deleteArticle(articleno);
		//hotplaceMapper.deleteFile(articleno);
		for (HotplaceFileDto hotplaceFileDto : fileList) {
			File file = new File(path + File.separator + hotplaceFileDto.getSavefolder() + File.separator + hotplaceFileDto.getSavefile());
			file.delete();
		}
		
	}

	@Override
	public void updateHit(int articleno) throws Exception {
		hotplaceMapper.updateHit(articleno);
	}

	@Override
	public void scrap(int articleno, String userid) throws Exception {
		System.out.println("스크랩 서비스 임플");
		hotplaceMapper.scrap(articleno, userid);
	}
	
	@Override
	public HotplaceScrapDto getScrap(String userid) throws SQLException {
		return hotplaceMapper.getScrap(userid);
	}

}
