package plataforma.pedido.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plataforma.carrinho.domain.Carrinho;
import plataforma.carrinho.domain.ItemCarrinho;
import plataforma.carrinho.service.CarrinhoService;
import plataforma.pedido.client.PagamentoClient;
import plataforma.pedido.domain.Pedido;
import plataforma.pedido.domain.StatusPedido;
import plataforma.pedido.repository.PedidoRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CarrinhoService carrinhoService;

    @Mock
    private PagamentoClient pagamentoClient;

    @Test
    @DisplayName("Deve realizar checkout, chamar microsserviço de pagamento e alterar status para PAGO")
    void deveRealizarCheckoutComSucesso() {
        Carrinho carrinho = new Carrinho();
        carrinho.setId(1L);

        ItemCarrinho item = new ItemCarrinho();
        item.setId(1L);
        item.setProdutoId(100L);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("50.00"));

        carrinho.setItens(List.of(item));

        Pedido pedidoSalvoMock = new Pedido();
        pedidoSalvoMock.setId(10L);
        pedidoSalvoMock.setStatus(StatusPedido.PENDENTE);
        pedidoSalvoMock.setTotal(new BigDecimal("100.00"));

        when(carrinhoService.buscarPorId(1L)).thenReturn(carrinho);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvoMock);

        Pedido pedidoFinal = pedidoService.realizarCheckout(1L);

        verify(pagamentoClient, times(1)).processarPagamento(any());
        assertThat(pedidoFinal.getStatus()).isEqualTo(StatusPedido.PAGO);
    }
}