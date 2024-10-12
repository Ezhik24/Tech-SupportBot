package org.example;

import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

public class BotTelegram extends TelegramLongPollingBot {
    private String token = "changeme";
    private String username = "changeme";

    public BotTelegram() {
        System.out.println("Bot started");
        YamlConfiguration config = new YamlConfiguration();
        File file = new File("config.yml");
        if (!file.exists()) {
            config.set("username", username);
            config.set("token", token);
            try {
                config.save(file);
            } catch (Exception e) {
                System.out.println("Error creating config file: " + e);
            }
        } else {
            try {
                config.load(file);
            } catch (IOException e) {
                System.out.println("Error loading config file: " + e);
            } catch (InvalidConfigurationException e) {
                System.out.println("Error loading config file: " + e);
            }
            username = config.getString("username");
            token = config.getString("token");
        }
    }


    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().getText().toString().startsWith("/")) {
            if (update.getMessage().getText().toString().equals("/start")) {
                sendMessage(update.getMessage().getChatId(), "[Бот] Привет! Вы попали в Тех-Поддержку Бота CS2SHOP! Чтобы задать ваш вопрос воспользуйтесь командой /answer");
            }
            if (update.getMessage().getText().equals("/answer")) {
                sendMessage(update.getMessage().getChatId(), "[Бот] Пожалуйста, введите ваш вопрос");
            } else {
                if (update.getMessage().getText().startsWith("/answer ")) {
                    String text = update.getMessage().getText().substring(8);
                    sendMessage(update.getMessage().getChatId(), "[Бот] Вы успешно задали вопрос: " + text);
                    System.out.println("Получен текст: " + text);
                }
            }
            if (update.getMessage().getText().toString().startsWith("/help")) {
                sendMessage(update.getMessage().getChatId(), "[Бот] Список команд: \n /answer {вопрос} - Задать вопрос \n ");
            }
        }
    }

    public void sendMessage(Long Chatid, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(Chatid);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e);
        }


    }
}
