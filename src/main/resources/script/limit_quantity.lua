local key1 = KEYS[1]
local key2 = KEYS[2]
local key3 = KEYS[3]
--- 设置key和value
redis.call("SET",key1,key2)
--- 设置key的过期时间
redis.call("EXPIRE",key1,key3)
--- 执行完返回true
return true