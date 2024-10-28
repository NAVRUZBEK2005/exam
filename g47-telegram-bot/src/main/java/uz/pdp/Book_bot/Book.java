package uz.pdp.Book_bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Book {
    private String title;
    private String author;
    private String publishedDate;

}

