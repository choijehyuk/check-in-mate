package com.ssafy.enjoytrip.board.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ssafy.enjoytrip.board.model.BoardDto;
import com.ssafy.enjoytrip.board.model.service.BoardService;
import com.ssafy.enjoytrip.board.model.service.BoardServiceImpl;
import com.ssafy.enjoytrip.member.model.MemberDto;
import com.ssafy.enjoytrip.util.PageNavigation;
import com.ssafy.enjoytrip.util.ParameterCheck;
import com.ssafy.enjoytrip.util.SizeConstant;

@RestController
@RequestMapping("/board/api")
@CrossOrigin(origins = { "*" })
public class RestBoardController {
	private static final long serialVersionUID = 1L;

	private String uploadPath;
	@Qualifier("BoardServiceImpl")
	private BoardService boardService;
 
	@Autowired
	public RestBoardController(BoardService boardService) {
		super();
		this.boardService = boardService;
	}

	// 여행정보 공유 리스트 출력
	@GetMapping("/list")
	public List<BoardDto> list(@RequestParam Map<String, String> map) throws Exception {
		return boardService.listArticle(map);
	}

	// 여행정보 공유 글쓰기
	@RequestMapping(value = "/write", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> write(@RequestBody BoardDto boardDto) throws Exception {
		ResponseEntity<Map<String, Object>> resEntity = null;
		System.out.println("여행정보공유 write >>>>>>>>>>>>>>>>>" + boardDto);
		try {
			boardService.writeArticle(boardDto);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("resmsg", "입력 성공");
			map.put("resValue", null);
			resEntity = new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
		} catch(Exception e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("resmsg", "입력 실패");
			resEntity = new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
		}
		return resEntity;

	}

	// 여행정보 공유 글보기 ( articleNo로 비교 )
	@RequestMapping(value = "/view/{articleNo}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> view(@PathVariable("articleNo") int articleNo) throws Exception {
		ResponseEntity<Map<String, Object>> resEntity = null;
		BoardDto boardDto = null;
		try {
			boardDto = boardService.getArticle(articleNo);
			boardService.updateHit(articleNo);
			System.out.println("boardServvice view >>>>" + boardDto);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("resmsg", "조회 성공");
			map.put("article", boardDto);
			resEntity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		} catch (RuntimeException e) {
			// TODO: handle exception
			System.out.println("boardServvice view >>>>" + boardDto);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("resmsg", "조회 실패");
			resEntity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}

		return resEntity;
	}

	// 여행정보 공유 글 수정하기로 이동
	@RequestMapping(value = "/modify", method = RequestMethod.GET)
	public ResponseEntity<String> modify(@RequestParam("articleno") int articleNo,
			@RequestParam Map<String, String> map, Model model) throws Exception {
		BoardDto boardDto = boardService.getArticle(articleNo);
		model.addAttribute("board", boardDto);
		model.addAttribute("pgno", map.get("pgno"));
		model.addAttribute("key", map.get("key"));
		model.addAttribute("word", map.get("word"));
		return ResponseEntity.ok("수정하기로 이동 완료");
	}

	// 여행정보 공유 글 수정하기
	@RequestMapping(value = "/modify", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> modify(@RequestBody BoardDto boardDto) throws Exception {
		ResponseEntity<Map<String, Object>> resEntity = null;
		try {
			boardService.modifyArticle(boardDto);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("resmsg", "수정 성공");
			map.put("article", boardDto);
			resEntity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("resmsg", "수정 실패");
			resEntity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}
		return resEntity;
	}

	// 여행정보 공유 글 삭제하기
	@RequestMapping(value = "/delete/{articleNo}", method = RequestMethod.DELETE)
	public ResponseEntity<Map<String, Object>> delete(@PathVariable("articleNo") int articleNo,
			RedirectAttributes redirectAttributes) throws Exception {
		ResponseEntity<Map<String, Object>> resEntity = null;
		System.out.println("board delete articleNo >>>>" + articleNo);
		try {
			boardService.deleteArticle(articleNo);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("resmsg", "삭제 성공");
			map.put("resValue", null);
			resEntity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("resmsg", "삭제 실패");
			resEntity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}
		return resEntity;
	}

}
