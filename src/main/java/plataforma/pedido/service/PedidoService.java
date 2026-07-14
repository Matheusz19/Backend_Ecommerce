package plataforma.pedido.service;

import plataforma.carrinho.domain.Carrinho;
import plataforma.carrinho.service.CarrinhoService;
import plataforma.pedido.domain.ItemPedido;
import plataforma.pedido.domain.Pedido;
import plataforma.pedido.domain.StatusPedido;
import plataforma.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoService carrinhoService;

    @Transactional
    public Pedido realizarCheckout(Long carrinhoId) {
        Carrinho carrinho = carrinhoService.buscarPorId(carrinhoId);

        if (carrinho.getItens().isEmpty()) {
            throw new RuntimeException("Não é possível fechar pedido com carrinho vazio.");
        }

        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setTotal(carrinho.calcularTotal());

        List<ItemPedido> itensPedido = carrinho.getItens().stream()
                .map(itemCart -> new ItemPedido(
                        null,
                        itemCart.getProdutoId(),
                        itemCart.getQuantidade(),
                        itemCart.getPrecoUnitario()))
                .collect(Collectors.toList());

        pedido.getItens().addAll(itensPedido);

        return pedidoRepository.save(pedido);
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }
}