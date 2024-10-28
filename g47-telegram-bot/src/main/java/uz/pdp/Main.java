package uz.pdp;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.fastfoodbot.FastFoodBot;

public class Main {
    public static void main(String[] args) {

        try {
            String token = "7789730319:AAFylIiHwip5T48Am82FxqrfSpPrhmwcMn8";

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

//            BotService botService = BotService.createInstance(token);
//            ReminderBot reminderBot = new ReminderBot(token);
            FastFoodBot fastFoodBot = new FastFoodBot(token);

            telegramBotsApi.registerBot(fastFoodBot);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}