package gr.healayra.backend.service;

import gr.healayra.backend.dto.user.UserReadOnlyDTO;

public interface IUserService {

    UserReadOnlyDTO getUserById(Long id);

    UserReadOnlyDTO getUserByEmail(String email);
}