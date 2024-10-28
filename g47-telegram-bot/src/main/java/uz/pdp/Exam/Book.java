package uz.pdp.Exam;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Book {
    private static int counter = 0; // Kitoblar ID uchun hisoblagich
    private int id;
    private String name;
    private String authors;
    private String genre;
    private double price;
    private String publishedDate;

    public Book(String name, String authors, String genre, double price, String publishedDate) {
        this.id = ++counter; // Har bir yangi kitob uchun ID beriladi
        this.name = name;
        this.authors = authors;
        this.genre = genre;
        this.price = price;
        this.publishedDate = publishedDate;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n" +
                "Nom: " + name + "\n" +
                "Mualliflar: " + authors + "\n" +
                "Janr: " + genre + "\n" +
                "Narx: " + price + "\n" +
                "Nashr sanasi: " + publishedDate;
    }
}


