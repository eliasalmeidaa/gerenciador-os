package br.edu.senai.gerenciadoros.controller;

import br.edu.senai.gerenciadoros.dto.ConcluirOrdemRequest;
import br.edu.senai.gerenciadoros.dto.OrdemServicoRequest;
import br.edu.senai.gerenciadoros.dto.OrdemServicoResponse;
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
    public List<OrdemServicoResponse> listar(@RequestParam(required = false) StatusOrdem status) {
        return service.listar(status).stream().map(OrdemServicoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public OrdemServicoResponse buscar(@PathVariable Long id) {
        return OrdemServicoResponse.de(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponse> abrir(@Valid @RequestBody OrdemServicoRequest r) {
        OrdemServico os = service.abrir(r);
        URI uri = URI.create("/api/ordens/" + os.getId());
        return ResponseEntity.created(uri).body(OrdemServicoResponse.de(os));
    }

    @PatchMapping("/{id}/tecnico/{tecnicoId}")
    public OrdemServicoResponse atribuirTecnico(@PathVariable Long id, @PathVariable Long tecnicoId) {
        return OrdemServicoResponse.de(service.atribuirTecnico(id, tecnicoId));
    }

    @PatchMapping("/{id}/iniciar")
    public OrdemServicoResponse iniciar(@PathVariable Long id) {
        return OrdemServicoResponse.de(service.iniciar(id));
    }

    @PatchMapping("/{id}/concluir")
    public OrdemServicoResponse concluir(@PathVariable Long id, @Valid @RequestBody ConcluirOrdemRequest r) {
        return OrdemServicoResponse.de(service.concluir(id, r.valor()));
    }

    @PatchMapping("/{id}/cancelar")
    public OrdemServicoResponse cancelar(@PathVariable Long id) {
        return OrdemServicoResponse.de(service.cancelar(id));
    }
}
