package tn.esprit.spring.control;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import tn.esprit.spring.entities.User;
import tn.esprit.spring.service.IUserService;

@RestController
public class UserRestControlImpl {

    private static final Logger logger = LogManager.getLogger(UserRestControlImpl.class);

    @Autowired
    IUserService userService;

    // http://localhost:8083/
    @RequestMapping("/")
    @ResponseBody
    public String welcome() {
        logger.info("GET / : welcome page");
        return "From Docker Container: Bonjour, Bienvenue à l'application de test des Web Services REST.";
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User u) {
        logger.info("POST /users");
        return userService.addUser(u);
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") Long id) {
        logger.info("GET /users/{}", id);
        User u = userService.retrieveUser(id);
        if (u == null) {
            logger.warn("GET /users/{} : not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return u;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User u) {
        logger.info("PUT /users (id={})", u.getId());
        return userService.updateUser(u);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {
        logger.info("DELETE /users/{}", id);
        userService.deleteUser(id);
    }
}