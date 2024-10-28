package uz.pdp.Exam;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            String botToken = "7280055119:AAEOgJ2Pcqx5uRNLJKPgmoz0YiqUgXQNe1Q";
            botsApi.registerBot(new BookBot(botToken));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
