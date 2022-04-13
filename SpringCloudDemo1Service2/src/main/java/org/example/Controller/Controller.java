package org.example.Controller;

import org.example.Service.Service;
import org.example.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Controller implements Api{
    @RequestMapping("/server2-api")
    public String Service(String s){
        Service S = new Service();
        return S.Service(s);
    }
}
