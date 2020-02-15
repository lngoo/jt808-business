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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                Thread.sleep(30000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
    }
}
