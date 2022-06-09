package com.vp.chatroom.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
	private String sender;
	private String recipient;
	private String message;
	private String timestamp;

	public Message() {
		// Default constructor for serialization
	}
	public Message(String sender, String recipient, String message) {
		this.sender = sender;
		this.recipient = recipient;
		this.message  = message;
		this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	public Message(String sender, String recipient, String message, String timestamp) {
		this.sender = sender;
		this.recipient = recipient;
		this.message  = message;
		this.timestamp = timestamp;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Message: {" +
				"sender: " + sender +
				", recepient: " + recipient +
				", message: "  + message +
				", timestamp: "  + timestamp +
				"}";
	}
}
