-- KEYS[1]: rate limit key (includes window id)
-- ARGV[1]: max requests per window
-- ARGV[2]: window size in seconds
-- Returns: {1=allowed/0=denied, remaining}
local limit = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local current = redis.call('INCR', KEYS[1])
if current == 1 then
    redis.call('EXPIRE', KEYS[1], window)
end
if current > limit then
    return {0, 0}
end
return {1, limit - current}
