package uz.pdp;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class chatId extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        // Yangilanish xabar bo'lsa
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId(); // Guruh chat ID'sini olish

            // Chat ID'sini konsolga chiqarish
            System.out.println("Chat ID: " + chatId);

            // Xabarni javob berish
            sendMessage(chatId, "Guruh chat ID: " + chatId);
        }
    }

    // Bot tokenini qaytarish
    @Override
    public String getBotToken() {
        return "7378619463:AAH8lAYGH31JDUxA6u2TL6jmZyma1VWuXjg"; // O'zingizning bot tokeningizni kiriting
    }

    // Bot nomini qaytarish
    @Override
    public String getBotUsername() {
        return "Navruz"; // O'zingizning bot username'ini kiriting
    }

    // Xabar yuborish uchun yordamchi metod
    private void sendMessage(Long chatId, String text) {
        try {
            SendMessage message = new SendMessage(); // Xabar obyektini yaratish
            message.setChatId(String.valueOf(chatId)); // Chat ID'sini o'rnatish
            message.setText(text); // Xabar matnini o'rnatish

            execute(message); // Xabarni yuborish
        } catch (TelegramApiException e) {
            e.printStackTrace(); // Xatolik yuz bersa chiqarish
        }
    }

    public static void main(String[] args) {
        try {
            // Telegram botini ishga tushirish
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new chatId()); // Botni ro'yxatdan o'tkazish
        } catch (TelegramApiException e) {
            e.printStackTrace(); // Xatolik yuz bersa chiqarish
        }
    }
}
