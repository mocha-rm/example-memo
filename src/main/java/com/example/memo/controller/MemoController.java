package com.example.memo.controller;

import com.example.memo.dto.MemoRequestDto;
import com.example.memo.dto.MemoResponseDto;
import com.example.memo.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/memos") //prefix , 공통적으로 사용하는 url 을 설정
public class MemoController {

    private final Map<Long, Memo> memoList = new HashMap<>();

    @PostMapping
    public ResponseEntity<MemoResponseDto> createMemo(@RequestBody MemoRequestDto dto) {

        //식별자가 1씩 증가 하도록
        Long memoId = memoList.isEmpty() ? 1 : Collections.max(memoList.keySet()) + 1;

        //요청받은 데이터로 메모를 생성
        Memo memo = new Memo(memoId, dto.getTitle(), dto.getContents());

        // Inmemry DB에 메모를 저장
        memoList.put(memoId, memo);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.CREATED);
    }

    @GetMapping//메모 전체 조회
    public ResponseEntity<List<MemoResponseDto>> findAllMemos() {
        List<MemoResponseDto> responseList = new ArrayList<>();

        // HashMap<Memo> -> LIst<MemoResponseDto>
        for (Memo memo : memoList.values()) {
            MemoResponseDto responseDto = new MemoResponseDto(memo);
            responseList.add(responseDto);
        }

        // Map to List 스트림을 이용한 방법
        //responseList = memoList.values().stream().map(MemoResponseDto::new).toList();

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemoResponseDto> findMemoById(@PathVariable Long id) {

        Memo memo = memoList.get(id);

        if (memo == null) { //목록이 없으면 404 에러 통보
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateMemoById(
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto
    ) {
        Memo memo = memoList.get(id);

        if (memo == null) { //메모가 없으면 NotFound
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (dto.getTitle() == null || dto.getContents() == null) { // 필수값을 입력하지 않았으면 Ba_Request
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        memo.update(dto);
        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateTitle( //제목만 수정
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto
    ) {
        Memo memo = memoList.get(id);

        if (memo == null) { //메모가 없으면 NotFound
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (dto.getTitle() == null || dto.getContents() != null) { // 필수값을 입력하지 않았으면 Ba_Request
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        memo.updateTitle(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMemo(@PathVariable Long id) {
        if (memoList.containsKey(id)) {
            memoList.remove(id);

            return new ResponseEntity<>("메모가 삭제되었습니다", HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
