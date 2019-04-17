package com.pinyougou.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 */
public class BCryptUtils {

    /**
     * 对密码进行加密
     * @param password
     * @return
     */
    public static String passwordEncoder(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(password);
        return encode;
    }

    /**
     * 验证密码是否正确
     * @param password
     * @param encodePassword
     * @return
     */
    public static Boolean isMatch(String password,String encodePassword){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password,encodePassword);
    }
}
