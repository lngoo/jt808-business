package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.antnest.msger.core.dto.persistence.TopicUserData;
import com.antnest.msger.core.enums.OperateType;
import com.antnest.msger.core.enums.SubjectType;
import com.antnest.msger.core.message.MsgerTaskMsg;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

@SpringBootTest
class SendTaskTests {

    @Value("${redis.key.queue.response}")
    private String redisKeyResponse;

    @Value("${redis.key.queue.task}")
    private String redisKeyTask;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        System.out.println(redisKeyResponse);
    }

    /**
     * topic注册
     */
    @Test
    void sendAddTopicTask() throws Exception {
        TopicUserData data = new TopicUserData("0", "应用注册码AA", "主题名称", "业务ID", "业务类型",
                1, new Date(), "a,b");

        MsgerTaskMsg msg = new MsgerTaskMsg(OperateType.TOPIC_REGISTER, SubjectType.TOPIC_USER, data);

        String json = JSONObject.toJSONStringWithDateFormat(msg, "yyyy-MM-dd HH:mm:ss");
        System.out.println(json);

        byte[] bytes = json.getBytes("UTF-8");

        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeyTask, bytes);
        System.out.println("finished..");
    }

    /**
     * topic更改
     */
    @Test
    void sendUpdateTopicTask() throws Exception {
        TopicUserData data = new TopicUserData();
        data.setTopicId("00398998633158152192"); // TODO  此处要修改
        data.setTopicName("AAA");
        data.setType(0);
        data.setExpiresTime(null);

        MsgerTaskMsg msg = new MsgerTaskMsg(OperateType.TOPIC_UPDATE_TYPE, SubjectType.TOPIC_USER, data);

        String json = JSONObject.toJSONStringWithDateFormat(msg, "yyyy-MM-dd HH:mm:ss");
        System.out.println(json);

        byte[] bytes = json.getBytes("UTF-8");

        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeyTask, bytes);
        System.out.println("finished..");
    }

    /**
     * topic adduser
     */
    @Test
    void sendTopicAddUserTask() throws Exception {
        TopicUserData data = new TopicUserData();
        data.setTopicId("00398998633158152192"); // TODO  此处要修改
        data.setMember("c,d");

        MsgerTaskMsg msg = new MsgerTaskMsg(OperateType.TOPIC_ADDUSER, SubjectType.TOPIC_USER, data);

        String json = JSONObject.toJSONStringWithDateFormat(msg, "yyyy-MM-dd HH:mm:ss");
        System.out.println(json);

        byte[] bytes = json.getBytes("UTF-8");

        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeyTask, bytes);
        System.out.println("finished..");
    }

    /**
     * topic del user
     */
    @Test
    void sendTopicDelUserTask() throws Exception {
        TopicUserData data = new TopicUserData();
        data.setTopicId("00398998633158152192"); // TODO  此处要修改
        data.setMember("c,a");

        MsgerTaskMsg msg = new MsgerTaskMsg(OperateType.TOPIC_REMOVEUSER, SubjectType.TOPIC_USER, data);

        String json = JSONObject.toJSONStringWithDateFormat(msg, "yyyy-MM-dd HH:mm:ss");
        System.out.println(json);

        byte[] bytes = json.getBytes("UTF-8");

        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeyTask, bytes);
        System.out.println("finished..");
    }

    /**
     * topic注销
     */
    @Test
    void sendDelTopicTask() throws Exception {
        TopicUserData data = new TopicUserData();
        data.setTopicId("00398998633158152192"); // TODO  此处要修改

        MsgerTaskMsg msg = new MsgerTaskMsg(OperateType.TOPIC_RELEASE, SubjectType.TOPIC_USER, data);

        String json = JSONObject.toJSONStringWithDateFormat(msg, "yyyy-MM-dd HH:mm:ss");
        System.out.println(json);

        byte[] bytes = json.getBytes("UTF-8");

        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeyTask, bytes);
        System.out.println("finished..");
    }
}
