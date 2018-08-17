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
	private int contHorario = 0;
	
    public void onUpdateReceived(Update update) {
    	
		// We check if the update has a message and the message has text
	    if (update.hasMessage() && update.getMessage().hasText()) {
	        String message_text = update.getMessage().getText();
	        long chat_id = update.getMessage().getChatId();
	        SendMessage message = new SendMessage();
	        
	        if(process == null) {
		        if (update.getMessage().getText().equals("/new")) {
		        	this.process = "/new_film";
		        	message.setChatId(chat_id)
		                   .setText(EmojiParser.parseToUnicode(":clapper: Nombre de la película"));		
		        	try {
		                execute(message); // Manda el objeto mensaje al usuario
		            } catch (TelegramApiException e) {
		                e.printStackTrace();
		            }
		        } else {
		
		        }
	        } else {	// Existe un proceso
		    	if(this.process.equals("/new_film")) {
		    		this.process_response = "";
		    		this.film.setName(message_text);
		    		this.process_response += EmojiParser.parseToUnicode(":clapper: ") + this.film.getName() + "\n";
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
		            
		            try {
		                execute(message); // Manda el objeto mensaje al usuario
		            } catch (TelegramApiException e) {
		                e.printStackTrace();
		            }
		    	} else if (this.process.equals("/new_edadMinima")) {
		    		this.film.setEdadMinima(Integer.parseInt(message_text));
		    		this.process_response += EmojiParser.parseToUnicode("\n:underage: +") + this.film.getEdadMinima() + " años.";
		    		this.process = null;
		    		message.setChatId(chat_id).setText("Cine de...");
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
		            
		            try {
		                execute(message); // Manda el objeto mensaje al usuario
		            } catch (TelegramApiException e) {
		                e.printStackTrace();
		            }
		    	}		    	
		    }
	
	    } else if (update.hasCallbackQuery()) {
	        // Set variables
	        String call_data = update.getCallbackQuery().getData();
	        int message_id = update.getCallbackQuery().getMessage().getMessageId();
	        long chat_id = update.getCallbackQuery().getMessage().getChatId();
	        
	        if (call_data.equals("/new_estrenoNacionalSi")) {
	        	this.film.setEstrenoNacional(true);
	        	this.process = "/new_edadMinima";
	        	this.process_response += "\nEstreno nacional";
	        	editMessage(chat_id, message_id, "Edad mínima:");
	        } else if (call_data.equals("/new_estrenoNacionalNo")) {
	            this.process = "/new_edadMinima";
	            this.film.setEstrenoNacional(false);
	            editMessage(chat_id, message_id, "Edad mínima:");
	        } else if (call_data.equals("/new_cineVerano")) {
	        	this.film.setCine(false);
	            this.process = null;
	            this.process_response += EmojiParser.parseToUnicode("\n:sunny: Cine de verano.");
	            horario(chat_id, message_id);
	        } else if (call_data.equals("/new_cineInvierno")) {
	        	this.film.setCine(true);
	            this.process = null;
	            this.process_response += EmojiParser.parseToUnicode("\n:snowflake: Cine de invierno.");
	            horario(chat_id, message_id);
	        } else if(call_data.startsWith("/new_diaMes")) {
	        	callBackDiaMes(call_data, chat_id);
	        } else {
	        	editMessage(chat_id, message_id, "Callback erróneo.");
	        }
	    } else {
	    	long message_id = update.getCallbackQuery().getMessage().getMessageId();
	        long chat_id = update.getCallbackQuery().getMessage().getChatId();
	    	EditMessageText new_message = new EditMessageText()
                    .setChatId(chat_id)
                    .setMessageId(Math.toIntExact(message_id))
	                .setText(EmojiParser.parseToUnicode("No sé qué decirte a eso."));
	         try {
	             execute(new_message);
	         } catch (TelegramApiException e) {
	             e.printStackTrace();
	         }
	    }
    }
    
    public void sendFilm(long chat_id) {
    	SendMessage message = new SendMessage().setChatId(chat_id)
                .setText(this.process_response);
	     try {
	         execute(message);
	     } catch (TelegramApiException e) {
	         e.printStackTrace();
	     }
    }
    
    public void editMessage(long chat_id, long message_id, String message) {
    	EditMessageText new_message = new EditMessageText()
                .setChatId(chat_id)
                .setMessageId(Math.toIntExact(message_id))
                .setText(message);
        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMessage(long chat_id, String message) {
    	SendMessage send_message = new SendMessage().setChatId(chat_id).setText(message);
        try {
            execute(send_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    public void horario(long chat_id, int message_id) {
    	EditMessageText new_message = new EditMessageText();
		this.process = null;
		new_message.setChatId(chat_id).setMessageId(message_id)
        .setText("Día de la semana del horario " + ++contHorario + ":");
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<InlineKeyboardButton>();
        rowInline1.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("1")).setCallbackData("/new_diaMes1"));
        rowInline1.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("2")).setCallbackData("/new_diaMes2"));
        rowInline1.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("3")).setCallbackData("/new_diaMes3"));
        rowInline1.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("4")).setCallbackData("/new_diaMes4"));
        rowInline1.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("5")).setCallbackData("/new_diaMes5"));
        rowInline1.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("6")).setCallbackData("/new_diaMes6"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        
        List<InlineKeyboardButton> rowInline2 = new ArrayList<InlineKeyboardButton>();
        rowInline2.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("7")).setCallbackData("/new_diaMes7"));
        rowInline2.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("8")).setCallbackData("/new_diaMes8"));
        rowInline2.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("9")).setCallbackData("/new_diaMes9"));
        rowInline2.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("10")).setCallbackData("/new_diaMes10"));
        rowInline2.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("11")).setCallbackData("/new_diaMes11"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline2);
        
        List<InlineKeyboardButton> rowInline3 = new ArrayList<InlineKeyboardButton>();
        rowInline3.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("12")).setCallbackData("/new_diaMes12"));
        rowInline3.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("13")).setCallbackData("/new_diaMes13"));
        rowInline3.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("14")).setCallbackData("/new_diaMes14"));
        rowInline3.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("15")).setCallbackData("/new_diaMes15"));
        rowInline3.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("16")).setCallbackData("/new_diaMes16"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline3);
        
        List<InlineKeyboardButton> rowInline4 = new ArrayList<InlineKeyboardButton>();
        rowInline4.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("17")).setCallbackData("/new_diaMes17"));
        rowInline4.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("18")).setCallbackData("/new_diaMes18"));
        rowInline4.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("19")).setCallbackData("/new_diaMes19"));
        rowInline4.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("20")).setCallbackData("/new_diaMes20"));
        rowInline4.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("21")).setCallbackData("/new_diaMes21"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline4);
        
        List<InlineKeyboardButton> rowInline5 = new ArrayList<InlineKeyboardButton>();
        rowInline5.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("22")).setCallbackData("/new_diaMes22"));
        rowInline5.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("23")).setCallbackData("/new_diaMes23"));
        rowInline5.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("24")).setCallbackData("/new_diaMes24"));
        rowInline5.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("25")).setCallbackData("/new_diaMes25"));
        rowInline5.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("26")).setCallbackData("/new_diaMes26"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline5);
        
        List<InlineKeyboardButton> rowInline6 = new ArrayList<InlineKeyboardButton>();
        rowInline6.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("27")).setCallbackData("/new_diaMes27"));
        rowInline6.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("28")).setCallbackData("/new_diaMes28"));
        rowInline6.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("29")).setCallbackData("/new_diaMes29"));
        rowInline6.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("30")).setCallbackData("/new_diaMes30"));
        rowInline6.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("31")).setCallbackData("/new_diaMes31"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline6);
        
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        new_message.setReplyMarkup(markupInline);
        
        try {
            execute(new_message); // Manda el objeto mensaje al usuario
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    public void callBackDiaMes(String call_data, long chat_id) {
    	this.process_response += EmojiParser.parseToUnicode("\n:calendar: " + call_data.split("/new_diaMes")[1]);
    	sendFilm(chat_id);
    }

    public String getBotUsername() {
        return "cineranza_bot";
    }

    @Override
    public String getBotToken() {
        return "650866457:AAGXWkox5bYFa_xIE9AJ2kIcjnFWH8DXL6c";
    }
}
