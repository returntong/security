package com.security.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    // 授权码模式需要
  /*  @Autowired
    private AuthorizationCodeServices authorizationCodeServices;
*/
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    DataSource dataSource;

    // 设置 jwt 秘钥
    private static final String SIGNINGKEY = "maimai";

    //配置令牌端点的安全服务
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()") //tokenkey这个endpoint当使用JwtToken且使用非对称加密时，资源服务用于获取公钥而开放的，这里指这个 endpoint完全公开。
                .checkTokenAccess("permitAll()") // ）checkToken这个endpoint完全公开
                .allowFormAuthenticationForClients(); // 允许表单认证
    }

    //配置客户端详情服务
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
  /*clients.inMemory().withClient("c1") //client_id 用来标识客户的id
                .secret(passwordEncoder.encode("secret")) //client_secret 客户端秘钥
                .resourceIds("res1") //设置资源服务器id
                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token") //客户端可以使用的授权类型，默认为空
                .scopes("all") // 用来限制客户端的访问范围，如果为空（默认）那么客户端拥有全部的访问范围
                .autoApprove(true); //false 如果是授权码模式，必须同意后才发放令牌。true 无需同意，默认自动发放*/

        clients.withClientDetails(clientDetailsService()); //从数据库获取
//        clients.jdbc(dataSource);
    }

    //初始化一个 clientDetailsService
    public ClientDetailsService clientDetailsService() {
        ClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        ((JdbcClientDetailsService) clientDetailsService).setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }


    //配置令牌的访问端点和令牌服务
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager) // 密码模式需要
                //.authorizationCodeServices(authorizationCodeServices) // 授权码模式需要
                .tokenServices(tokenService()) //令牌管理服务
                //.pathMapping("/oauth/token","/getToken") //该方法主要是将系统默认的接口改成自定义的接口
                .allowedTokenEndpointRequestMethods(HttpMethod.POST); //允许post提交
    }

    //令牌存储策略
    @Bean
    public TokenStore tokenStore() {
        //生成jwt令牌
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNINGKEY); //对称秘钥，资源服务器使用该秘钥来验证
        return converter;
    }


    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service = new DefaultTokenServices();
        service.setClientDetailsService(clientDetailsService()); //客户端信息服务
        service.setSupportRefreshToken(true); //是否产产生刷新令牌
        service.setTokenStore(tokenStore()); // 令牌存储策略
        //令牌增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter()));
        service.setTokenEnhancer(tokenEnhancerChain);

        service.setAccessTokenValiditySeconds(7200); // 令牌默认有效期2小时
        service.setRefreshTokenValiditySeconds(259200); // 刷新令牌默认有效期3天
        return service;
    }


   /* @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) { //设置授权码模式的授权码如何存取，暂时采用内存方式
        return new JdbcAuthorizationCodeServices(dataSource);
    }
*/

}
