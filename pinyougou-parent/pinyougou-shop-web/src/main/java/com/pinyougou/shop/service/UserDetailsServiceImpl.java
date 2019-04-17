package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 重写验证类
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;
    //注入
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       //页面内容
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //后台数据
        TbSeller seller = sellerService.findOne(username);
        //只有状态为1的用户可以通过认证
        if (seller != null && seller.getStatus().equals("1")){

            return new User(username,seller.getPassword(),grantedAuthorities);
        }else {
            return null;
        }

    }
}
