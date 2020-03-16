package com.example.demo;

import com.antnest.msger.core.common.MessageId;
import com.antnest.msger.core.dto.jt808.CommonResult;
import com.antnest.msger.proto.ProtoMain;
import com.google.protobuf.Any;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class Send2UserTopicTests {

    @Value("${redis.key.queue.send.user}")
    private String redisKeySendUser;

    @Value("${redis.key.queue.send.topic}")
    private String redisKeySendTopic;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送到用户
     * 前置条件：2个 相同用户 客户端连接
     */
    @Test
    void send2User() {
        // 注册成功
        ProtoMain.RegisterResult.Builder builder = ProtoMain.RegisterResult.newBuilder();
        builder.setSerialNumber(666);
        builder.setResultCode(CommonResult.Success);
        builder.setToken("12345678913888888888_authed");
        ProtoMain.RegisterResult registerResult = builder.build();

        ProtoMain.Message.Builder mBuilder = ProtoMain.Message.newBuilder();
        mBuilder.setCmd(MessageId.终端注册应答);
        mBuilder.setProtocolType("Jt808");
        mBuilder.setUserAlias("00001000020000300004");
//            mBuilder.setSerialNumber();  不用设置流水号，
        mBuilder.setSendTo("12345678913888888888");
        mBuilder.setMsgBody(Any.pack(registerResult));

        ProtoMain.Message message = mBuilder.build();
        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeySendUser, message.toByteArray());
    }


    /**
     * 发送到topic
     * 前置条件：2个客户端连接,且TOPIC_USER数据已准备好
     */
    @Test
    void send2Topic() {
        // 注册成功
        ProtoMain.RegisterResult.Builder builder = ProtoMain.RegisterResult.newBuilder();
        builder.setSerialNumber(666);
        builder.setResultCode(CommonResult.Success);
        builder.setToken("12345678913888888888_authed");
        ProtoMain.RegisterResult registerResult = builder.build();

        ProtoMain.Message.Builder mBuilder = ProtoMain.Message.newBuilder();
        mBuilder.setCmd(MessageId.终端注册应答);
        mBuilder.setProtocolType("Jt808");
        mBuilder.setUserAlias("00001000020000300004");
//            mBuilder.setSerialNumber();  不用设置流水号，
        mBuilder.setSendTo("qunliao");
        mBuilder.setMsgBody(Any.pack(registerResult));

        ProtoMain.Message message = mBuilder.build();
        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeySendTopic, message.toByteArray());
    }
}
