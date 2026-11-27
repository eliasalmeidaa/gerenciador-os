package br.edu.senai.gerenciadoros.controller;

import br.edu.senai.gerenciadoros.dto.ConcluirOrdemRequest;
import br.edu.senai.gerenciadoros.dto.OrdemServicoRequest;
import br.edu.senai.gerenciadoros.model.OrdemServico;
import br.edu.senai.gerenciadoros.model.StatusOrdem;
import br.edu.senai.gerenciadoros.service.OrdemServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ordens")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService service;

    @GetMapping
    public List<OrdemServico> listar(@RequestParam(required = false) StatusOrdem status) {
        return service.listar(status);
    }

    @GetMapping("/{id}")
    public OrdemServico buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<OrdemServico> abrir(@Valid @RequestBody OrdemServicoRequest r) {
        OrdemServico os = service.abrir(r);
        URI uri = URI.create("/api/ordens/" + os.getId());
        return ResponseEntity.created(uri).body(os);
    }

    @PatchMapping("/{id}/tecnico/{tecnicoId}")
    public OrdemServico atribuirTecnico(@PathVariable Long id, @PathVariable Long tecnicoId) {
        return service.atribuirTecnico(id, tecnicoId);
    }

    @PatchMapping("/{id}/iniciar")
    public OrdemServico iniciar(@PathVariable Long id) {
        return service.iniciar(id);
    }

    @PatchMapping("/{id}/concluir")
    public OrdemServico concluir(@PathVariable Long id, @Valid @RequestBody ConcluirOrdemRequest r) {
        return service.concluir(id, r.valor());
    }

    @PatchMapping("/{id}/cancelar")
    public OrdemServico cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
