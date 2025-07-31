package com.yohan.bank.dto;


import com.yohan.bank.enums.AccountType;
import com.yohan.bank.enums.IdentificationType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequestDTO {

    @NotBlank(message = "El tipo de identificación no puede ser un campo vacío")
    private IdentificationType identificationType;

    @NotBlank(message = "El número de identificación es obligatorio")
    @Size(min = 5, max = 20, message = "El número de identificación debe tener entre 5 y 20 carácteres")
    private String identificationNumber;

    @NotBlank(message = "El nombre representa un campo obligatorio")
    @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido representa un campo obligatorio")
    @Size(min = 2, message = "El apellido debe tener al menos 2 caracteres")
    private String lastName;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    private String email;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento no corresponde al pasado")
    private LocalDateTime dateOfBirth;

}
