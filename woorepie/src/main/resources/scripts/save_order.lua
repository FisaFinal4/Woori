local estateKey = KEYS[1]
local customerKey = KEYS[2]
local estateOrderJson = ARGV[1]
local customerOrderJson = ARGV[2]
local timestamp = tonumber(ARGV[3])

-- JSON 파싱
local estateOrder = cjson.decode(estateOrderJson)
local customerOrder = cjson.decode(customerOrderJson)

-- ZADD 실행 (스코어는 timestamp)
redis.call('ZADD', estateKey, timestamp, estateOrderJson)
redis.call('ZADD', customerKey, timestamp, customerOrderJson)

return 1
