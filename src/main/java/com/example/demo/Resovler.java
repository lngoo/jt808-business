package com.example.demo;

import com.ant.jt808.base.common.MessageId;
import com.ant.jt808.base.dto.jt808.Authentication;
import com.ant.jt808.base.dto.jt808.CommonResult;
import com.ant.jt808.base.dto.jt808.RegisterResult;
import com.ant.jt808.base.dto.jt808.basics.Message;
import com.ant.jt808.base.message.AbstractBody;
import com.example.demo.cache.Cache;
import com.example.demo.util.JsonUtils;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true){
                            String data = stringRedisTemplate.opsForList().rightPop(redisRequestKey);
                            if (StringUtils.isEmpty(data)) {
                                // 暂无数据，等待3秒
                                System.out.println("%%% no redis data.sleep 3 seconds...");
                                Thread.sleep(3000);
                            }

                            // 消费消息
                            msgConsume(data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        ).start();
    }

    private void msgConsume(String data) {
        Message msg = JsonUtils.toObj(Message.class, data);
        Integer type = msg.getType();

        System.out.println("###  收到信息：" + data);
        switch(type){
            case(MessageId.终端注册) :
                doClientRegister(msg);
                break;
//            case(MessageId.终端心跳) :  由antMsger处理
//                doClientHeart(msg);
//                break;
            case(MessageId.位置信息汇报) :
                doClientPositionReport(msg);
                break;
            case(MessageId.终端鉴权) :
                doClientAuthentication(msg);
                break;
            default:
        }
    }

    private void doClientAuthentication(Message msg) {
        String mobileNum = msg.getMobileNumber();
        Authentication authentication = (Authentication) msg.getBody();
        String token = authentication.getToken();
        if (StringUtils.equalsIgnoreCase(Cache.mapRegister.get(token), mobileNum)) {
            Cache.mapAuthed.add(mobileNum);
        }

        // 鉴权应答
        CommonResult commonResult = new CommonResult(MessageId.终端鉴权, msg.getSerialNumber() ,CommonResult.Success);
        stringRedisTemplate.opsForList().leftPush(redisResponseKey, JsonUtils.toJson(commonResult));
    }

    private void doClientPositionReport(Message msg) {
        String mobileNum = msg.getMobileNumber();
        CommonResult commonResult = new CommonResult(MessageId.位置信息汇报, msg.getSerialNumber(), CommonResult.Success);

        // 调用统一鉴权处理方法，无权限不处理
        if (!isAuthedClient(mobileNum)) {
            commonResult.setResultCode(CommonResult.Fial);
        } else {
           // do something
        }
        stringRedisTemplate.opsForList().leftPush(redisResponseKey, JsonUtils.toJson(commonResult));
    }

    private boolean isAuthedClient(String mobileNum) {
        return Cache.mapAuthed.contains(mobileNum);
    }

    private void doClientRegister(Message msg) {
        String mobileNum = msg.getMobileNumber();
        AbstractBody body = msg.getBody();

//        String authKey = String.valueOf(System.currentTimeMillis());
        String authKey = mobileNum + "_authed";
        Cache.mapRegister.put(authKey, mobileNum);

        // do something with body

        RegisterResult result = new RegisterResult(msg.getSerialNumber(), RegisterResult.Success, authKey);
        stringRedisTemplate.opsForList().leftPush(redisResponseKey, JsonUtils.toJson(result));
    }
}
