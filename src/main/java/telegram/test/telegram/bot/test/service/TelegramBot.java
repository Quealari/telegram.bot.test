package telegram.test.telegram.bot.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
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



        var chatId = update.getMessage().getChatId();
        var userName = update.getMessage().getFrom().getFirstName();


        //если получено сообщение текстом
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = new Message();
            message.setReplyMarkup(Buttons.inlineMarkup());
            var receivedMessage = update.getMessage().getText(); // в эту переменную текст входящего сообщения
            botAnswerUtils(receivedMessage, chatId, userName);
        }

        //если нажата одна из кнопок бота
        else if (update.hasCallbackQuery()) {
            var receivedMessage = update.getCallbackQuery().getData();
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
                startBot(chatId, userName);
                break;
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
