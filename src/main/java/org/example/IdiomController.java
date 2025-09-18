package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class IdiomController {

    private final IdiomService service;
    private final BufferedReader br;

    // 테스트용 생성자
    public IdiomController(InputStream in) throws IOException {
        this.service = new IdiomService(new IdiomRepository());
        this.br = new BufferedReader(new InputStreamReader(in));
    }

    public IdiomController() throws IOException {
        this(System.in);
    }

    public void start() throws IOException {
        while (true) {
            System.out.print("명령) ");
            String cmd = br.readLine().trim();

            if (cmd.equals("종료")) {
                br.close();
                return;
            } else if (cmd.equals("등록")) {
                register();
            } else if (cmd.contains("목록")) {
                list(cmd);
            } else if (cmd.startsWith("삭제")) {
                delete(cmd);
            } else if (cmd.startsWith("수정")) {
                edit(cmd);
            } else if (cmd.equals("빌드")) {
                build();
            } else {
                System.out.println("알 수 없는 명령입니다.");
            }
        }
    }

    private void register() throws IOException {
        System.out.print("명언 : ");
        String content = br.readLine();
        System.out.print("작가 : ");
        String author = br.readLine();
        Idiom idiom = service.register(content, author);
        System.out.printf("%d번 명언이 등록되었습니다.\n", idiom.getNumber());
    }

    private void list(String cmd) {

        String keywordType = null;
        String keyword = null;
        int page = 1; // 기본값: 1페이지

        // page 파라미터 추출
        if (cmd.contains("page=")) {
            int start = cmd.indexOf("page=") + "page=".length();
            int end = cmd.indexOf("&", start);
            if (end == -1) end = cmd.length();
            try {
                page = Integer.parseInt(cmd.substring(start, end));
            } catch (NumberFormatException e) {
                page = 1; // 잘못된 값이면 1페이지로
            }
        }

        // keywordType 추출
        if (cmd.contains("keywordType=")) {
            int start = cmd.indexOf("keywordType=") + "keywordType=".length();
            int end = cmd.indexOf("&", start);
            if (end == -1) end = cmd.length();
            keywordType = cmd.substring(start, end);
        }

        // keyword 추출
        if (cmd.contains("keyword=")) {
            int start = cmd.indexOf("keyword=") + "keyword=".length();
            int end = cmd.indexOf("&", start);
            if (end == -1) end = cmd.length();
            keyword = cmd.substring(start, end).trim();
        }

        // 서비스 호출 (필터링)
        List<Idiom> targetIdiom;
        if ("author".equals(keywordType)) {
            targetIdiom = service.searchByAuthor(keyword);
        } else if ("content".equals(keywordType)) {
            targetIdiom = service.searchByContent(keyword);
        } else {
            targetIdiom = service.getAll();
        }

        // 페이지네이션 적용
        List<Idiom> pageIdiom = service.getPage(targetIdiom, page);

        // 출력
        System.out.println("---------------------");
        for (Idiom i : pageIdiom) {
            System.out.printf("%d / %s / %s\n", i.getNumber(), i.getAuthor(), i.getIdiom());
        }
        System.out.println("---------------------");

    }

    private void delete(String cmd) {
        int num = extractNumber(cmd);
        if (num == -1) {
            System.out.println("삭제할 명언 번호를 입력해주세요.");
            return;
        }
        boolean ok = service.remove(num);
        if (ok) System.out.printf("%d번 명언이 삭제되었습니다.\n", num);
        else System.out.printf("%d번 명언은 존재하지 않습니다.\n", num);
    }

    private void edit(String cmd) throws IOException {
        int num = extractNumber(cmd);
        if (num == -1) {
            System.out.println("수정할 명언 번호를 입력해주세요.");
            return;
        }
        Idiom idiom = service.getAll().stream().filter(i -> i.getNumber() == num).findFirst().orElse(null);
        if (idiom == null) {
            System.out.printf("%d번 명언은 존재하지 않습니다.\n", num);
            return;
        }

        System.out.printf("명언(기존) : %s\n", idiom.getIdiom());
        System.out.print("명언 : ");
        String content = br.readLine();
        System.out.printf("작가(기존) : %s\n", idiom.getAuthor());
        System.out.print("작가 : ");
        String author = br.readLine();

        service.edit(num, content, author);
    }

    private void build() throws IOException {
        service.buildJson();
        System.out.println("data.json 파일의 내용이 갱신되었습니다.");
    }

    private int extractNumber(String cmd) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(cmd);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return -1;
    }

}
