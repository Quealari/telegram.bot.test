package telegram.test.telegram.bot.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.test.telegram.bot.test.buttons.Buttons;
import telegram.test.telegram.bot.test.config.BotConfig;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = 0;
        long userId = 0; //это нам понадобится позже
        String userName = null;
        String receivedMessage;

        //если получено сообщение текстом
        if (update.hasMessage() && update.getMessage().hasText()) {

            userId = update.getMessage().getFrom().getId();
            chatId = update.getMessage().getChatId();
            userName = update.getMessage().getFrom().getFirstName();

            Message message = new Message();
            message.setReplyMarkup(Buttons.inlineMarkup());
            receivedMessage = update.getMessage().getText(); // в эту переменную текст входящего сообщения
            botAnswerUtils(receivedMessage, chatId, userName);
        }

        //если нажата одна из кнопок бота
        else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            receivedMessage = update.getCallbackQuery().getData();

            botAnswerUtils(receivedMessage, chatId, userName);
        }
        log.info("Replied to user" + update.getMessage().getChat().getFirstName());
    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
        switch (receivedMessage) {
            case "/start":
                startBot(chatId, userName);
                break;
            case "/help":
                sendHelpText(chatId, "HELP_TEXT");
                break;
            default:
                sendAnotherText(chatId, "Нажми кнопку, балда");
                break;
                //todo поработать тут с условиями продумать логику
        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hi, " + userName + "! I'm a Telegram bot.'");
        message.setReplyMarkup(Buttons.inlineMarkup());


        try {
            execute(message); // здесть отправляем приветственное сообщение
            log.info(message.getText() + "Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendHelpText(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());

        }
    }

    private void sendAnotherText(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
