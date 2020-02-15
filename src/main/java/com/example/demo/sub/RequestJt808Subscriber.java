package com.example.demo.sub;

import com.ant.msger.base.common.MessageId;
import com.ant.msger.base.dto.jt808.CommonResult;
import com.antnest.msger.proto.ProtoMain;
import com.example.demo.cache.Cache;
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

public class RequestJt808Subscriber implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger("RequstJt808Subscriber");

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${redis.key.queue.response}")
    private String redisKeyResponse;

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
                case (MessageId.位置信息汇报):
                    doClientPositionReport(message1);
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
            mBuilder.setProtocolType("Jt808");
            mBuilder.setUserAlias(mobileNum);
//            mBuilder.setSerialNumber();  不用设置流水号，
            mBuilder.setSendTo(msg.getSessionId());
            mBuilder.setMsgBody(Any.pack(commonResult));

            ProtoMain.Message message = mBuilder.build();
            redisTemplate.setValueSerializer(null);
            redisTemplate.setEnableDefaultSerializer(false);
            redisTemplate.convertAndSend(redisKeyResponse.concat(":").concat(msg.getMsgerId()), message.toByteArray());
            LOG.info("### send one data to response channel.msg={}", message.toByteArray());
//            com.ant.msger.base.dto.jt808.basics.Message result = new com.ant.msger.base.dto.jt808.basics.Message(MessageId.平台通用应答, mobileNum, commonResult);
//            result.setDelimiter(msg.getDelimiter());
//            AntSendChannelMsg sendChannelMsg = new AntSendChannelMsg(SendType.TO_SESSION, null, result);
//            stringRedisTemplate.opsForList().leftPush(redisResponseKey, xstream.toXML(sendChannelMsg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doClientPositionReport(ProtoMain.Message msg) {
        try {
            String mobileNum = msg.getUserAlias();
            // 位置汇报应答
            ProtoMain.CommonResult.Builder builder = ProtoMain.CommonResult.newBuilder();
            builder.setReplyId(MessageId.位置信息汇报);
            builder.setFlowId(msg.getSerialNumber());
            builder.setResultCode(CommonResult.Success);
            // 调用统一鉴权处理方法，无权限不处理
            if (!isAuthedClient(mobileNum)) {
                builder.setResultCode(CommonResult.Fial);
            } else {
                // do something
            }
            ProtoMain.CommonResult commonResult = builder.build();

            ProtoMain.Message.Builder mBuilder = ProtoMain.Message.newBuilder();
            mBuilder.setCmd(MessageId.平台通用应答);
            mBuilder.setProtocolType("Jt808");
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

    private boolean isAuthedClient(String mobileNum) {
        return Cache.mapAuthed.contains(mobileNum);
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
            mBuilder.setProtocolType("Jt808");
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
