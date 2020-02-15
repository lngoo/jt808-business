package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ant.msger.base.common.MessageId;
import com.ant.msger.base.dto.jt808.*;
import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.enums.SendType;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AntSendChannelMsg;
import com.example.demo.cache.Cache;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class Resovler implements ApplicationRunner {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${redis.key.queue.request.jt808}")
    String redisRequestKey;

    @Value("${redis.key.queue.response}")
    String redisResponseKey;

    private XStream xstream = new XStream(new StaxDriver());

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
//                                String data = stringRedisTemplate.opsForList().rightPop(redisRequestKey);
//                                if (StringUtils.isEmpty(data)) {
//                                    // 暂无数据，等待3秒
//                                    System.out.println("%%% no redis request data.sleep 3 seconds...");
//                                    Thread.sleep(3000);
//                                    continue;
//                                }
//
//                                // 消费消息
//                                msgConsume(data);
                                Thread.sleep(3000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
    }
//
//    private void msgConsume(String data) {
//        System.out.println("###  收到信息：" + data);
//
//        JSONObject msgObj = JSON.parseObject(data);
//        Integer type = msgObj.getInteger("type");
//
//        switch (type) {
//            case (MessageId.终端注册):
//                doClientRegister(data);
//                break;
////            case(MessageId.终端心跳) :  由antMsger处理
////                doClientHeart(msg);
////                break;
//            case (MessageId.位置信息汇报):
//                doClientPositionReport(data);
//                break;
//            case (MessageId.终端鉴权):
//                doClientAuthentication(data);
//                break;
//            default:
//        }
//    }
//
//    private void doClientAuthentication(String data) {
//        Message<Authentication> msg = JSON.parseObject(data, new TypeReference<Message<Authentication>>() {
//        });
//        String mobileNum = msg.getMobileNumber();
//        Authentication authentication = (Authentication) msg.getBody();
//        String token = authentication.getToken();
//        // 鉴权应答
//        CommonResult commonResult = new CommonResult(MessageId.终端鉴权, msg.getSerialNumber(), CommonResult.Success);
//        if (StringUtils.equalsIgnoreCase(Cache.mapRegister.get(token), mobileNum)) {
//            Cache.mapAuthed.add(mobileNum);
//        } else {
//            commonResult.setResultCode(CommonResult.Fial);
//        }
//
//
//        Message result = new Message(MessageId.平台通用应答, mobileNum, commonResult);
//        result.setDelimiter(msg.getDelimiter());
//        AntSendChannelMsg sendChannelMsg = new AntSendChannelMsg(SendType.TO_SESSION, null, result);
//        stringRedisTemplate.opsForList().leftPush(redisResponseKey, xstream.toXML(sendChannelMsg));
//    }
//
//    private void doClientPositionReport(String data) {
//        Message<PositionReport> msg = JSON.parseObject(data, new TypeReference<Message<PositionReport>>() {
//        });
//        String mobileNum = msg.getMobileNumber();
//        CommonResult commonResult = new CommonResult(MessageId.位置信息汇报, msg.getSerialNumber(), CommonResult.Success);
//
//        // 调用统一鉴权处理方法，无权限不处理
//        if (!isAuthedClient(mobileNum)) {
//            commonResult.setResultCode(CommonResult.Fial);
//        } else {
//            // do something
//        }
//
//        Message result = new Message(MessageId.平台通用应答, mobileNum, commonResult);
//        result.setDelimiter(msg.getDelimiter());
//        AntSendChannelMsg sendChannelMsg = new AntSendChannelMsg(SendType.TO_SESSION, null, result);
//        stringRedisTemplate.opsForList().leftPush(redisResponseKey, xstream.toXML(sendChannelMsg));
//    }
//
//    private boolean isAuthedClient(String mobileNum) {
//        return Cache.mapAuthed.contains(mobileNum);
//    }
//
//    /**
//     * 终端注册
//     */
//    private void doClientRegister(String data) {
//        Message<Register> msg = JSON.parseObject(data, new TypeReference<Message<Register>>() {
//        });
//        String mobileNum = msg.getMobileNumber();
//        AbstractBody body = msg.getBody();
//
////        String authKey = String.valueOf(System.currentTimeMillis());
//        String authKey = mobileNum + "_authed";
//        Cache.mapRegister.put(authKey, mobileNum);
//        Cache.mapAuthed.add(mobileNum);
//
//        // do something with body
//
//        RegisterResult registerResult = new RegisterResult(msg.getSerialNumber(), RegisterResult.Success, authKey);
//        Message result = new Message(MessageId.终端注册应答, mobileNum, registerResult);
//        result.setDelimiter(msg.getDelimiter());
//
//        AntSendChannelMsg sendChannelMsg = new AntSendChannelMsg(SendType.TO_SESSION, null, result);
//        stringRedisTemplate.opsForList().leftPush(redisResponseKey, xstream.toXML(sendChannelMsg));
//    }
}
