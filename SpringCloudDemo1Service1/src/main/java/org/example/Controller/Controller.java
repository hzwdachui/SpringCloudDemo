package org.example.Controller;

import org.example.Feign.ClientApi;
import org.example.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Controller implements Api{
    @Autowired
    private ClientApi client;
    @RequestMapping("/server1-api")
    public String Service(String s){
        return client.ServiceClient(s);
    }
}
