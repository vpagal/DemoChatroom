package com.vp.chatroom.services.websockets;

import com.vp.chatroom.models.Member;
import com.vp.chatroom.models.Message;
import com.vp.chatroom.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class MessageController {
	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final MessageRepository messageRepository;
	private final Set<Member>  registeredMembers;
	private final Set<Member> offlineMembers;

	@Autowired
	public MessageController(SimpMessagingTemplate simpMessagingTemplate, MessageRepository messageRepository) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.messageRepository = messageRepository;
		this.registeredMembers = new HashSet<>();
		this.offlineMembers = new HashSet<>();
	}

	/**
	 * Endpoint for register when a new chatroom member join the room
	 *
	 * @param userName
	 */
	@MessageMapping("/register")
	public void registerUser(Member userName) {
		if(!registeredMembers.contains(userName)) {
			registeredMembers.add(userName);
			simpMessagingTemplate.convertAndSend("/topic/newMember", userName);
			simpMessagingTemplate.convertAndSend("/topic/chatroomMembers", registeredMembers);

			if(offlineMembers.contains(userName)) {
				List<Message> offlineMessages = messageRepository.getMessagesByMemberName(userName.getMemberName());
				offlineMessages.forEach(msg -> simpMessagingTemplate.convertAndSendToUser(msg.getRecipient(), "/message", msg));
        		messageRepository.deleteMessagesByMemberName(userName.getMemberName());
			}
			offlineMembers.remove(userName);
		}
	}

	/**
	 * Endpoint for unregister a user which leaves the room
	 *
	 * @param memberName
	 * @return
	 */
	@MessageMapping("/unregister")
	@SendTo("/topic/disconnectedMember")
	public Member unregisterUser(Member memberName) {
		registeredMembers.remove(memberName);
		offlineMembers.add(memberName);
		simpMessagingTemplate.convertAndSend("/topic/chatroomMembers", registeredMembers);
		return memberName;
	}

	/**
	 * Endpoint for messages to all members of the room
	 *
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@MessageMapping("/room")
	@SendTo("/topic/messages")
	public Message sendMessage(final Message message) throws Exception {
		Message outputMessage = new Message(message.getSender(), message.getRecipient(), message.getMessage());
		logger.debug("Send out message  : {}", outputMessage);
		return outputMessage;
	}

	/**
	 * Endpoint for private messages
	 *
	 * @param message
	 */
	@MessageMapping("/private-room")
	public void sendPrivateMessage(final Message message) {
		Message outputMessage = new Message(message.getSender(), message.getRecipient(), message.getMessage());
		logger.debug("Send out private message  : {} to user : {}", outputMessage, outputMessage.getRecipient());
		if (registeredMembers.contains(new Member(message.getRecipient()))) {
			simpMessagingTemplate.convertAndSendToUser(outputMessage.getRecipient(), "/message", outputMessage);
		} else if (offlineMembers.contains(new Member(message.getRecipient()))){
			// If there is such user but is not active at the moment store message in Redis
			messageRepository.insertMessages(message.getRecipient(), outputMessage);
		}
	}
}
