package com.ranger.mybootredislua.redisConfiguration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiang.wang01@hand-china.com
 * @version 1.0
 * @name RedissonProperties
 * @description 分布式锁使用自动装配
 * @date 2019-06-26
 */
@ConfigurationProperties(prefix = "redisson")
@Getter
@Setter
public class RedissonProperties {
    private int timeout ;

    private String address;

    private String password;

    private int connectionPoolSize = 64;

    private int connectionMinimumIdleSize=10;

    private int slaveConnectionPoolSize = 250;

    private int masterConnectionPoolSize = 250;

    private String[] sentinelAddresses;

    private String masterName;

    public void setSentinelAddresses(String sentinelAddresses) {
        this.sentinelAddresses = sentinelAddresses.split(",");
    }
}
