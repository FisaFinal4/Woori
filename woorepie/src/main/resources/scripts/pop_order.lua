local estateKey = KEYS[1]
local customerKeyPattern = KEYS[2]

local result = redis.call('ZPOPMIN', estateKey, 1)
if #result == 0 then
    return nil
end

local orderJson = result[1]
local order = cjson.decode(orderJson)
local customerKey = string.format(customerKeyPattern, order.customerId)
redis.call('ZREMRANGEBYSCORE', customerKey, order.timestamp, order.timestamp)

return orderJson
