package com.example.memo.dto;

import lombok.Getter;

@Getter
public class MemoRequestDto {

    //요청 받을 것은 title 과, contents
    private String title;
    private String contents;
}
