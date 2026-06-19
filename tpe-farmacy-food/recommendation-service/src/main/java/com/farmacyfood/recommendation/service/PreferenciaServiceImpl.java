package com.farmacyfood.recommendation.service;

import com.farmacyfood.recommendation.client.UsuarioClient;
import com.farmacyfood.recommendation.dto.PerfilPreferenciaDTO;
import com.farmacyfood.recommendation.dto.UsuarioResponseDTO;
import com.farmacyfood.recommendation.entity.PerfilPreferencia;
import com.farmacyfood.recommendation.exception.UsuarioNotFoundException;
import com.farmacyfood.recommendation.repository.PerfilPreferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PreferenciaServiceImpl implements PreferenciaService {

    private final PerfilPreferenciaRepository perfilPreferenciaRepository;
    private final UsuarioClient usuarioClient;

    @Override
    @Transactional(readOnly = true)
    public PerfilPreferenciaDTO obtenerPreferencias(Long userId) {
        // Primero busco en la base de datos local
        Optional<PerfilPreferencia> perfilOpt = perfilPreferenciaRepository.findByUserId(userId);

        if (perfilOpt.isPresent()) {
            PerfilPreferencia perfil = perfilOpt.get();
            List<String> preferencias = parsearPreferencias(perfil.getDietaryPreferences());
            return new PerfilPreferenciaDTO(userId, preferencias);
        }

        // Si no existe en local, consulto al user-service
        UsuarioResponseDTO usuario = usuarioClient.getUsuario(userId);
        if (usuario == null) {
            throw new UsuarioNotFoundException(userId);
        }

        // Convierto List<String> a String separado por comas para guardar en la entidad
        String preferenciasString = convertirAString(usuario.dietaryPreferences());

        // Guardo las preferencias en cache local
        PerfilPreferencia nuevoPerfil = PerfilPreferencia.builder()
                .userId(userId)
                .dietaryPreferences(preferenciasString)
                .build();
        perfilPreferenciaRepository.save(nuevoPerfil);

        return new PerfilPreferenciaDTO(userId, usuario.dietaryPreferences());
    }

    private String convertirAString(List<String> preferencias) {
        if (preferencias == null || preferencias.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < preferencias.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(preferencias.get(i));
        }
        return sb.toString();
    }

    private List<String> parsearPreferencias(String dietaryPreferences) {
        List<String> resultado = new ArrayList<>();

        if (dietaryPreferences == null || dietaryPreferences.isEmpty()) {
            return resultado;
        }

        String[] partes = dietaryPreferences.split(",");
        for (int i = 0; i < partes.length; i++) {
            String preferencia = partes[i].trim();
            if (!preferencia.isEmpty()) {
                resultado.add(preferencia);
            }
        }

        return resultado;
    }
}
