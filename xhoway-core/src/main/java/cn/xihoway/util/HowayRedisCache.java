package cn.xihoway.util;

import org.apache.ibatis.cache.Cache;
import org.mybatis.caches.redis.RedisCallback;
import org.mybatis.caches.redis.RedisConfig;
import org.mybatis.caches.redis.SerializeUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;


public class HowayRedisCache implements Cache {
    private final ReadWriteLock readWriteLock = new DummyReadWriteLock();
    private String id;
    private static JedisPool pool;

    public HowayRedisCache(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        } else {
            this.id = id;
            RedisConfig redisConfig = RedisConfigurationBuilder.getInstance().parseConfiguration();
            pool = new JedisPool(redisConfig, redisConfig.getHost(), redisConfig.getPort(), redisConfig.getConnectionTimeout(), redisConfig.getSoTimeout(), redisConfig.getPassword(), redisConfig.getDatabase(), redisConfig.getClientName());
        }
    }

    private Object execute(RedisCallback callback) {
        Jedis jedis = pool.getResource();

        Object var3;
        try {
            var3 = callback.doWithRedis(jedis);
        } finally {
            jedis.close();
        }

        return var3;
    }

    public String getId() {
        return this.id;
    }

    public int getSize() {
        return (Integer)this.execute(jedis -> {
            Map<byte[], byte[]> result = jedis.hgetAll(HowayRedisCache.this.id.toString().getBytes());
            return result.size();
        });
    }

    public Map<byte[], byte[]> getAll(){
        return (Map<byte[], byte[]>) this.execute(jedis -> jedis.hgetAll(this.id.getBytes()));
    }

    public void putObject(final Object key, final Object value) {
        this.execute(jedis -> {
            jedis.hset(HowayRedisCache.this.id.toString().getBytes(), key.toString().getBytes(), SerializeUtil.serialize(value));
            return null;
        });
    }

    public Object getObject(final Object key) {
        return this.execute(jedis -> SerializeUtil.unserialize(jedis.hget(HowayRedisCache.this.id.getBytes(), key.toString().getBytes())));
    }

    public Object removeObject(final Object key) {
        return this.execute(jedis -> jedis.hdel(HowayRedisCache.this.id, key.toString()));
    }

    public void clear() {
        this.execute(jedis -> {
            jedis.del(HowayRedisCache.this.id.toString());
            return null;
        });
    }

    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

    public String toString() {
        return "Redis {" + this.id + "}";
    }
}
