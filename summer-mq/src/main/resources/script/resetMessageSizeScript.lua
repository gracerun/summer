local messageSize = tonumber(redis.call('get', KEYS[1])) or 0;
if messageSize <= 0 then
    messageSize = redis.call('lLen', KEYS[2]) or 0;
    if messageSize > 0 then
        redis.call('set', KEYS[1], messageSize);
    end
    return messageSize;
else
    return messageSize;
end