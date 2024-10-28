package uz.pdp.fastfoodbot;

import org.glassfish.jersey.model.internal.RankedComparator;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by: Mehrojbek
 * DateTime: 23/10/24 20:50
 **/
public class  FastFoodBot extends TelegramLongPollingBot {

    String ORDER = "\uD83D\uDECD Buyurtma berish";
    String BASKET = "\uD83D\uDED2 Savat";
    String CATEGORY_PREFIX = "cb:category:";
    String PRODUCT_PREFIX = "cb:product:";
    String DECREMENT_PRODUCT = "cb:decrementPr:";
    String INCREMENT_PRODUCT = "cb:incrementPr:";
    String ADD_BASKET = "cb:addBasket:";
    String BACK = "cb:back:";
    String CATEGORY_BACK = "categoryBack:";
    String ORDER_REGISTER = "Buyurtmani rasmiylashtirish";
    String CLEAR_BASKET = "🧹 savatni tozalash";
    final Long GROUP_CHAT_ID = -1002397100643L;

    List<Category> categories = new ArrayList<>(List.of(
            new Category(1, "\uD83C\uDF2F Lavash",
                    new ArrayList<>(List.of(
                            new Product(1, "Tandir lavash", "test", 36000),
                            new Product(2, "Sirli lavash", "test", 35000),
                            new Product(3, "Qalampir lavash", "test", 32000),
                            new Product(4, "Tovuqli lavash", "test", 31000)
                    ))
            ),
            new Category(2, "\uD83E\uDED4 Spice", new ArrayList<>()),
            new Category(3, "\uD83C\uDF2E Shaurma", new ArrayList<>()),
            new Category(4, "\uD83C\uDF54 Gamburger", new ArrayList<>()),
            new Category(5, "\uD83C\uDF2D Hotdog", new ArrayList<>())
    ));

    private Map<Long, User> users = new HashMap<>();

    private List<Order> orders = new ArrayList<>();

