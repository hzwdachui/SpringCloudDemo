package org.example.Feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "demo-server2")
public interface ClientApi {
    @RequestMapping(value = "/server2-api")
    String ServiceClient(@RequestParam("s") String s);  // @RequestParam("s") 必须写
}
