package com.ranger.mybootredislua;

import com.ranger.mybootredislua.redisService.RedisService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author qiang.wang01@hand-china.com
 * @version 1.0
 * @name testController
 * @description
 * @date 2019-06-26
 */
@Controller
public class testController {

    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/insertUserToRedis")
    @ApiOperation(value="创建用户", notes="往redis中存储用户信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "redis的key", dataType = "String",paramType = "path"),
        @ApiImplicitParam(name = "nameValue", value = "redis中的value", dataType = "String",paramType = "path")
    })
    public String insertToRedis(String name,String nameValue){
        redisService.set(name,nameValue);
        return redisService.get(name).toString();
    }
}
