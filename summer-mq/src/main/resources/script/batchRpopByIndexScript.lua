local results = {};
local batchSize = tonumber(ARGV[1]);
if (batchSize <= 0) then
    return results;
end

local table_len = function(t)
    local len = 0
    for k, v in pairs(t) do
        len = len + 1
    end
    return len;
end

local min = function(num1, num2)
    if (num1 > num2) then
        return num2;
    else
        return num1;
    end
end

local indexSize = redis.call('incrby', KEYS[1], 0 - batchSize);
if (indexSize < 0) then
    batchSize = batchSize + indexSize;
    indexSize = redis.call('incrby', KEYS[1], 0 - indexSize);
end

local messageList = redis.call('lRange', KEYS[2], 0 - batchSize, -1);
local listSize = min(table_len(messageList), batchSize);
if (listSize <= 0) then
    return results;
end

redis.call('lTrim', KEYS[2], 0, 0 - listSize - 1);

results[1] = indexSize;
results[2] = messageList;
return results;