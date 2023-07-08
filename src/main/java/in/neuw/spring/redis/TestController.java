package in.neuw.spring.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apis/v1")
public class TestController {

    @Autowired
    private RedisDataService redisDataService;

    @PostMapping("/test")
    public RedisData<String> testCreate(final @RequestParam(required = false, defaultValue = "NA") String data) {
        return redisDataService.save(data);
    }

    @GetMapping("/test")
    public RedisData<String> getData(final @RequestParam(required = false, defaultValue = "NA") String id) {
        return redisDataService.get(id);
    }

}
