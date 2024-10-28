package uz.pdp;

import lombok.Getter;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Mehrojbek
 * DateTime: 21/10/24 21:06
 **/
public class BotService extends TelegramLongPollingBot {

    @Getter
    private static BotService instance;

    public static BotService createInstance(String token) {
        if (instance == null) {
            instance = new BotService(token);
        }
        return instance;
    }

    private BotService(String botToken) {
        super(botToken);
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        System.out.println("update = " + update);

        if (update.hasMessage()) {

            Message message = update.getMessage();

            if (message.hasText()) {
                getMessageText(message);
            }

        }

        if(update.hasCallbackQuery()) {

            getCallback(update.getCallbackQuery());

        }

    }

    private void getCallback(CallbackQuery callbackQuery) {

        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        System.out.println("data = " + data);

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setText("Bu alert");
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
//        answerCallbackQuery.setShowAlert(true);

        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private void getMessageText(Message message) throws InterruptedException {
        String text = message.getText();

        if (text.equals("/start")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Xush kelibsiz boy ota");

            ReplyKeyboardMarkup replyMarkup = getReplyMarkup();

            InlineKeyboardMarkup inlineKeyboardMarkup = getInlineMarkup();

            sendMessage.setReplyMarkup(inlineKeyboardMarkup);

            Message responseMessage = sendMsg(sendMessage);
            Integer messageId = responseMessage.getMessageId();

            Thread.sleep(1000);

//            EditMessageText editMessageText = new EditMessageText();
//
//            editMessageText.setChatId(message.getChatId());
//            editMessageText.setMessageId(messageId);
//            editMessageText.setText("Text o'zgardi aaa 😎");
//
//            editMsg(editMessageText);
        }

        if (text.equals("Button1")){
            SendMessage sendMessage = new SendMessage(
                    message.getChatId().toString(),
                    "Siz %s ni bosdingiz".formatted(text)
            );
            sendMsg(sendMessage);
        }
    }

    private InlineKeyboardMarkup getInlineMarkup() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        ArrayList<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText("Button1");
        btn1.setCallbackData("Btn1Clicked");

        row1.add(btn1);

        rows.add(row1);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup getReplyMarkup() {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();

        KeyboardButton btn1 = new KeyboardButton();
        btn1.setText("Button1");

        KeyboardButton btn2 = new KeyboardButton();
        btn2.setText("Button2");

        row1.add(btn1);
        row1.add(btn2);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Button3");

        rows.add(row1);
        rows.add(row2);

        replyMarkup.setKeyboard(rows);
        return replyMarkup;
    }

    public Message sendMsg(SendMessage sendMessage) {
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void editMsg(EditMessageText editMessageText) {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public String getBotUsername() {
        return "computer_db_bot";
    }
}
