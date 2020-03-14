package com.example.demo.sub;

import com.ant.msger.base.common.MessageId;
import com.ant.msger.base.dto.jt808.CommonResult;
import com.ant.msger.base.dto.jt808.IMMsg;
import com.ant.msger.base.message.MessageExternal;
import com.antnest.msger.proto.ProtoMain;
import com.example.demo.cache.Cache;
import com.example.demo.util.LocalProtoBufUtil;
import com.example.demo.util.MessageConvertUtil;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

public class RequestIMSubscriber implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger("RequstIMSubscriber");

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${redis.key.queue.response}")
    private String redisKeyResponse;

    @Value("${redis.key.queue.send.user}")
    private String redisKeySendUser;

    @Value("${redis.key.queue.send.topic}")
    private String redisKeySendTopic;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        LOG.info("### receive one data. msg={}", message.getBody());
        System.out.println("#################### stastt ");

        byte[] body = message.getBody();
        try {
            ProtoMain.Message message1 = ProtoMain.Message.parseFrom(body);
            Integer type = message1.getCmd();

            switch (type) {
                case (MessageId.终端注册):
                    doClientRegister(message1);
                    break;
                case (MessageId.IM消息):
                    doIMMsgSend(message1);
                    break;
                case (MessageId.终端鉴权):
                    doClientAuthentication(message1);
                    break;
                default:
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        System.out.println("#################### end ^ ");
    }

    private void doClientAuthentication(ProtoMain.Message msg) {
        try {
            String mobileNum = msg.getUserAlias();
            ProtoMain.Authentication authentication = msg.getMsgBody().unpack(ProtoMain.Authentication.class);
            String token = authentication.getToken();
            // 鉴权应答
            ProtoMain.CommonResult.Builder builder = ProtoMain.CommonResult.newBuilder();
            builder.setReplyId(MessageId.终端鉴权);
            builder.setFlowId(msg.getSerialNumber());
            builder.setResultCode(CommonResult.Success);
            if (StringUtils.equalsIgnoreCase(Cache.mapRegister.get(token), mobileNum)) {
                Cache.mapAuthed.add(mobileNum);
            } else {
                builder.setResultCode(CommonResult.Fial);
            }
            ProtoMain.CommonResult commonResult = builder.build();

            ProtoMain.Message.Builder mBuilder = ProtoMain.Message.newBuilder();
            mBuilder.setCmd(MessageId.平台通用应答);
            mBuilder.setProtocolType("AntIM");
            mBuilder.setUserAlias(mobileNum);
//            mBuilder.setSerialNumber();  不用设置流水号，
            mBuilder.setSendTo(msg.getSessionId());
            mBuilder.setMsgBody(Any.pack(commonResult));

            ProtoMain.Message message = mBuilder.build();
            redisTemplate.setValueSerializer(null);
            redisTemplate.setEnableDefaultSerializer(false);
            redisTemplate.convertAndSend(redisKeyResponse.concat(":").concat(msg.getMsgerId()), message.toByteArray());
            LOG.info("### send one data to response channel.msg={}", message.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doIMMsgSend(ProtoMain.Message message1) {
        try {
            String mobileNum = message1.getUserAlias();

            // 平台通用应答
            commonIMResponse(message1, mobileNum);

            // 发送消息给对应的机器
            MessageExternal external = LocalProtoBufUtil.copyProtoBeanToMessageExternal(message1);

            Map<String, Integer> protocolMap = new HashMap<>();
            protocolMap.put("Jt808", 0x7e);
            protocolMap.put("AntIM", 0x7a);
//            com.ant.msger.base.dto.jt808.basics.Message message2 = MessageConvertUtil.toInternal(external, GlobalConfig.protocolBusinessMap());
            com.ant.msger.base.dto.jt808.basics.Message message2 = MessageConvertUtil.toInternal(external, protocolMap);
            if (message2.getBody() instanceof IMMsg) {
                IMMsg imMsg = (IMMsg) message2.getBody();
                int sendType = imMsg.getSendType();
                if (sendType == 2) {
                    send2User(imMsg);
                } else {
                    send2Topic(imMsg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send2Topic(IMMsg imMsg) {
        String sendTo = imMsg.getSendTo();
        String msg = imMsg.getMsg();
        String sendUserAlias = imMsg.getSendUserAlias();
        String sendUserName = imMsg.getSendUserName();

        // 消息
        ProtoMain.IMMsg.Builder builder = ProtoMain.IMMsg.newBuilder();
        builder.setMsg(msg);
        builder.setSendUserAlias(sendUserAlias);
        builder.setSendUserName(sendUserName);
        ProtoMain.IMMsg imMsg1 = builder.build();

        ProtoMain.Message.Builder mBuilder = ProtoMain.Message.newBuilder();
        mBuilder.setCmd(MessageId.IM消息);
        mBuilder.setProtocolType("AntIM");
        mBuilder.setUserAlias("00001000020000300005");
//            mBuilder.setSerialNumber();  不用设置流水号，
        mBuilder.setSendTo(sendTo);
        mBuilder.setMsgBody(Any.pack(imMsg1));

        ProtoMain.Message message = mBuilder.build();
        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeySendTopic, message.toByteArray());
    }

    private void send2User(IMMsg imMsg) {
        String sendTo = imMsg.getSendTo();
        String msg = imMsg.getMsg();
        String sendUserAlias = imMsg.getSendUserAlias();
        String sendUserName = imMsg.getSendUserName();

        // 消息
        ProtoMain.IMMsg.Builder builder = ProtoMain.IMMsg.newBuilder();
        builder.setMsg(msg);
        builder.setSendUserAlias(sendUserAlias);
        builder.setSendUserName(sendUserName);
        ProtoMain.IMMsg imMsg1 = builder.build();

        ProtoMain.Message.Builder mBuilder = ProtoMain.Message.newBuilder();
        mBuilder.setCmd(MessageId.IM消息);
        mBuilder.setProtocolType("AntIM");
        mBuilder.setUserAlias("00001000020000300005");
//            mBuilder.setSerialNumber();  不用设置流水号，
        mBuilder.setSendTo(sendTo);
        mBuilder.setMsgBody(Any.pack(imMsg1));

        ProtoMain.Message message = mBuilder.build();
        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeySendUser, message.toByteArray());
    }

    private void commonIMResponse(ProtoMain.Message msg, String mobileNum) {
        ProtoMain.CommonResult.Builder builder = ProtoMain.CommonResult.newBuilder();
        builder.setReplyId(MessageId.IM消息);
        builder.setFlowId(msg.getSerialNumber());
        builder.setResultCode(CommonResult.Success);

        ProtoMain.CommonResult commonResult = builder.build();

        ProtoMain.Message.Builder mBuilder = ProtoMain.Message.newBuilder();
        mBuilder.setCmd(MessageId.平台通用应答);
        mBuilder.setProtocolType("AntIM");
        mBuilder.setUserAlias(mobileNum);
//            mBuilder.setSerialNumber();  不用设置流水号，
        mBuilder.setSendTo(msg.getSessionId());
        mBuilder.setMsgBody(Any.pack(commonResult));

        ProtoMain.Message message = mBuilder.build();
        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeyResponse.concat(":").concat(msg.getMsgerId()), message.toByteArray());
        LOG.info("### send one data to response channel.msg={}", message.toByteArray());
    }

    /**
     * 终端注册
     *
     * @param msg
     */
    private void doClientRegister(ProtoMain.Message msg) {
        try {
            String mobileNum = msg.getUserAlias();
            //        String authKey = String.valueOf(System.currentTimeMillis());
            String authKey = mobileNum + "_authed";
            Cache.mapRegister.put(authKey, mobileNum);
            Cache.mapAuthed.add(mobileNum);

            // 位置汇报应答
            ProtoMain.RegisterResult.Builder builder = ProtoMain.RegisterResult.newBuilder();
            builder.setSerialNumber(msg.getSerialNumber());
            builder.setResultCode(CommonResult.Success);
            builder.setToken(authKey);

            ProtoMain.RegisterResult registerResult = builder.build();

            ProtoMain.Message.Builder mBuilder = ProtoMain.Message.newBuilder();
            mBuilder.setCmd(MessageId.终端注册应答);
            mBuilder.setProtocolType("AntIM");
            mBuilder.setUserAlias(mobileNum);
//            mBuilder.setSerialNumber();  不用设置流水号，
            mBuilder.setSendTo(msg.getSessionId());
            mBuilder.setMsgBody(Any.pack(registerResult));

            ProtoMain.Message message = mBuilder.build();
            redisTemplate.setValueSerializer(null);
            redisTemplate.setEnableDefaultSerializer(false);
            redisTemplate.convertAndSend(redisKeyResponse.concat(":").concat(msg.getMsgerId()), message.toByteArray());
            LOG.info("### send one data to response channel.msg={}", message.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
