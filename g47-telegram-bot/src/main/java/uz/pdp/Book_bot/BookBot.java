package uz.pdp.Book_bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookBot extends TelegramLongPollingBot {
    private List<Book> books = new ArrayList<>();

    @Override
    public String getBotUsername() {
        return "Book_bot";
    }

    @Override
    public String getBotToken() {
        return "7280055119:AAEOgJ2Pcqx5uRNLJKPgmoz0YiqUgXQNe1Q";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();

            if (text.startsWith("/add")) {
                handleAddBook(message);
            } else if (text.startsWith("/update")) {
                handleUpdateBook(message);
            } else if (text.startsWith("/export")) {
                handleExportBooks(message);
            } else {
                sendMessage(message.getChatId(), "Yordam:\n" +
                        "/add - Kitob qo'shish\n" +
                        "/update - Kitobni o'zgartirish\n" +
                        "/export - Kitoblarni JSON faylga eksport qilish");
            }
        }
    }

    private void handleAddBook(Message message) {
        String[] parts = message.getText().split(";", 3);
        if (parts.length < 3) {
            sendMessage(message.getChatId(), "Format: /add <kitob nomi>; <muallif>; <chiqarilgan sana>");
            return;
        }
        Book book = new Book(parts[0].substring(5).trim(), parts[1].trim(), parts[2].trim());
        books.add(book);
        sendMessage(message.getChatId(), "Kitob qo'shildi!");
    }

    private void handleUpdateBook(Message message) {
        String[] parts = message.getText().split(";", 3);
        if (parts.length < 3) {
            sendMessage(message.getChatId(), "Format: /update <kitob nomi>; <yangi muallif>; <yangi sana>");
            return;
        }
        String title = parts[0].substring(8).trim();
        String newAuthor = parts[1].trim();
        String newPublishedDate = parts[2].trim();

        // Kitobni nomi bo'yicha qidiramiz
        boolean found = false;
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                book.setAuthor(newAuthor);
                book.setPublishedDate(newPublishedDate);
                sendMessage(message.getChatId(), "Kitob ma'lumotlari yangilandi!");
                found = true;
                break;
            }
        }
        if (!found) {
            sendMessage(message.getChatId(), "Kitob topilmadi.");
        }
    }

    private void handleExportBooks(Message message) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonBooks = gson.toJson(books);
        File jsonFile = new File("books.json");

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(jsonBooks);

            // Fayl muvaffaqiyatli yaratildi, uni foydalanuvchiga yuboramiz
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(message.getChatId().toString());
            sendDocument.setDocument(new InputFile(jsonFile));

            execute(sendDocument);
            sendMessage(message.getChatId(), "Kitoblar JSON fayl ko'rinishida yuborildi!");

        } catch (IOException e) {
            sendMessage(message.getChatId(), "Xatolik yuz berdi: JSON faylni saqlab bo'lmadi.");
            e.printStackTrace();
        } catch (TelegramApiException e) {
            sendMessage(message.getChatId(), "Faylni yuborishda xatolik yuz berdi.");
            e.printStackTrace();
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new BookBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
