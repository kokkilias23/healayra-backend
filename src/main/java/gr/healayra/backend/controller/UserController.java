package gr.healayra.backend.controller;

import gr.healayra.backend.dto.user.UserReadOnlyDTO;
import gr.healayra.backend.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserReadOnlyDTO> getCurrentUser(
            Principal principal
    ) {

        UserReadOnlyDTO user =
                userService.getUserByEmail(principal.getName());

        return ResponseEntity.ok(user);
    }
}