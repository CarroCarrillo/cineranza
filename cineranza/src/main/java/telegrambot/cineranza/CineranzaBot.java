package telegrambot.cineranza;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CineranzaBot extends TelegramLongPollingBot {
	
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            if(message_text.equals("/new")) {
            	message_text = "Creando nueva película...";
            } else if (message_text.equals("/start")) {
            	message_text = "Has iniciado la aplicación.";
            }
            
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(message_text);
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return "cineranza_bot";
    }

    @Override
    public String getBotToken() {
        return "650866457:AAGXWkox5bYFa_xIE9AJ2kIcjnFWH8DXL6c";
    }
}
