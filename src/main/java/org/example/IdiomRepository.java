package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class IdiomRepository {

    private final Path dirPath = Paths.get("db/wiseSaying");
    private final Path lastIdPath = dirPath.resolve("lastId.txt");
    private ArrayList<Idiom> listIdioms = new ArrayList<>();
    private int lastId = 0;

    public IdiomRepository() throws IOException {
        if (!Files.exists(dirPath)) Files.createDirectories(dirPath);
        if (!Files.exists(lastIdPath)) Files.createFile(lastIdPath);

        // lastId 읽기
        String content = Files.readString(lastIdPath).trim();
        if (!content.isEmpty()) lastId = Integer.parseInt(content);
    }


    public Idiom save(String content, String author) throws IOException {
        lastId++;
        Idiom idiom = new Idiom(lastId, author, content);
        listIdioms.add(idiom);

        // lastId.txt 갱신
        Files.writeString(lastIdPath, String.valueOf(lastId), StandardOpenOption.TRUNCATE_EXISTING);

        // 개별 JSON 파일 생성
        String json = "{\n" +
                "  \"id\": " + lastId + ",\n" +
                "  \"content\": \"" + content.replace("\"", "\\\"") + "\",\n" +
                "  \"author\": \"" + author.replace("\"", "\\\"") + "\"\n" +
                "}";
        Path jsonPath = dirPath.resolve(lastId + ".json");
        Files.writeString(jsonPath, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return idiom;
    }

    public boolean delete(int number) {
        return listIdioms.removeIf(i -> i.getNumber() == number);
    }

    public Idiom findById(int number) {
        return listIdioms.stream().filter(i -> i.getNumber() == number).findFirst().orElse(null);
    }

    public void buildJson() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < listIdioms.size(); i++) {
            Idiom item = listIdioms.get(i);
            sb.append("  {\n");
            sb.append("    \"id\": ").append(item.getNumber()).append(",\n");
            sb.append("    \"content\": \"").append(item.getIdiom().replace("\"", "\\\"")).append("\",\n");
            sb.append("    \"author\": \"").append(item.getAuthor().replace("\"", "\\\"")).append("\"\n");
            sb.append("  }");
            if (i < listIdioms.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");

        Path outputPath = dirPath.resolve("data.json");
        Files.writeString(outputPath, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // 전체 조회
    public List<Idiom> findAll() {
        return new ArrayList<>(listIdioms); // 복사본 반환
    }

    // 작가 검색
    public List<Idiom> findByAuthorContains(String keyword) {
        return listIdioms.stream()
                .filter(i -> i.getAuthor().contains(keyword))
                .collect(Collectors.toList());
    }

    // 명언(내용) 검색
    public List<Idiom> findByContentContains(String keyword) {
        return listIdioms.stream()
                .filter(i -> i.getIdiom().contains(keyword))
                .collect(Collectors.toList());
    }
}
