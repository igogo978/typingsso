/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author igogo
 */
@Controller
public class IndexController {
    
//導到welcome頁面 
   @RequestMapping("/")
    public RedirectView index(RedirectAttributes attributes) {
//        attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
//        attributes.addAttribute("attribute", "redirectWithRedirectView");
        return new RedirectView("typingsso/welcome");
    }
}
