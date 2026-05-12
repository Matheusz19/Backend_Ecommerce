package plataforma.carrinho.controller;

import plataforma.carrinho.domain.Carrinho;
import plataforma.carrinho.service.CarrinhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrinhos")
@RequiredArgsConstructor
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    @PostMapping
    public ResponseEntity<Carrinho> criarCarrinho() {
        return ResponseEntity.status(HttpStatus.CREATED).body(carrinhoService.criarCarrinho());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrinho> buscarCarrinho(@PathVariable Long id) {
        return ResponseEntity.ok(carrinhoService.buscarPorId(id));
    }

    @PostMapping("/{carrinhoId}/itens")
    public ResponseEntity<Carrinho> adicionarItem(
            @PathVariable Long carrinhoId,
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade) {

        Carrinho carrinhoAtualizado = carrinhoService.adicionarItem(carrinhoId, produtoId, quantidade);
        return ResponseEntity.ok(carrinhoAtualizado);
    }
}
