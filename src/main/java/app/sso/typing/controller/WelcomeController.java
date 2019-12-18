/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author igogo
 */
@Controller
public class WelcomeController {
    
    //未登入的頁面 一律導到這裡
    @RequestMapping("/typingsso/welcome")
    public String welcome() {
        return "welcome";
    }
}
