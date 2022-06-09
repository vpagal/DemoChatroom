package com.vp.chatroom.repositories;

import com.vp.chatroom.models.Message;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MessageRepository {
	void insertMessages(String memberName, Message message);
	List<Message> getMessagesByMemberName(String memberName);
	void deleteMessagesByMemberName(String memberName);
}
