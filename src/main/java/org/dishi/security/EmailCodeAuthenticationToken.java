package org.dishi.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class EmailCodeAuthenticationToken extends AbstractAuthenticationToken {


    //这里的 principal 指的是 email 地址（未认证的时候）
    private final Object principal;
    private Object credentials;

    public EmailCodeAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        setAuthenticated(false);
    }

    public EmailCodeAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public EmailCodeAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    //如果是Set认证状态，就无情的给一个异常，意思是：
    //不要在这里设置已认证，不要在这里设置已认证，不要在这里设置已认证
    //应该从构造方法里创建，别忘了要带上用户信息和权限列表哦
    //原来如此，是避免犯错吧
    /*
上面是复制的 接下来个人理解->
第一次 使用token 是未认证的 principal 里存放的是用户名
第二次 是认证的 该token 里面 还存放了 该用户的权限
springsecurity 通过这个标志来判断 是否被认证
     */
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }


    /********************父类抽象方法***************************/


    @Override
    public boolean implies(Subject subject) {
        return false;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
