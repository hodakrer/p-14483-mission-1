package org.example;

public class Idiom {
    private int number;
    private String author;
    private String idiom;

    public Idiom(int number, String author, String idiom) {
        this.number = number;
        this.author = author;
        this.idiom = idiom;
    }

    public int getNumber() { return number; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIdiom() { return idiom; }
    public void setIdiom(String idiom) { this.idiom = idiom; }
}

