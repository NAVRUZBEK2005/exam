package uz.pdp.Exam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class BookBot extends TelegramLongPollingBot {
    private final ConcurrentHashMap<Long, List<Book>> userBooksMap = new ConcurrentHashMap<>();

    final static String ADD_COMMAND = "/add";
    final static String LIST_COMMAND = "/list";
    final static String DELETE_COMMAND = "/delete";
    final static String EXPORT_COMMAND = "/export";

    public BookBot(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();

            if (text.equals("/start")) {
                sendMainMenu(message.getChatId());
            } else {
                switch (text.split(" ")[0]) {
                    case ADD_COMMAND -> handleAddBook(message);
                    case LIST_COMMAND -> handleListBooks(message);
                    case DELETE_COMMAND -> handleDeleteBook(message);
                    case EXPORT_COMMAND -> handleExportBooks(message);
                    default -> sendMainMenu(message.getChatId());
                }
            }
        }
    }

    private void sendMainMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Kitoblar botiga xush kelibsiz! Quyidagi amallardan birini tanlang:");

        ReplyKeyboardMarkup markup = MakeButtons.makeReplyBtn();
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleAddBook(Message message) {
        String[] parts = message.getText().split(";", 5);
        if (parts.length < 5) {
            sendMessage(message.getChatId(), "Format: /add <kitob nomi>; <mualliflar>; <janr>; <narx>; <nashr sanasi>");
            return;
        }

        String name = parts[0].replace(ADD_COMMAND, "").trim();
        String authors = parts[1].trim();
        String genre = parts[2].trim();

        try {
            double price = Double.parseDouble(parts[3].trim());
            String publishedDate = parts[4].trim();
            Book book = new Book(name, authors, genre, price, publishedDate);

            // Foydalanuvchining kitoblar ro'yxatini olish yoki yaratish
            userBooksMap.computeIfAbsent(message.getChatId(), k -> new ArrayList<>()).add(book);
            sendMessage(message.getChatId(), "Kitob qo'shildi:\n" + book);
        } catch (NumberFormatException e) {
            sendMessage(message.getChatId(), "Narx raqamini to'g'ri kiriting.");
        }
    }

    private void handleListBooks(Message message) {
        List<Book> books = userBooksMap.get(message.getChatId());
        if (books == null || books.isEmpty()) {
            sendMessage(message.getChatId(), "Hozircha kitoblar mavjud emas.");
            return;
        }

        StringBuilder response = new StringBuilder("Kitoblar ro'yxati:\n\n");
        for (Book book : books) {
            response.append(book).append("\n\n");
        }
        sendMessage(message.getChatId(), response.toString());
    }

    private void handleDeleteBook(Message message) {
        String[] parts = message.getText().split(" ");
        if (parts.length < 2) {
            sendMessage(message.getChatId(), "Format: /delete <kitob ID>");
            return;
        }

        try {
            int id = Integer.parseInt(parts[1]);
            List<Book> books = userBooksMap.get(message.getChatId());

            if (books != null && books.removeIf(book -> book.getId() == id)) {
                sendMessage(message.getChatId(), "Kitob o'chirildi. ID: " + id);
            } else {
                sendMessage(message.getChatId(), "Berilgan ID ga ega kitob topilmadi.");
            }
        } catch (NumberFormatException e) {
            sendMessage(message.getChatId(), "/delete ID raqamini to'g'ri kiriting.");
        }
    }

    @SneakyThrows
    private void handleExportBooks(Message message) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<Book> books = userBooksMap.get(message.getChatId());

        if (books == null || books.isEmpty()) {
            sendMessage(message.getChatId(), "Hozircha kitoblar mavjud emas.");
            return;
        }

        String jsonBooks = gson.toJson(books);
        File jsonFile = new File("books_" + message.getChatId() + ".json"); // Foydalanuvchi ID bilan fayl nomi

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(jsonBooks);
            writer.flush();

            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(message.getChatId().toString());
            sendDocument.setDocument(new InputFile(jsonFile));

            execute(sendDocument);
            sendMessage(message.getChatId(), "Kitoblar JSON fayl ko'rinishida yuborildi!");
        } catch (IOException | TelegramApiException e) {
            sendMessage(message.getChatId(), "Xatolik yuz berdi: JSON faylni saqlab bo'lmadi.");
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

    @Override
    public String getBotUsername() {
        return "Your_Bot_Username"; // Bot username ni o'zgartiring
    }
}
