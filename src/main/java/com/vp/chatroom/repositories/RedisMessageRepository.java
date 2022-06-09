package com.vp.chatroom.repositories;

import com.vp.chatroom.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RedisMessageRepository implements MessageRepository{
	private static final Logger logger = LoggerFactory.getLogger(RedisMessageRepository.class);
	private final RedisTemplate<String, Message> redisTemplate;

	@Autowired
	public RedisMessageRepository(RedisTemplate<String, Message> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void insertMessages(String memberName, Message message) {
		redisTemplate.opsForHash().put(memberName, UUID.randomUUID().toString(), message);
	}

	@Override
	public List<Message> getMessagesByMemberName(String memberName) {
		return redisTemplate.opsForHash().values(memberName)
				.stream()
				.map(Message.class::cast)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteMessagesByMemberName(String memberName) {
		redisTemplate.opsForHash().keys(memberName)
				.forEach(key -> redisTemplate.opsForHash().delete(memberName, key));
	}
}
