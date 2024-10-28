package uz.pdp;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Mehrojbek
 * DateTime: 23/10/24 19:46
 **/
public class ReminderBot extends TelegramLongPollingBot {

    final String ADD_REMINDER_TEXT = "Add reminder";
    final String WELCOME_TEXT = "Assalomu alaykum boy ota.";

    private Map<Long, List<String>> reminders = new HashMap<>();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReminderBot(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage())
            messageProcess(update);
    }

    @Override
    public String getBotUsername() {
        return "computer_db_bot";
    }

    private void messageProcess(Update update) {
        Message message = update.getMessage();

        if (message.hasText())
            textProcess(message);

    }

    private void textProcess(Message message) {

        Long chatId = message.getChatId();
        String text = message.getText();

        if (text.equals("/start")) {

            //make button
            ReplyKeyboard markup = makeReplyButton(ADD_REMINDER_TEXT);

            //send msg
            sendMsg(chatId, WELCOME_TEXT, markup);

        } else if (text.equals(ADD_REMINDER_TEXT)) {

            sendMsg(chatId, "Ma'lumot kiriting: Namuna: \nFutbolga borish kerak\n23/10/2024 16:06");

            List<String> reminderTextList = reminders.getOrDefault(chatId, new ArrayList<>());
            reminders.put(chatId, reminderTextList);

        } else if (reminders.containsKey(chatId)) {

            List<String> remindTexts = reminders.get(chatId);

            String[] strings = text.split("\n");

            if (strings.length != 2) {
                sendMsg(chatId, "Xato buyruq berildi");
                return;
            }

            String remindText = strings[0];
            String dateTimStr = strings[1];

            remindTexts.add(remindText);

            LocalDateTime remindDateTime = LocalDateTime.parse(dateTimStr, dateTimeFormatter);

            if (remindDateTime.isBefore(LocalDateTime.now())) {
                sendMsg(chatId, "Vaqt xato");
                return;
            }

            long seconds = Duration.between(LocalDateTime.now(), remindDateTime).toSeconds();

            Runnable send = () -> sendAndClearMap(chatId, remindText);

            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(send, seconds, TimeUnit.SECONDS);
        }

    }

    private void sendAndClearMap(Long chatId, String remindText) {

        sendMsg(chatId, remindText);

        List<String> remindTextList = reminders.get(chatId);

        remindTextList.removeIf(text -> text.contains(remindText));
    }

    private Message sendMsg(Long chatId, String text, ReplyKeyboard markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(markup);

        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private Message sendMsg(Long chatId, String text) {
        return sendMsg(chatId, text, null);
    }

    private static ReplyKeyboard makeReplyButton(String... btnTexts) {

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);

        ArrayList<KeyboardRow> rows = new ArrayList<>();

        for (String btnText : btnTexts) {
            KeyboardRow row = new KeyboardRow();
            row.add(btnText);
            rows.add(row);
        }

        markup.setKeyboard(rows);

        return markup;
    }


}
