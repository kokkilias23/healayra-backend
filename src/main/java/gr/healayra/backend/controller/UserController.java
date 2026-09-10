package gr.healayra.backend.controller;

import gr.healayra.backend.dto.user.UserReadOnlyDTO;
import gr.healayra.backend.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(
        name = "Users",
        description = "Authenticated user profile endpoints"
)
public class UserController {

    private final IUserService userService;

    @Operation(
            summary = "Get current authenticated user",
            description = """
                    Returns basic information about the currently
                    authenticated user based on the JWT identity.

                    This endpoint can be used by both DOCTOR and CLIENT roles
                    to retrieve their authenticated account information.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authenticated user returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Authenticated user not found"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserReadOnlyDTO> getCurrentUser(
            Principal principal
    ) {

        UserReadOnlyDTO user =
                userService.getUserByEmail(
                        principal.getName()
                );

        return ResponseEntity.ok(user);
    }
}