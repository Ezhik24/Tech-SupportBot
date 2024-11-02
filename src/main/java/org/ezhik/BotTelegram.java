package org.ezhik;

import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

public class BotTelegram extends TelegramLongPollingBot {
    private String token = "changeme";
    private String username = "changeme";
    private ArrayList<String> answers = new ArrayList<>();
    private String adminprefix;

    public BotTelegram(Update update) {
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
        YamlConfiguration users = new YamlConfiguration();
        File file = new File("users/" + update.getMessage().getChatId() + ".yml");
        if (!file.exists()) {
            try {
                users.load(file);
                users.set("chatid", update.getMessage().getChatId());
                users.set("firstname", update.getMessage().getChat().getFirstName());
                users.set("lastname", update.getMessage().getChat().getLastName());
                users.set("username", update.getMessage().getChat().getUserName());
                users.set("admin", false);
                users.set("root", false);
                users.set("adminprefix", adminprefix);
                users.set("answers", answers);
            } catch (NoSuchFileException e) {
                users.set("chatid", update.getMessage().getChatId());
                users.set("firstname", update.getMessage().getChat().getFirstName());
                users.set("lastname", update.getMessage().getChat().getLastName());
                users.set("username", update.getMessage().getChat().getUserName());
                users.set("admin", false);
                users.set("root", false);
                users.set("adminprefix", adminprefix);
                users.set("answers", answers);
            } catch (IOException e) {
                System.out.println("Error loading config file: " + e);
            } catch (InvalidConfigurationException e) {
                System.out.println("Error loading config file: " + e);
            }
            try {
                users.save(file);
            } catch (IOException e) {
                System.out.println("Error saving config file: " + e);
            }
        }
        if (update.getMessage().getText().toString().startsWith("/")) {
            if (update.getMessage().getText().toString().equals("/start")) {
                sendMessage(update.getMessage().getChatId(), "[Бот] Привет! Вы попали в Тех-Поддержку! Чтобы задать ваш вопрос воспользуйтесь командой /answer");
            }
            if (update.getMessage().getText().equals("/answer")) {
                sendMessage(update.getMessage().getChatId(), "[Бот] Пожалуйста, введите ваш вопрос");
            } else {
                if (update.getMessage().getText().startsWith("/answer ")) {
                    String text = update.getMessage().getText().substring(8);
                    sendMessage(update.getMessage().getChatId(), "[Бот] Вы успешно задали вопрос: " + text);
                    answers.add(text);
                    System.out.println(answers);
                    setAnswers(update);
                }
            }
            if (update.getMessage().getText().equals("/admin")) {
                sendMessage(update.getMessage().getChatId(), "[root] Пожалуйста, введите ID пользователя");
            } else {
                if (update.getMessage().getText().startsWith("/admin " + update.getMessage().getText().substring(7))) {
                     if (isRoot(update).equals(true)) {
                         setAdmin(update);
                     } else {
                         sendMessage(update.getMessage().getChatId(), "[root] Вы не являетесь администратором!");
                     }
                }
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

    public void setAnswers(Update update) {
        YamlConfiguration answersconf = new YamlConfiguration();
        File answersfile = new File( "users/" + update.getMessage().getChatId() + ".yml");
        try {
            answersconf.load(answersfile);
            answersconf.set("answers", this.answers);
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading config file: " + e);
        }
        System.out.println("BotTelegram.setAnswers: " + answersconf.getStringList("answers"));
        System.out.println(answersconf.get("answers"));
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAdmin(Update update) {
        YamlConfiguration users = new YamlConfiguration();
        File file = new File("users/" + update.getMessage().getText().substring(7) + ".yml");
        try {
            users.load(file);
            users.set("admin", true);
            long user = Long.parseLong(update.getMessage().getText().substring(7));
            sendMessage(update.getMessage().getChatId(), "[root] Вы успешно сделали данного пользователя администратором");
            sendMessage(user, "[Admin] Вы стали администратором");
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading config file: " + e);
        }
        try {
            users.save(file);
        } catch (IOException e) {
            System.out.println("Error saving config file: " + e);
        }
    }
    public Boolean isRoot(Update update) {
        YamlConfiguration users = new YamlConfiguration();
        File file = new File("users/" + update.getMessage().getChatId() + ".yml");
        try {
            users.load(file);
            return users.getBoolean("root");
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading config file: " + e);
        }
        return false;
    }
}
