package com.example.demo.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cache {

    // 授权码，终端手机号(注册的手机号和授权码)
    public static Map<String,String> mapRegister = new HashMap<>();

    // 当前连接的已授权终端集合,存手机号即可
    public static Set<String> mapAuthed = new HashSet<>();
}
