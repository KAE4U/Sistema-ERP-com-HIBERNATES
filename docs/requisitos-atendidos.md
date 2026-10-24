# Rastreabilidade do 4º Trabalho — ALPOO

Este documento relaciona os requisitos das três páginas do enunciado com a implementação do projeto.

| Requisito | Implementação |
|---|---|
| Java Swing | Telas em `br.unip.erp.view`, menu MDI e tema FlatLaf. |
| MVC | Packages `model`, `view` e `controller`. |
| DAO | `GenericDAO<T, ID>`, `JpaGenericDAO<T, ID>` e DAOs concretos. |
| Hibernate | Hibernate ORM + Jakarta Persistence em `persistence.xml` e `JpaUtil`. |
| Anotações em cada Model persistente | 11 entidades com `@Entity`, `@Table`, `@Id`, `@Column` e relacionamentos. |
| Login completo | `LoginView`, `UsuarioController` e `UsuarioService`. |
| Validação de senha | BCrypt para novas senhas e migração automática das senhas legadas. |
| Opção para criar usuário no login | `CadastroUsuarioDialog`; conta criada sempre como usuário comum e ativo. |
| Cadastro de usuário | `UsuarioView`, incluindo perfil administrador. |
| Cadastro de cliente | `ClienteView` + `ClienteDAO`; relação Hibernate com Pessoa. |
| Cadastro de fornecedor | `FornecedorView` + `FornecedorDAO`; relação Hibernate com Pessoa. |
| Cadastro de produto | `ProdutoView`, incluindo `PRO_TIPO`, preço, custo e estoque mínimo. |
| Cadastro de forma de pagamento | `FormaPagamentoView`. |
| Venda completa | `VendaView` + `VendaService`: itens, pagamentos, validação e baixa de estoque. |
| Compra completa | `CompraView` + `CompraService`: itens e entrada de estoque. |
| Relatório para cada formulário | Sete relatórios PDF: usuários, clientes, fornecedores, produtos, formas de pagamento, vendas e compras. |
| Exemplo de relatório com paginação | `ReportService`, com título, data de emissão, página atual/total e linhas tabulares. |
| Dashboard gerencial | `DashboardView`, `DashboardController` e `DashboardDAO`. |
| Indicadores do dashboard | Faturamento, custo de compras, saldo e itens abaixo do estoque mínimo. |
| Gráficos do dashboard | Linha de vendas × compras (12 meses) e gráfico de anel por forma de pagamento. |

## Melhorias preservadas do ERP anterior

- Tema claro e escuro.
- Busca dinâmica nos cadastros.
- Máscaras de CPF/CNPJ, telefone, data e moeda.
- Validação dos dígitos verificadores de CPF/CNPJ.
- Histórico e detalhes de compras e vendas.
- Exclusão permitida somente para administradores.
- Reversão de estoque ao excluir compra ou venda.
- Transações atômicas e locks pessimistas de produtos.

## Observação sobre o banco existente

Para atualizar o Supabase utilizado pelo projeto anterior, execute primeiro `database/migration_v2_hibernate.sql`. Para uma instalação vazia, utilize `database/schema.sql`. O Hibernate está configurado com `hbm2ddl.auto=validate`, portanto ele valida o schema, mas não cria nem remove tabelas automaticamente.
