-- KEYS[1]: sorted-set key per client
-- ARGV[1]: max requests per window
-- ARGV[2]: window size in seconds
-- ARGV[3]: current timestamp in milliseconds
-- Returns: {1=allowed/0=denied, remaining}
local limit  = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local now    = tonumber(ARGV[3])
local cutoff = now - (window * 1000)

redis.call('ZREMRANGEBYSCORE', KEYS[1], '-inf', cutoff)
local count = redis.call('ZCARD', KEYS[1])

if count < limit then
    -- member = "now:count" to guarantee uniqueness under high concurrency
    redis.call('ZADD', KEYS[1], now, now .. ':' .. count)
    redis.call('EXPIRE', KEYS[1], window + 1)
    return {1, limit - count - 1}
else
    return {0, 0}
end
