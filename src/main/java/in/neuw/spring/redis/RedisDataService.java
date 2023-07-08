package in.neuw.spring.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisDataService {

    private Logger logger = LoggerFactory.getLogger(RedisDataService.class);

    private final BoundHashOperations<String, String, RedisData<String>> hashOperations;

    private final BoundZSetOperations<String, String> setOperations;

    @Value("${app.cache-name}")
    private String cacheName;

    @Value("${app.cache-ttl.secs}")
    private Integer cacheTTL;

    public RedisDataService(final RedisTemplate<String, RedisData<String>> redisDataTemplate,
                            final StringRedisTemplate stringRedisTemplate,
                            final @Value("${app.cache-name}") String cacheName) {
        this.hashOperations = redisDataTemplate.boundHashOps("hash-"+cacheName);
        this.setOperations = stringRedisTemplate.boundZSetOps("sort-set-"+cacheName);
    }

    @Transactional
    public RedisData<String> save(final String data) {
        String id = UUID.randomUUID().toString();
        RedisData<String> redisData = new RedisData<>();
        redisData.setId(id);
        redisData.setData(data);
        redisData.setDate(new Date());
        hashOperations.put(id, redisData);
        setOperations.add(id, (System.currentTimeMillis() + (cacheTTL * 1000)));
        return redisData;
    }

    @Transactional
    public RedisData<String> get(final String id) {
        return hashOperations.get(id);
    }

    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRateString = "${app.cache-ttl.executor-secs}")
    @Transactional
    public void removeData() {
        logger.info("event for the cleaning up cache - {} triggered", cacheName);
        long end = System.currentTimeMillis();
        Set<String> rangeSet = setOperations.rangeByScore(0, end);
        if (!CollectionUtils.isEmpty(rangeSet)) {
            logger.info("there are {} candidate(s) ready for removal from cache", rangeSet.size());
            hashOperations.delete(rangeSet.toArray());
            setOperations.removeRangeByScore(0, end);
        }
    }

}
