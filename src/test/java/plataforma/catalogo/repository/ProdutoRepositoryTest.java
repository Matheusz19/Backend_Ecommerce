package plataforma.catalogo.repository;

import plataforma.catalogo.domain.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    @DisplayName("Deve salvar um produto com sucesso e gerar ID")
    void deveSalvarProduto() {
        Produto produto = new Produto(null, "Teclado Mecânico", "Switch Azul", new BigDecimal("350.00"), 10);

        Produto produtoSalvo = produtoRepository.save(produto);

        assertThat(produtoSalvo).isNotNull();
        assertThat(produtoSalvo.getId()).isGreaterThan(0);
        assertThat(produtoSalvo.getNome()).isEqualTo("Teclado Mecânico");
    }

    @Test
    @DisplayName("Deve retornar apenas produtos com estoque positivo")
    void deveRetornarProdutosComEstoque() {
        produtoRepository.save(new Produto(null, "Mouse", "Gamer", new BigDecimal("150.00"), 5));
        produtoRepository.save(new Produto(null, "Monitor", "144hz", new BigDecimal("1200.00"), 0));

        List<Produto> produtosEmEstoque = produtoRepository.findByQuantidadeEstoqueGreaterThan(0);

        assertThat(produtosEmEstoque).hasSize(1);
        assertThat(produtosEmEstoque.get(0).getNome()).isEqualTo("Mouse");
    }
}