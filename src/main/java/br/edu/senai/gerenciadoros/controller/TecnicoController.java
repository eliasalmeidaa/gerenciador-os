package br.edu.senai.gerenciadoros.controller;

import br.edu.senai.gerenciadoros.dto.TecnicoRequest;
import br.edu.senai.gerenciadoros.model.Tecnico;
import br.edu.senai.gerenciadoros.service.TecnicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tecnicos")
@RequiredArgsConstructor
public class TecnicoController {

    private final TecnicoService service;

    @GetMapping
    public List<Tecnico> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Tecnico buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Tecnico> criar(@Valid @RequestBody TecnicoRequest r) {
        Tecnico t = service.cadastrar(r);
        URI uri = URI.create("/api/tecnicos/" + t.getId());
        return ResponseEntity.created(uri).body(t);
    }

    @PutMapping("/{id}")
    public Tecnico atualizar(@PathVariable Long id, @Valid @RequestBody TecnicoRequest r) {
        return service.atualizar(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
