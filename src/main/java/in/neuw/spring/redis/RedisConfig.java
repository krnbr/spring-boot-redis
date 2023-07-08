package in.neuw.spring.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class RedisConfig {

    @Bean
    public RedisTemplate<String, RedisData<String>> redisDataTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, RedisData<String>> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(RedisData.class));
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisData.class));
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        template.setEnableTransactionSupport(true);
        return template;
    }



}
