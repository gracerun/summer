local batchSize = tonumber(ARGV[1]);
local messageList = redis.call('lRange', KEYS[1], 0 - batchSize, -1);
redis.call('lTrim', KEYS[1], 0, 0 - batchSize - 1);
return messageList;