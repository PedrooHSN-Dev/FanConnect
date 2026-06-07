package br.com.fanconnect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @PostMapping
    public ResponseEntity<Map<String, String>> fazerUploadDaImagem(@RequestParam("arquivo") MultipartFile arquivo) {
        try {
            Path diretorio = Paths.get("uploads");
            if (!Files.exists(diretorio)) {
                Files.createDirectories(diretorio);
            }

            String nomeFicheiro = UUID.randomUUID().toString() + "_" + arquivo.getOriginalFilename();
            Path caminhoFicheiro = diretorio.resolve(nomeFicheiro);

            Files.copy(arquivo.getInputStream(), caminhoFicheiro);

            String urlPublica = "http://localhost:8080/uploads/" + nomeFicheiro;

            return ResponseEntity.ok(Map.of("url", urlPublica));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}