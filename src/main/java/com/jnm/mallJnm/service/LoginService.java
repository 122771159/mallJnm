package com.jnm.mallJnm.service;

import com.jnm.mallJnm.model.vo.User;
import org.springframework.security.core.AuthenticationException;

public interface LoginService {
    User loadByAccountType(String account, String userType,String openId) throws AuthenticationException;
    User loadByAccount(String account,String openId);
    User loadById(String id, String userType);

    User loadByOpenId(String openId, String userType);

}
