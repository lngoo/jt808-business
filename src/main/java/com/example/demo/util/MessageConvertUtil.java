package com.example.demo.util;

import com.antnest.msger.core.dto.jt808.basics.Message;
import com.antnest.msger.core.message.ChannelMessage;

import java.util.Map;

/**
 * 内外message对象转换
 */
public class MessageConvertUtil {

    public static Message toInternal (ChannelMessage external, Map<String, Integer> protocolMap){
        Message message = new Message();
        message.setDelimiter(protocolMap.get(external.getProtocolType()));
        message.setType(external.getCmd());
        message.setMobileNumber(external.getUserAlias());
        message.setBody(external.getMsgBody());
        return message;
    }

    public static ChannelMessage toExternal (Message message, String msgerId, String sessionId){
        ChannelMessage external = new ChannelMessage();
        external.setCmd(message.getType());
        external.setUserAlias(message.getMobileNumber());
        external.setMsgerId(msgerId);
        external.setSessionId(sessionId);
        external.setMsgBody(message.getBody());
        return external;
    }


}
