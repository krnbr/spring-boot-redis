package in.neuw.spring.redis;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fppt.jedismock.RedisServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, useMainMethod = SpringBootTest.UseMainMethod.WHEN_AVAILABLE)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringBootRedisApplicationTests {

    private RedisServer server;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeAll
    void init() throws IOException {
        this.server = RedisServer.newRedisServer(6399);  // bind to a random port
        this.server.start();
    }


    @Test
    void contextLoads() throws InterruptedException {
        String inputData = UUID.randomUUID().toString();
        String id = testRestTemplate.postForObject("/apis/v1/test?data="+ inputData, null, ObjectNode.class).get("id").asText();
        RedisData<?> data = testRestTemplate.getForObject("/apis/v1/test?id="+ id, RedisData.class);
        System.out.println("id is "+data.getId());
        assertEquals(inputData, data.getData());
        assertNotNull(data.getDate());
        Thread.sleep(15000);
        data = testRestTemplate.getForObject("/apis/v1/test?id="+ id, RedisData.class);
        assertNull(data);
    }

    @AfterAll
    void destroy() throws IOException {
        this.server.stop();
    }

}
