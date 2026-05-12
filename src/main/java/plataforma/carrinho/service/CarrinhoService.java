package plataforma.carrinho.service;

import plataforma.carrinho.domain.Carrinho;
import plataforma.carrinho.domain.ItemCarrinho;
import plataforma.carrinho.repository.CarrinhoRepository;
import plataforma.catalogo.domain.Produto;
import plataforma.catalogo.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoService produtoService;

    public Carrinho criarCarrinho() {
        return carrinhoRepository.save(new Carrinho());
    }

    public Carrinho buscarPorId(Long id) {
        return carrinhoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));
    }

    public Carrinho adicionarItem(Long carrinhoId, Long produtoId, Integer quantidade) {
        Carrinho carrinho = buscarPorId(carrinhoId);

        // Comunicação entre contextos: buscando o produto no catálogo
        Produto produto = produtoService.listarTodos().stream()
                .filter(p -> p.getId().equals(produtoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente");
        }

        ItemCarrinho novoItem = new ItemCarrinho(null, produto.getId(), quantidade, produto.getPreco());
        carrinho.getItens().add(novoItem);

        return carrinhoRepository.save(carrinho);
    }
}
