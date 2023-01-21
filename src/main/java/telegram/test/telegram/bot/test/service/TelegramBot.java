package telegram.test.telegram.bot.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.test.telegram.bot.test.buttons.Buttons;
import telegram.test.telegram.bot.test.config.BotConfig;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {


    final BotConfig config;

    @Autowired
    Buttons buttons;

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
        long chatId = update.getMessage().getChatId();
        sendMessage(chatId, "Какую таблетку выпить?");
        SendMessage message = new SendMessage();
        buttons.setButtons(message);


        log.info("Replied to user" + update.getMessage().getChat().getFirstName());
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException exception) {
            log.error("Случилась ошибка " + exception.getMessage());

        }
    }

}
