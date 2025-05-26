package com.jnm.mallJnm.service;

import com.jnm.mallJnm.model.vo.User;
import org.springframework.security.core.AuthenticationException;

public interface LoginService {
    User loadByAccount(String account, String userType) throws AuthenticationException;

    User loadById(String id, String userType);



}
