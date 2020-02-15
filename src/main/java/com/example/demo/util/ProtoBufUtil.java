package com.example.demo.util;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工具类
 */
public class ProtoBufUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProtoBufUtil.class);

    private static final JsonFormat.Printer printer = JsonFormat.printer();

    private static final JsonFormat.Parser parser = JsonFormat.parser();

    /**
     * Proto 转化为Json
     *
     * @param target
     * @return
     */
    public static String copyProtoBeanToJson(MessageOrBuilder target) {
        try {
            return printer.print(target);
        } catch (InvalidProtocolBufferException e) {
            logger.error("ProtoBufUtil复制到Json异常", e);
            return null;
        }
    }

    /**
     * javabean转化为Proto
     *
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T extends Message> T copyJavaBeanToProtoBean(Object source, T.Builder target) {
        // javaBean 转换为Json
//        String sourceStr = JSONUtil.bean2json(source);
        String sourceStr = JSONObject.toJSONString(source);
        try {
            parser.merge(sourceStr, target);
            return (T) target.build();
        } catch (InvalidProtocolBufferException e) {
            logger.error("ProtoBufUtil复制到Proto异常", e);
        }
        return null;
    }


    /**
     * proto 转化为javabean
     *
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copyProtoBeanToJavaBean(MessageOrBuilder source, Class<T> target) {
        // protoBuf 转换为Json
        String sourceStr = copyProtoBeanToJson(source);
//        return (T) JSONUtil.json2Object(soutceStr,target);
//        return (T) JsonUtils.toObj(target,sourceStr);
        return (T) JSONObject.parseObject(sourceStr).toJavaObject(target);
    }

    /**
     * 使用proto序列化javabean
     *
     * @param source
     * @param target
     * @return
     */
    public static byte[] serializFromJavaBean(Object source, Message.Builder target) {
        return copyJavaBeanToProtoBean(source, target).toByteArray();
    }

    /**
     * 使用proto反序列化javabean
     *
     * @param source
     * @param parser
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T deserializToJavaBean(byte[] source, Parser parser, Class<T> target) {
        try {
            return copyProtoBeanToJavaBean((MessageOrBuilder) parser.parseFrom(source), target);
        } catch (InvalidProtocolBufferException e) {
            logger.error("反序列化错误", e);
        }
        return null;
    }
}
