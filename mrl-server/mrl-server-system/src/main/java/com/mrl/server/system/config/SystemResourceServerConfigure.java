package com.mrl.server.system.config;

import com.mrl.common.handle.AuthAccessDeniedHandler;
import com.mrl.common.handle.AuthExceptionEntryPoint;
import com.mrl.server.system.properties.ServerSystemProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * 资源服务器配置，包括请求拦截以及异常处理
 * @author luc
 * @date 2020/7/2018:19
 */
@Configuration
@EnableResourceServer
public class SystemResourceServerConfigure extends ResourceServerConfigurerAdapter {
    @Autowired
    private AuthExceptionEntryPoint authenticationEntryPoint;
    @Autowired
    private AuthAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private ServerSystemProperties properties;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        String[] anonUrls= StringUtils.splitByWholeSeparatorPreserveAllTokens(properties.getAnonUrl(),",");

        //关闭csrf，表示所有请求都需要认证，携带令牌才能访问
        http.csrf().disable()
                .requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests()
                //免认证URI
                .antMatchers(anonUrls).permitAll()
                .antMatchers("/**").authenticated();
    }
    //通过注解已在启动类注入401 403异常的处理，在此处资源服务配置中引入
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
    }
}
