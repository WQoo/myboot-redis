/**
 * 文件名：IpLimitFilter
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述：IpLimitFilter
 */
package com.ranger.mybootredislua;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * ip limit filter
 */
@WebFilter(filterName = "IpLimitFilter", urlPatterns = "/*")
public class IpLimitFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(IpLimitFilter.class);

    /**
     * The constant TIME_LIMIT.
     */
    public static final Integer TIME_LIMIT = 60;

    /**
     * The constant COUNT_LIMIT.
     */
    public static final Integer COUNT_LIMIT = 5;

    private RedisTemplate redisTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        redisTemplate = ctx.getBean("redisTemplate",RedisTemplate.class);
    }

    /**
     * 过滤
     *
     * @param servletRequest  ServletRequest
     * @param servletResponse ServletResponse
     * @param filterChain     FilterChain
     * @throws IOException      io 异常
     * @throws ServletException servlet 异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        // 过滤帆软查询请求
        String ip = getIpAddress(req);
        String url = req.getRequestURL().toString();
        String key = "req_limit_".concat(url).concat("_").concat(ip);
        key = key.replaceAll(":","-");
        //这里需要替换下: 不然塞值的时候，redis会出现层次分化。。mac电脑的问题
        if (checkWithRedis(key)) {
            filterChain.doFilter(req, resp);
        } else {
            logger.debug("ip{} 的访问频率太高", ip);
            req.getRequestDispatcher("fr_limit.html").forward(req, resp);
        }
    }

    /**
     * 以redis实现请求记录
     *
     * @param key 核心key
     * @return boolean 是否逾期
     */
    private boolean checkWithRedis(String key) {
        long count = redisTemplate.opsForValue().increment(key, 1);
        if (count == 1) {
            redisTemplate.expire(key, TIME_LIMIT, TimeUnit.SECONDS);
        }
        if (count > COUNT_LIMIT) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param request
     * @return
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        logger.info("x-forwarded-for ip: " + ip);
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip.indexOf(",")!=-1 ){
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            logger.info("Proxy-Client-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            logger.info("WL-Proxy-Client-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            logger.info("HTTP_CLIENT_IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            logger.info("HTTP_X_FORWARDED_FOR ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            logger.info("X-Real-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            logger.info("getRemoteAddr ip: " + ip);
        }
        logger.info("获取客户端ip: " + ip);
        return ip;
    }
}