    public FastFoodBot(String botToken) {
        super(botToken);
    }





    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage())
            messageProcess(update.getMessage());

        else if (update.hasCallbackQuery())
            callbackProcess(update.getCallbackQuery());

    }

    private void callbackProcess(CallbackQuery callbackQuery) {

        String data = callbackQuery.getData();

        if (data.equals(ORDER))
            showCategories(callbackQuery);

        else if (data.equals(BASKET))
            showBasket(callbackQuery);

        else if (data.equals(ORDER_REGISTER))
            orderRegister(callbackQuery);

        else if (data.startsWith(CATEGORY_PREFIX))
            showProducts(callbackQuery);

        else if (data.startsWith(PRODUCT_PREFIX))
            showOneProduct(callbackQuery, 1);

        else if (data.startsWith(DECREMENT_PRODUCT))
            decrementProduct(callbackQuery);

        else if (data.startsWith(INCREMENT_PRODUCT))
            incrementProduct(callbackQuery);

        else if (data.startsWith(ADD_BASKET))
            addBasket(callbackQuery);

        else if (data.startsWith(BACK))
            back(callbackQuery);
    }

    private void orderRegister(CallbackQuery callbackQuery) {

        Long chatId = callbackQuery.getMessage().getChatId();

        User user = users.get(chatId);

        Basket basket = user.getBasket();

        if (Objects.isNull(basket))
            user.setBasket(new Basket());

        Map<Product, Integer> products = basket.getProducts();

        if (Objects.isNull(products)) {
            showAlert(callbackQuery.getId(), "Savatingiz bo'sh");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder().append("Mahsulotlar:\n\n");

        int totalPrice = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        for (Map.Entry<Product, Integer> productIntegerEntry : products.entrySet()) {

            Product product = productIntegerEntry.getKey();
            Integer count = productIntegerEntry.getValue();

            int price = product.getPrice() * count;
            String totalPriceStr = decimalFormat.format(price);

            stringBuilder
                    .append(product.getName())
                    .append(" ")
                    .append(count)
                    .append("*")
                    .append(decimalFormat.format(product.getPrice()))
                    .append(" = ")
                    .append(totalPriceStr)
                    .append("\n\n");

            totalPrice += price;
        }

        stringBuilder.append("Umumiy narx: ").append(decimalFormat.format(totalPrice)).append("\n\n");

        var from = callbackQuery.getMessage().getChat();

        String userName = from.getUserName();

        stringBuilder.append("Mijoz: ").append("@").append(userName).append("\n")
                .append("Tel: ").append(user.getPhoneNumber()).append("\n");

        sendMsg(GROUP_CHAT_ID, stringBuilder.toString());
    }

    private void showBasket(CallbackQuery callbackQuery) {

        Long chatId = callbackQuery.getMessage().getChatId();

        User user = users.get(chatId);

        Basket basket = user.getBasket();

        if (Objects.isNull(basket))
            user.setBasket(new Basket());

        Map<Product, Integer> products = basket.getProducts();

        if (Objects.isNull(products)) {
            showAlert(callbackQuery.getId(), "Savatingiz bo'sh");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder().append("Mahsulotlar:\n");

        int totalPrice = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        for (Map.Entry<Product, Integer> productIntegerEntry : products.entrySet()) {

            Product product = productIntegerEntry.getKey();
            Integer count = productIntegerEntry.getValue();

            int price = product.getPrice() * count;
            String totalPriceStr = decimalFormat.format(price);

            stringBuilder
                    .append(product.getName())
                    .append(" ")
                    .append(count)
                    .append("*")
                    .append(decimalFormat.format(product.getPrice()))
                    .append(" = ")
                    .append(totalPriceStr)
                    .append("\n\n");

            totalPrice += price;
        }

        stringBuilder.append("Umumiy narx: ").append(decimalFormat.format(totalPrice));

        Message message = callbackQuery.getMessage();
        Integer messageId = message.getMessageId();
        String inlineMessageId = callbackQuery.getInlineMessageId();

        List<InlineBtnData> buttons = List.of(
                new InlineBtnData(
                        ORDER_REGISTER,
                        ORDER_REGISTER,
                        1,
                        1
                ),
                new InlineBtnData(
                        CLEAR_BASKET,
                        CLEAR_BASKET,
                        2,
                        1
                ),
                new InlineBtnData(
                        "🔙 orqaga",
                        BACK + "home",
                        3,
                        1
                )
        );

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(stringBuilder.toString());
        editMessageText.setMessageId(messageId);
        editMessageText.setInlineMessageId(inlineMessageId);

        InlineKeyboardMarkup markup = makeInlineBtn(buttons);
        editMessageText.setReplyMarkup(markup);

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void decrementProduct(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        String catIdAndProId = data.replace(DECREMENT_PRODUCT, "");

        String[] strings = catIdAndProId.split(":");
        int categoryId = Integer.parseInt(strings[0]);
        int productId = Integer.parseInt(strings[1]);
        int count = Integer.parseInt(strings[2]);

        callbackQuery.setData(PRODUCT_PREFIX + categoryId + ":" + productId);

        count--;

        showOneProduct(callbackQuery, count);
    }

    private void incrementProduct(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        String catIdAndProId = data.replace(INCREMENT_PRODUCT, "");

        String[] strings = catIdAndProId.split(":");
        int categoryId = Integer.parseInt(strings[0]);
        int productId = Integer.parseInt(strings[1]);
        int count = Integer.parseInt(strings[2]);

        callbackQuery.setData(PRODUCT_PREFIX + categoryId + ":" + productId);

        count++;

        showOneProduct(callbackQuery, count);
    }

    private void addBasket(CallbackQuery callbackQuery) {

        String[] strings = callbackQuery.getData().replace(ADD_BASKET, "").split(":");

        int categoryId = Integer.parseInt(strings[0]);
        int productId = Integer.parseInt(strings[1]);
        int count = Integer.parseInt(strings[2]);

        Category category = categories.stream()
                .filter(oneCategory -> oneCategory.getId() == categoryId)
                .findFirst()
                .get();

        Product product = category.getProducts().stream()
                .filter(oneProduct -> oneProduct.getId() == productId)
                .findFirst()
                .get();

        Long chatId = callbackQuery.getMessage().getChatId();
        User user = users.get(chatId);

        if (Objects.isNull(user.getBasket()))
            user.setBasket(new Basket());

        Basket basket = user.getBasket();

        if (Objects.isNull(basket.getProducts()))
            basket.setProducts(new HashMap<>());

        Map<Product, Integer> products = basket.getProducts();
        products.put(product, count);

        showAlert(callbackQuery.getId(), "✅ Savatga qo'shildi.");
    }

    private void showAlert(String callbackQueryId, String text) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQueryId);
        answerCallbackQuery.setText(text);
        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void back(CallbackQuery callbackQuery) {

        String data = callbackQuery.getData();
        String back = data.replace(BACK, "");

        if (back.startsWith(CATEGORY_BACK)) {
            int categoryId = Integer.parseInt(back.replace(CATEGORY_BACK, ""));
            categoryBack(categoryId, callbackQuery);
        }

    }

    private void categoryBack(int categoryId, CallbackQuery callbackQuery) {

        callbackQuery.setData(CATEGORY_PREFIX + categoryId);

        showProducts(callbackQuery);
    }

    private void showOneProduct(CallbackQuery callbackQuery, int count) {

        String[] strings = callbackQuery.getData().replace(PRODUCT_PREFIX, "").split(":");

        int categoryId = Integer.parseInt(strings[0]);
        int productId = Integer.parseInt(strings[1]);

        Optional<Category> optionalCategory = categories.stream()
                .filter(category -> category.getId() == categoryId)
                .findFirst();
        if (optionalCategory.isEmpty()) {
            System.err.println("Yo'qku bu .....");
            return;
        }

        Category category = optionalCategory.get();

        Product product = category.getProducts().stream()
                .filter(oneProduct -> oneProduct.getId() == productId)
                .findFirst().get();

        Message message = callbackQuery.getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();

        List<InlineBtnData> buttons = List.of(
                new InlineBtnData(
                        "-",
                        DECREMENT_PRODUCT + categoryId + ":" + productId + ":" + count,
                        1,
                        1
                ),
                new InlineBtnData(
                        String.valueOf(count),
                        "cb:",
                        1,
                        2
                ),
                new InlineBtnData(
                        "+",
                        INCREMENT_PRODUCT + categoryId + ":" + productId + ":" + count,
                        1,
                        3
                ),
                new InlineBtnData(
                        "🛒 savatga",
                        ADD_BASKET + categoryId + ":" + productId + ":" + count,
                        2,
                        1
                ),
                new InlineBtnData(
                        "🔙 orqaga",
                        BACK + CATEGORY_BACK + categoryId,
                        3,
                        1
                )
        );

        String text = productInfo(product);
        editMsgText(chatId, text, messageId, buttons);
    }

    private void editMsgText(Long chatId, String text, Integer messageId, List<InlineBtnData> buttons) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(text);
        editMessageText.setMessageId(messageId);

        InlineKeyboardMarkup markup = makeInlineBtn(buttons);
        editMessageText.setReplyMarkup(markup);

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String productInfo(Product product) {
        return "%s\n%s\n%s".formatted(product.getName(), product.getDescription(), product.getPrice());
    }

    private void showProducts(CallbackQuery callbackQuery) {

        String data = callbackQuery.getData();
        int categoryId = Integer.parseInt(data.replace(CATEGORY_PREFIX, ""));

        Optional<Category> optionalCategory = categories.stream()
                .filter(category -> category.getId() == categoryId)
                .findFirst();
        if (optionalCategory.isEmpty()) {
            System.err.println("Yo'qku bu .....");
            return;
        }

        Category category = optionalCategory.get();

        List<Product> products = category.getProducts();

        Map<String, String> productMap = products.stream()
                .collect(Collectors.toMap(Product::getName, product -> PRODUCT_PREFIX + category.getId() + ":" + product.getId()));

        Message message = callbackQuery.getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        String inlineMessageId = callbackQuery.getInlineMessageId();

        editBtn(chatId, messageId, inlineMessageId, productMap);
    }

    private void showCategories(CallbackQuery callbackQuery) {

        Message message = callbackQuery.getMessage();
        String inlineMessageId = callbackQuery.getInlineMessageId();
        Integer messageId = message.getMessageId();
        Long chatId = message.getChatId();
        Map<String, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getName, category -> CATEGORY_PREFIX + category.getId()));

        editBtn(chatId, messageId, inlineMessageId, categoryMap);



    }

    private void editBtn(Long chatId, Integer messageId, String inlineMessageId, Map<String, String> buttonMap) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setInlineMessageId(inlineMessageId);

        InlineKeyboardMarkup replyKeyboard = makeInlineBtn(buttonMap);

        editMessageReplyMarkup.setReplyMarkup(replyKeyboard);

        editMsgBtn(editMessageReplyMarkup);
    }

    public void editMsgBtn(EditMessageReplyMarkup editMessageReplyMarkup) {
        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void messageProcess(Message message) {

        if (message.getChatId() < 0) {
            return;
        }

        if (message.hasText())
            textProcess(message);

        else if (message.hasContact())
            contactProcess(message);

    }

    private void contactProcess(Message message) {

        Contact contact = message.getContact();
        Long chatId = message.getChatId();

        if (!Objects.equals(contact.getUserId(), message.getFrom().getId())) {
            sendMsg(message.getChatId(), "O'zingnikini jo'nat menga");
        } else {
            User user = users.get(chatId);
            user.setPhoneNumber(contact.getPhoneNumber());

            Integer messageId = sendMsg(chatId, "Muvaffaqiyatli", new ReplyKeyboardRemove(true)).getMessageId();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            DeleteMessage deleteMessage = new DeleteMessage(chatId.toString(), messageId);

            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

            ReplyKeyboard replyKeyboard = makeInlineBtn(ORDER, BASKET);
            sendMsg(chatId, "Xush kelibsiz", replyKeyboard);
        }
    }

    private void textProcess(Message message) {

        String text = message.getText();
        Long chatId = message.getChatId();

        User user = users.getOrDefault(chatId, new User(chatId, null));
        users.put(chatId, user);

        if (text.equals("/start")) {

            if (user.getPhoneNumber() == null) {
                String welcomeText = "Xush kelibsiz. Telefon raqamingizni yuboring.";
                ReplyKeyboard keyboard = shareContactButton();
                sendMsg(chatId, welcomeText, keyboard);

            } else {
                ReplyKeyboard replyKeyboard = makeInlineBtn(ORDER, BASKET);
                sendMsg(chatId, "Xush kelibsiz😊", replyKeyboard);
            }
        }
    }

    private ReplyKeyboard shareContactButton(String... btnTexts) {

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);

        ArrayList<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton();
        button.setText("Telefon raqam");
        button.setRequestContact(true);
        row.add(button);
        rows.add(row);

        markup.setKeyboard(rows);

        return markup;
    }

    private ReplyKeyboard makeInlineBtn(String... texts) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (String text : texts) {

            InlineKeyboardButton button = new InlineKeyboardButton();

            button.setText(text);
            button.setCallbackData(text);

            List<InlineKeyboardButton> row = List.of(button);
            rows.add(row);

        }

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup makeInlineBtn(Map<String, String> buttons) {

        //{ "Lavash": "category:1" }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (String key : buttons.keySet()) {

            String value = buttons.get(key);

            InlineKeyboardButton button = new InlineKeyboardButton();

            button.setText(key);
            button.setCallbackData(value);

            List<InlineKeyboardButton> row = List.of(button);
            rows.add(row);

        }

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup makeInlineBtn(List<InlineBtnData> buttons) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        Map<Integer, List<InlineBtnData>> rowMap = buttons.stream()
                .collect(Collectors.groupingBy(InlineBtnData::getRowNumber));

        ArrayList<Integer> rowNumbers = new ArrayList<>(rowMap.keySet());
        Collections.sort(rowNumbers);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Integer rowNumber : rowNumbers) {

            List<InlineKeyboardButton> row = new ArrayList<>();

            List<InlineBtnData> columnButtons = rowMap.get(rowNumber);
            columnButtons.sort(Comparator.comparingInt(InlineBtnData::getColumnNumber));

            for (InlineBtnData columnButton : columnButtons) {

                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(columnButton.getText());
                button.setCallbackData(columnButton.getCallback());
                row.add(button);

            }

            rows.add(row);
        }

        markup.setKeyboard(rows);
        return markup;
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

    @Override
    public String getBotUsername() {
        return "ooooooo";
    }

}
