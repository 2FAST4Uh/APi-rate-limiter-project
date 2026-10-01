-- KEYS[1]: hash key for this client's bucket
-- ARGV[1]: capacity  (max tokens)
-- ARGV[2]: refill_rate (tokens per second)
-- ARGV[3]: current time in milliseconds
-- ARGV[4]: key TTL in seconds
-- Returns: {1=allowed/0=denied, floor(remaining_tokens)}
local capacity    = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now         = tonumber(ARGV[3])
local ttl         = tonumber(ARGV[4])

local vals     = redis.call('HMGET', KEYS[1], 'tokens', 'last_ms')
local tokens   = tonumber(vals[1])
local last_ms  = tonumber(vals[2])

if tokens == nil then
    tokens  = capacity
    last_ms = now
end

-- Refill proportionally to elapsed time
local elapsed_ms = math.max(0, now - last_ms)
tokens = math.min(capacity, tokens + (elapsed_ms / 1000) * refill_rate)

if tokens >= 1 then
    tokens = tokens - 1
    redis.call('HMSET', KEYS[1], 'tokens', tokens, 'last_ms', now)
    redis.call('EXPIRE', KEYS[1], ttl)
    return {1, math.floor(tokens)}
else
    redis.call('HMSET', KEYS[1], 'tokens', tokens, 'last_ms', now)
    redis.call('EXPIRE', KEYS[1], ttl)
    return {0, 0}
end
