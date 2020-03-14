package com.example.demo.util;

import com.ant.msger.base.dto.jt808.*;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.MessageExternal;
import com.antnest.msger.proto.ProtoMain;
import com.google.protobuf.Any;
import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地化的转换类
 */
public class LocalProtoBufUtil {

    private static Map<String, Message.Builder> map = new HashMap<>();

    static {
        map.put(Register.class.getSimpleName(), ProtoMain.Register.newBuilder());
        map.put(CommonResult.class.getSimpleName(), ProtoMain.CommonResult.newBuilder());
        map.put(RegisterResult.class.getSimpleName(), ProtoMain.RegisterResult.newBuilder());
        map.put(Authentication.class.getSimpleName(), ProtoMain.Authentication.newBuilder());
        map.put(PositionReport.class.getSimpleName(), ProtoMain.PositionReport.newBuilder());
        map.put(IMMsg.class.getSimpleName(), ProtoMain.IMMsg.newBuilder());
    }

    public static ProtoMain.Message copyMessageExternalToProtoBean(MessageExternal messageExternal, ProtoMain.Message.Builder target) {
        AbstractBody body = messageExternal.getMsgBody();
        messageExternal.setMsgBody(null);
        ProtoMain.Message msgProto = ProtoBufUtil.copyJavaBeanToProtoBean(messageExternal, target);

        Class bodyClass = body.getClass();
        Message.Builder bodyBuilder = map.get(bodyClass.getSimpleName());
        bodyBuilder.clear();
        Message bodyProto = ProtoBufUtil.copyJavaBeanToProtoBean(body, bodyBuilder);

        return msgProto.toBuilder().setMsgBody(Any.pack(bodyProto)).build();
    }

    public static MessageExternal copyProtoBeanToMessageExternal(ProtoMain.Message message) throws Exception {
        Any bodyAny = message.getMsgBody();
//        type.googleapis.com/RegisterResult
        String bodyClassName = bodyAny.getTypeUrl().replace("type.googleapis.com/", "");
        Message.Builder bodyBuilder = map.get(bodyClassName);
        Message bodyProto = bodyAny.unpack(bodyBuilder.build().getClass());
        Class bodyInternalClass = Class.forName("com.ant.msger.base.dto.jt808.".concat(bodyClassName));
        AbstractBody body = (AbstractBody) ProtoBufUtil.copyProtoBeanToJavaBean(bodyProto, bodyInternalClass);

        message = message.toBuilder().clearMsgBody().build();
        MessageExternal external = ProtoBufUtil.copyProtoBeanToJavaBean(message, MessageExternal.class);
        external.setMsgBody(body);

        return external;
    }
}
