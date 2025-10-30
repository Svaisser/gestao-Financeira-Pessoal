# 💰 API de Gestão Financeira Pessoal

## Índice

- [Informações](#-informações-do-projeto)
- [Descrição](#descrição)
- [Arquitetura e Tecnologias](#️-arquitetura-e-tecnologias)
- [Modelos de Dados](#-modelos-de-dados-entidades)
- [Relacionamentos Entre Entidades](#-relacionamentos-entre-entidades)
- [Principais Funcionalidades da API](#-principais-funcionalidades-da-api)
- [Funcionalidades Detalhadas por Entidade](#-funcionalidades-detalhadas-por-entidade)
- [Implementação da Carta-Desafio](#-implementação-da-carta-desafio-limite-de-requisições)
- [Roadmap de Desenvolvimento](#-roadmap-de-desenvolvimento)
- [Referências e Recursos](#-referências-e-recursos)
- [Contato](#contato)

## 📋 Informações do Projeto

**Disciplina:** Back-end
**Instituição:** UniSatc
**Período:** 2025.2  

### 👥 Integrantes da Equipe

- **João Vitor Schmitz Svaisser** - svaisserjv@gmail.com

### 🎴 Carta-Desafio Sorteada

**Limite de Requisições** - Implementação de rate limiting configurável para controlar o número de requisições por minuto e por hora, protegendo a API contra sobrecarga e uso abusivo.

---

## 🎯 Descrição

A **API de Gestão Financeira Pessoal** é uma solução backend completa para controle de finanças pessoais, permitindo que usuários gerenciem suas contas bancárias, registrem transações financeiras (receitas e despesas), categorizem gastos e acompanhem seu patrimônio de forma organizada e eficiente.

### 🌟 Problema Resolvido

Muitas pessoas têm dificuldade em controlar suas finanças pessoais, não sabendo exatamente para onde vai seu dinheiro ou quanto possuem em diferentes contas. Esta API oferece uma solução centralizada para:

- ✅ Centralizar informações de múltiplas contas financeiras
- ✅ Registrar e categorizar todas as transações financeiras
- ✅ Acompanhar receitas e despesas de forma detalhada
- ✅ Gerar relatórios e análises sobre hábitos financeiros
- ✅ Manter histórico completo de movimentações financeiras
- ✅ Proteger dados sensíveis com autenticação e controle de acesso

---

## 🏗️ Arquitetura e Tecnologias

### Stack Tecnológica

- **Framework:** Spring Boot 3.x
- **Linguagem:** Java 17+
- **Banco de Dados:** Supabase
- **Gerenciamento de Dependências:** Maven
- **Documentação:** Swagger/OpenAPI 3.0
- **Autenticação:** JWT (JSON Web Tokens)
- **Rate Limiting:** Bucket4j
- **Validação:** Bean Validation (Hibernate Validator)

### Arquitetura em Camadas

```
┌─────────────────────────────────┐
│     Controller Layer            │  ← Endpoints REST
├─────────────────────────────────┤
│     Service Layer               │  ← Lógica de Negócio
├─────────────────────────────────┤
│     Repository Layer            │  ← Acesso a Dados (JPA)
├─────────────────────────────────┤
│     Database (PostgreSQL)       │  ← Persistência
└─────────────────────────────────┘
```

---

## 📊 Modelos de Dados (Entidades)

### 1. 👤 Usuário (User)

Representa os usuários do sistema que possuem contas financeiras.

**Atributos:**
- `id` (Long): Identificador único do usuário
- `nome` (String): Nome completo do usuário
- `email` (String): Email único para login
- `senha` (String): Senha criptografada
- `dataCadastro` (LocalDateTime): Data de criação do usuário
- `ativo` (Boolean): Status de ativação da conta

**Relacionamentos:**
- Um usuário pode ter **múltiplas contas** (1:N com Account)

**Validações:**
- Email deve ser único e válido
- Nome não pode ser vazio
- Senha deve ter no mínimo 8 caracteres

---

### 2. 💳 Conta (Account)

Representa uma conta bancária ou financeira do usuário (conta corrente, poupança, investimentos, carteira digital).

**Atributos:**
- `id` (Long): Identificador único da conta
- `nome` (String): Nome/descrição da conta (ex: "Nubank Corrente")
- `tipo` (TipoConta): Tipo da conta (CORRENTE, POUPANCA, INVESTIMENTO, SALARIO)
- `saldo` (BigDecimal): Saldo atual da conta
- `moeda` (String): Moeda da conta (BRL, USD, EUR)
- `dataCriacao` (LocalDateTime): Data de criação da conta
- `ativa` (Boolean): Se a conta está ativa

**Relacionamentos:**
- Pertence a **um usuário** (N:1 com User)
- Pode ter **múltiplas transações** (1:N com Transaction)

**Validações:**
- Nome não pode ser vazio
- Saldo deve ser um valor decimal válido
- Tipo de conta deve ser um dos valores do enum

**Regras de Negócio:**
- O saldo é atualizado automaticamente ao criar/editar transações
- Contas inativas não podem receber novas transações

---

### 3. 💸 Transação (Transaction)

Representa uma movimentação financeira (receita ou despesa) em uma conta.

**Atributos:**
- `id` (Long): Identificador único da transação
- `descricao` (String): Descrição da transação (ex: "Salário Janeiro", "Compra Supermercado")
- `valor` (BigDecimal): Valor da transação
- `tipo` (TipoTransacao): Tipo da transação (RECEITA, DESPESA)
- `data` (LocalDateTime): Data e hora da transação
- `observacoes` (String): Observações adicionais (opcional)
- `dataCriacao` (LocalDateTime): Data de registro da transação no sistema

**Relacionamentos:**
- Pertence a **uma conta** (N:1 com Account)
- Pode ter **múltiplas categorias** (N:N com Category)

**Validações:**
- Descrição não pode ser vazia
- Valor deve ser positivo
- Data não pode ser futura
- Deve ter pelo menos uma categoria

**Regras de Negócio:**
- Ao criar uma RECEITA, o saldo da conta aumenta
- Ao criar uma DESPESA, o saldo da conta diminui
- Ao editar/excluir, o saldo é recalculado
- Transações só podem ser criadas em contas ativas

---

### 4. 🏷️ Categoria (Category)

Representa categorias para classificar as transações financeiras.

**Atributos:**
- `id` (Long): Identificador único da categoria
- `nome` (String): Nome da categoria
- `tipo` (TipoCategoria): Tipo da categoria
- `descricao` (String): Descrição da categoria (opcional)
- `cor` (String): Cor em hexadecimal para representação visual (ex: "#FF5733")
- `icone` (String): Nome do ícone para UI (opcional)

**Tipos de Categoria (TipoCategoria):**
- **Receitas:** SALARIO, FREELANCE, INVESTIMENTO, VENDA, OUTROS_GANHOS
- **Despesas:** ALIMENTACAO, TRANSPORTE, MORADIA, SAUDE, EDUCACAO, LAZER, COMPRAS, CONTAS, OUTROS_GASTOS

**Relacionamentos:**
- Pode estar em **múltiplas transações** (N:N com Transaction)

**Validações:**
- Nome não pode ser vazio
- Nome deve ser único
- Tipo deve ser um dos valores do enum

**Regras de Negócio:**
- Categorias são compartilhadas entre todos os usuários (ou podem ser personalizadas por usuário)
- Ao deletar uma categoria, as transações associadas não são deletadas (relacionamento é removido)

---

## 🔄 Relacionamentos Entre Entidades

```
User (1) ──────< (N) Account
                        │
                        │
                        │ (1)
                        │
                        ▼
                     (N) Transaction (N) ────────< (N) Category
```

### Detalhamento dos Relacionamentos:

1. **User ↔ Account (1:N)**
   - Um usuário pode ter várias contas
   - Uma conta pertence a apenas um usuário
   - Cascade: ALL (ao deletar usuário, deleta suas contas)

2. **Account ↔ Transaction (1:N)**
   - Uma conta pode ter várias transações
   - Uma transação pertence a apenas uma conta
   - Cascade: ALL (ao deletar conta, deleta suas transações)

3. **Transaction ↔ Category (N:N)**
   - Uma transação pode ter várias categorias
   - Uma categoria pode estar em várias transações
   - Tabela de junção: `transaction_category`
   - Cascade: MERGE, PERSIST (não deleta em cascata)

---

## 🚀 Principais Funcionalidades da API

### 1. 🔐 Autenticação e Autorização
- Registro de novos usuários
- Login com JWT
- Proteção de rotas com autenticação
- Controle de acesso por usuário

### 2. 👤 Gestão de Usuários
- Listar usuários (com paginação e ordenação)
- Buscar usuário por ID
- Atualizar dados do usuário
- Desativar/excluir usuário
- Visualizar perfil próprio

### 3. 💳 Gestão de Contas
- Criar novas contas financeiras
- Listar todas as contas do usuário
- Buscar conta por ID
- Atualizar informações da conta
- Desativar/excluir conta
- Consultar saldo atual da conta
- Filtrar contas por tipo

### 4. 💸 Gestão de Transações
- Registrar receitas e despesas
- Listar transações com:
  - **Paginação** (page, size)
  - **Ordenação** (por data, valor, descrição)
  - **Filtros múltiplos:**
    - Por categoria
    - Por tipo (receita/despesa)
    - Por período (data início e fim)
    - Por conta
    - Por valor mínimo/máximo
- Buscar transação por ID
- Atualizar transação (recalcula saldo automaticamente)
- Excluir transação (recalcula saldo automaticamente)
- Visualizar histórico completo de uma conta

### 5. 🏷️ Gestão de Categorias
- Criar novas categorias
- Listar todas as categorias (com filtro por tipo)
- Buscar categoria por ID
- Atualizar categoria
- Excluir categoria
- Estatísticas por categoria

### 6. 📊 Relatórios e Análises
- Resumo financeiro por período
- Gastos por categoria
- Evolução do patrimônio
- Comparativo receitas vs despesas
- Top categorias mais utilizadas

### 7. ⚙️ Configuração de Limites de Requisições (Carta-Desafio)
- Visualizar limites configurados
- Atualizar limites globais (admin)
- Consultar status de uso do limite por usuário
- Diferentes limites por endpoint:
  - Limite geral: 100 req/min, 1000 req/hora
  - Transações: 20 req/min, 200 req/hora (mais restritivo)
  - Contas: 50 req/min, 500 req/hora

---

## 📝 Funcionalidades Detalhadas por Entidade

### User (Usuário)
| Funcionalidade | Descrição |
|----------------|-----------|
| **CRUD Completo** | Criar, ler, atualizar e deletar usuários |
| **Autenticação** | Login com email e senha, geração de token JWT |
| **Perfil** | Visualizar e editar dados do próprio perfil |
| **Listagem** | Paginação, ordenação e busca por nome/email |

### Account (Conta)
| Funcionalidade | Descrição |
|----------------|-----------|
| **CRUD Completo** | Criar, ler, atualizar e deletar contas |
| **Consulta de Saldo** | Endpoint específico para consultar saldo atualizado |
| **Filtros** | Filtrar por tipo de conta, status (ativa/inativa) |
| **Cálculo Automático** | Saldo calculado automaticamente com base nas transações |
| **Validação** | Impede operações em contas inativas |

### Transaction (Transação)
| Funcionalidade | Descrição |
|----------------|-----------|
| **CRUD Completo** | Criar, ler, atualizar e deletar transações |
| **Paginação Avançada** | Suporte a múltiplos parâmetros de paginação |
| **Ordenação** | Por data, valor, descrição (ASC/DESC) |
| **Filtros Combinados** | Categoria, tipo, período, conta, valor |
| **Atualização de Saldo** | Recalcula saldo da conta automaticamente |
| **Categorização** | Associar múltiplas categorias a uma transação |
| **Validação de Datas** | Impede transações com data futura |

### Category (Categoria)
| Funcionalidade | Descrição |
|----------------|-----------|
| **CRUD Completo** | Criar, ler, atualizar e deletar categorias |
| **Tipos Pré-definidos** | Enum com tipos comuns de receitas e despesas |
| **Filtros** | Filtrar por tipo (receita/despesa) |
| **Estatísticas** | Total de transações por categoria |
| **Personalização** | Cores e ícones para interface visual |

---

## 🎴 Implementação da Carta-Desafio: Limite de Requisições

### Descrição
Sistema de rate limiting configurável que controla o número de requisições que podem ser feitas à API por minuto e por hora, protegendo contra sobrecarga e uso abusivo.

### Características
- ✅ Limites configuráveis por minuto e por hora
- ✅ Limites diferentes por tipo de recurso
- ✅ Limites baseados em IP ou usuário autenticado
- ✅ Headers informativos nas respostas (X-Rate-Limit-Remaining)
- ✅ Mensagens de erro apropriadas (HTTP 429 - Too Many Requests)
- ✅ Endpoint administrativo para ajustar limites em tempo real
- ✅ Endpoint para usuários consultarem seu status de uso

### Limites Padrão
| Recurso | Requisições/Minuto | Requisições/Hora |
|---------|-------------------|------------------|
| **Global** | 100 | 1000 |
| **Transações** | 20 | 200 |
| **Contas** | 50 | 500 |
| **Autenticação** | 5 | 20 |

### Endpoints de Gerenciamento
```
GET  /api/config/rate-limits              # Visualizar limites atuais
PUT  /api/config/rate-limits              # Atualizar limites (apenas admin)
GET  /api/users/{id}/rate-limit-status    # Status de uso do usuário
```

---

## 📈 Roadmap de Desenvolvimento

### ✅ Fase 1 - Estrutura Base (Semanas 1-2)
- [x] Definição do tema e formação da equipe
- [x] Criação do repositório Git
- [x] Documentação inicial (README.md)
- [x] Modelagem das entidades
- [x] Definição de DTOs

### 🔄 Fase 2 - Implementação Core (Semanas 3-4)
- [ ] Configuração do projeto Spring Boot
- [ ] Implementação das entidades JPA
- [ ] Criação dos repositories
- [ ] Implementação dos services (CRUD)
- [ ] Implementação dos controllers
- [ ] Validações e tratamento de erros

### 🔄 Fase 3 - Funcionalidades Avançadas (Semana 5)
- [ ] Implementação de paginação e ordenação
- [ ] Implementação de filtros complexos
- [ ] Autenticação JWT
- [ ] **Implementação da Carta-Desafio (Rate Limiting)**
- [ ] Documentação Swagger

### 🔄 Fase 4 - Testes e Refinamento (Semanas 6-7)
- [ ] Testes unitários
- [ ] Testes de integração
- [ ] Ajustes finais
- [ ] Preparação da apresentação
- [ ] Deploy (se aplicável)

---

## 📚 Referências e Recursos

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Bucket4j - Rate Limiting](https://github.com/bucket4j/bucket4j)
- [JWT.io](https://jwt.io/)
- [Swagger/OpenAPI](https://swagger.io/)

---

## 📞 Contato

Para dúvidas ou sugestões sobre o projeto, entre em contato com a equipe através dos emails listados acima.

---

**Última atualização:** 30 de outubro de 2025
