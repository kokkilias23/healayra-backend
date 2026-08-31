package gr.healayra.backend.service;

import gr.healayra.backend.dto.auth.AuthResponseDTO;
import gr.healayra.backend.dto.auth.LoginRequestDTO;
import gr.healayra.backend.dto.auth.RegisterRequestDTO;

public interface IAuthService {

    AuthResponseDTO register(RegisterRequestDTO dto);

    AuthResponseDTO login(LoginRequestDTO dto);
}