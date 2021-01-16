package ma.enset.productsapp.web;

import lombok.Data;
import ma.enset.productsapp.repositories.ProductRepository;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.hateoas.PagedModel;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Controller
public class ProductController{
    @Autowired
    private KeycloakRestTemplate keycloakRestTemplate;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/products")
    public String products(Model model){
        model.addAttribute("products",productRepository.findAll());
        return "products";
    }
    @GetMapping("/suppliers")
    public String suppliers(Model model){
        PagedModel <Supplier> pageSuppliers = keycloakRestTemplate.getForObject("http://localhost:8083/suppliers",PagedModel.class);
        model.addAttribute("suppliers",pageSuppliers);
        return "suppliers";
    }

    @GetMapping("/jwt")
    @ResponseBody
    public Map<String ,String>map(HttpServletRequest request){
        KeycloakAuthenticationToken token=(KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal principal=(KeycloakPrincipal)token.getPrincipal();
        KeycloakSecurityContext keycloakSecurityContext = principal.getKeycloakSecurityContext();
        Map<String,String> map =new HashMap<>();
        map.put("access_token",keycloakSecurityContext.getTokenString());
        return map;
    }
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e,Model model){
        model.addAttribute("errorMessage","you are not authorized to access this page");
        return "errors";
    }
}
@Data
class Suppliers{

    private Long id;
    private String name;
    private String email;
}