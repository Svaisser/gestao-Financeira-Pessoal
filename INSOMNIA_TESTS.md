# 📝 Guia de Testes - Insomnia

## 🔐 1. Registrar Novo Usuário

**Endpoint:** `POST http://localhost:8080/api/usuarios/registro`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "email": "teste@email.com",
  "nome": "Usuário Teste",
  "senha": "senha123456",
  "confirmarSenha": "senha123456"
}
```

**Resposta de Sucesso (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwic2NvcGVzIjoiMTAwIn0.abc123...",
  "usuarioId": 1,
  "email": "teste@email.com",
  "nome": "Usuário Teste"
}
```

---

## 🔑 2. Login com Usuário Existente

**Endpoint:** `POST http://localhost:8080/api/usuarios/login`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "email": "teste@email.com",
  "senha": "senha123456"
}
```

**Resposta de Sucesso (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "usuarioId": 1,
  "email": "teste@email.com",
  "nome": "Usuário Teste"
}
```

---

## 👤 3. Listar Todos os Usuários (Requer Token)

**Endpoint:** `GET http://localhost:8080/api/usuarios`

**Headers:**
```
Authorization: Bearer <COLE_O_TOKEN_AQUI>
```

---

## 💳 4. Criar Nova Conta

**Endpoint:** `POST http://localhost:8080/api/contas`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <COLE_O_TOKEN_AQUI>
```

**Body (JSON):**
```json
{
  "nome": "Conta Corrente Nubank",
  "tipo": "CORRENTE",
  "saldo": 1000.00,
  "moeda": "BRL",
  "usuario": {
    "id": 1
  }
}
```

---

## 🏷️ 5. Criar Categoria

**Endpoint:** `POST http://localhost:8080/api/categorias`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <COLE_O_TOKEN_AQUI>
```

**Body (JSON):**
```json
{
  "nome": "Alimentação",
  "tipo": "DESPESA",
  "descricao": "Gastos com alimentação",
  "cor": "#FF5733",
  "icone": "food"
}
```

---

## 💸 6. Registrar Transação (Despesa)

**Endpoint:** `POST http://localhost:8080/api/transacoes`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <COLE_O_TOKEN_AQUI>
```

**Body (JSON):**
```json
{
  "descricao": "Compra no supermercado",
  "valor": 150.50,
  "tipo": "DESPESA",
  "data": "2025-11-27T15:30:00",
  "observacoes": "Compras da semana",
  "conta": {
    "id": 1
  },
  "categorias": [
    {
      "id": 1
    }
  ]
}
```

---

## 📊 7. Listar Transações por Conta

**Endpoint:** `GET http://localhost:8080/api/transacoes/conta/1`

**Headers:**
```
Authorization: Bearer <COLE_O_TOKEN_AQUI>
```

---

## 📌 Outros Exemplos de Emails para Testar:

```
usuario1@gmail.com
usuario2@gmail.com
usuario3@gmail.com
maria@email.com
pedro@email.com
ana@email.com
```

---

## ⚠️ Erros Comuns e Soluções:

| Erro | Causa | Solução |
|------|-------|--------|
| Email já registrado | Email duplicado | Use outro email |
| Senhas não conferem | confirmarSenha ≠ senha | Use senhas iguais |
| Senha muito curta | Menos de 8 caracteres | Use mínimo 8 caracteres |
| Usuário ou senha inválidos | Credenciais incorretas no login | Verifique email/senha |
| Token inválido | Token expirado ou inválido | Faça login novamente |
| 400 Bad Request | Dados inválidos | Verifique formato do JSON |

---

## 🚀 Dica: Salvando o Token no Insomnia

Depois de fazer login ou registro:

1. No **Insomnia**, vá em **Manage Environments**
2. Clique em **Edit as JSON**
3. Adicione:

```json
{
  "token": "cole_aqui_o_token_que_recebeu"
}
```

4. Agora em qualquer requisição, use o header:

```
Authorization: Bearer _.token
```

Assim o Insomnia preencherá automaticamente! 🎯
