package com.example.demo.util;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.MessageExternal;

import java.util.Map;

/**
 * 内外message对象转换
 */
public class MessageConvertUtil {

    public static Message toInternal (MessageExternal external, Map<String, Integer> protocolMap){
        Message message = new Message();
        message.setDelimiter(protocolMap.get(external.getProtocolType()));
        message.setType(external.getCmd());
        message.setMobileNumber(external.getUserAlias());
        message.setBody(external.getMsgBody());
        return message;
    }

    public static MessageExternal toExternal (Message message, String msgerId, String sessionId){
        MessageExternal external = new MessageExternal();
        external.setCmd(message.getType());
        external.setUserAlias(message.getMobileNumber());
        external.setMsgerId(msgerId);
        external.setSessionId(sessionId);
        external.setMsgBody(message.getBody());
        return external;
    }


}
