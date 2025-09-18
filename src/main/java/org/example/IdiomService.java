package org.example;

import java.awt.print.Pageable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class IdiomService {

    private final IdiomRepository repository;

    public IdiomService(IdiomRepository repository) {
        this.repository = repository;
    }

    public Idiom register(String content, String author) throws IOException {
        return repository.save(content, author);
    }

    public boolean remove(int number) {
        return repository.delete(number);
    }

    public void edit(int number, String newContent, String newAuthor) {
        Idiom idiom = repository.findById(number);
        if (idiom != null) {
            idiom.setIdiom(newContent);
            idiom.setAuthor(newAuthor);
        }
    }

    public void buildJson() throws IOException {
        repository.buildJson();
    }

    // 명령어에서 검색 카테고리 추출 (author or content)
    public String searchCategory(String cmd) {
        if (cmd.contains("author")) {
            return "author";
        } else if (cmd.contains("content")) {
            return "content";
        } else {
            return "all"; // 전체 조회
        }
    }

    public List<Idiom> searchByAuthor(String keyword) {


        List<Idiom> answerIdiom = repository.findByAuthorContains(keyword);
        return answerIdiom;
    }

    public List<Idiom> searchByContent(String keyword) {
        List<Idiom> answerIdiom = repository.findByContentContains(keyword);
        return answerIdiom;
    }

    public List<Idiom> getAll() {
        List<Idiom> answerIdiom = repository.findAll();
        return answerIdiom;
    }

    //애초에 page, pageable이란 인터페이스 다루는 법을 알아야
    //Page
    //Pageable
    public List<Idiom> getPage(List<Idiom> inputIdiom, int page) {
        List<Idiom> pageIdiom = new ArrayList<>();

        // 1. null 체크
        if (inputIdiom == null || inputIdiom.isEmpty()) {
            return pageIdiom; // 빈 리스트 반환
        }

        // 2. idiom.id 기준으로 내림차순 정렬
        inputIdiom.sort((a, b) -> Long.compare(b.getNumber(), a.getNumber()));

        // 3. 한 페이지당 아이템 수 고정
        int size = 5;

        // 4. page 번호에 따른 시작/끝 인덱스 계산
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, inputIdiom.size());

        // 5. 유효 범위면 subList로 잘라내기
        if (startIndex < inputIdiom.size()) {
            pageIdiom = inputIdiom.subList(startIndex, endIndex);
        }

        // 6. 잘라낸 결과 반환
        return pageIdiom;

    }


}
