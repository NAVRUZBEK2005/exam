package uz.pdp.fastfoodbot;


import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        try {

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Register your bot
            botsApi.registerBot(new FastFoodBot("7378619463:AAH8lAYGH31JDUxA6u2TL6jmZyma1VWuXjg"));

            System.out.println("Bot started successfully!");
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }
}
