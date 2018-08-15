package telegrambot.cineranza;


import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.EmojiParser;

import telegrambot.cineranza.classes.Film;

public class CineranzaBot extends TelegramLongPollingBot {
	
	private String process = null;
	private Film film = new Film();
	private String process_response = "";
	
    public void onUpdateReceived(Update update) {
    	
		// We check if the update has a message and the message has text
	    if (update.hasMessage() && update.getMessage().hasText()) {
	        String message_text = update.getMessage().getText();
	        long chat_id = update.getMessage().getChatId();
	        SendMessage message = new SendMessage(); // Create a message object object
	        
	        if(process == null) {
		        if (update.getMessage().getText().equals("/new")) {
		        	this.process = "/new_film";
		            message.setChatId(chat_id)
		                   .setText(EmojiParser.parseToUnicode(":clapper: Nombre de la película"));		           
		        } else {
		
		        }
	        } else {	// Existe un proceso
		    	if(this.process.equals("/new_film")) {
		    		this.process_response = "";
		    		this.film.setName(message_text);
		    		this.process_response += EmojiParser.parseToUnicode(":clapper: ") + this.film.getName();
		    		this.process = null;
		    		message.setChatId(chat_id)
	                .setText("¿Estreno nacional?");
		    		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
		            List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
		            rowInline.add(new InlineKeyboardButton().setText("Sí").setCallbackData("/new_estrenoNacionalSi"));
		            rowInline.add(new InlineKeyboardButton().setText("No").setCallbackData("/new_estrenoNacionalNo"));
		            // Set the keyboard to the markup
		            rowsInline.add(rowInline);
		            // Add it to the message
		            markupInline.setKeyboard(rowsInline);
		            message.setReplyMarkup(markupInline);
		    	} else if (this.process.equals("/new_edadMinima")) {
		    		this.film.setEdadMinima(Integer.parseInt(message_text));
		    		this.process_response += EmojiParser.parseToUnicode("\n:underage: +") + this.film.getEdadMinima() + " años.";
		    		this.process = null;
		    		message.setChatId(chat_id)
	                .setText("Cine de...");
		    		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
		            List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
		            rowInline.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode(":sunny: Verano")).setCallbackData("/new_cineVerano"));
		            rowInline.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode(":snowflake: Invierno")).setCallbackData("/new_cineInvierno"));
		            // Set the keyboard to the markup
		            rowsInline.add(rowInline);
		            // Add it to the message
		            markupInline.setKeyboard(rowsInline);
		            message.setReplyMarkup(markupInline);
		    	}
		    	
		    	
		    }
	        
	        try {
                execute(message); // Manda el objeto mensaje al usuario
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
	
	    } else if (update.hasCallbackQuery()) {
	        // Set variables
	        String call_data = update.getCallbackQuery().getData();
	        long message_id = update.getCallbackQuery().getMessage().getMessageId();
	        long chat_id = update.getCallbackQuery().getMessage().getChatId();
	        String answer = "Callback erróneo";
	        
	        if (call_data.equals("update_msg_text")) {
	            answer = "Respuesta actualizada";
	            EditMessageText new_message = new EditMessageText()
	                    .setChatId(chat_id)
	                    .setMessageId(Math.toIntExact(message_id))
	                    .setText(answer);
	            try {
	                execute(new_message);
	            } catch (TelegramApiException e) {
	                e.printStackTrace();
	            }
	        } else if (call_data.equals("/new_estrenoNacionalSi")) {
	        	this.film.setEstrenoNacional(true);
	        	this.process_response += "\n\nEstreno nacional";
	            SendMessage message = new SendMessage().setChatId(chat_id)
		                   .setText("Edad mínima.");
	            this.process = "/new_edadMinima";
	            try {
	                execute(message);
	            } catch (TelegramApiException e) {
	                e.printStackTrace();
	            }
	        } else if (call_data.equals("/new_estrenoNacionalNo")) {
	            this.process = "/new_edadMinima";
	            SendMessage message = new SendMessage().setChatId(chat_id)
		                   .setText("Edad mínima.");
	            try {
	                execute(message);
	            } catch (TelegramApiException e) {
	                e.printStackTrace();
	            }
	        } else if (call_data.equals("/new_cineVerano")) {
	        	this.film.setCine(false);
	            this.process = null;
	            this.process_response += EmojiParser.parseToUnicode("\n:sunny: Cine de verano.");
	            SendMessage message = new SendMessage().setChatId(chat_id)
		                   .setText(this.process_response);
	            try {
	                execute(message);
	            } catch (TelegramApiException e) {
	                e.printStackTrace();
	            }
	        } else if (call_data.equals("/new_cineInvierno")) {
	        	this.film.setCine(true);
	            this.process = null;
	            this.process_response += EmojiParser.parseToUnicode("\n:snowflake: Cine de invierno.");
	            SendMessage message = new SendMessage().setChatId(chat_id)
		                   .setText(this.process_response);
	            try {
	                execute(message);
	            } catch (TelegramApiException e) {
	                e.printStackTrace();
	            }
	        }
	    } else {
	    	SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId())
	                   .setText(EmojiParser.parseToUnicode("No sé qué decirte a eso."));
	         try {
	             execute(message);
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
