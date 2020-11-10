package org.dishi.utils;

import org.dishi.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {
    public static User getUserFromSession(){
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")){
            return null;
        }
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
