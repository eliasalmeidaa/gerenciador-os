# Gerenciador de OS — API REST

API REST em **Java 21 + Spring Boot 3** para gerenciar **Ordens de Serviço (OS)** de uma assistência técnica.

**Dupla:** Elias Martins e Gustavo Maschette

## Problema e solução
Assistências técnicas controlam as OS em papel ou planilha: não sabem o status de cada serviço, qual técnico está com o equipamento nem o valor cobrado.
A API cadastra **clientes e técnicos** e controla o ciclo da OS: **ABERTA → EM_ANDAMENTO → CONCLUIDA** (ou **CANCELADA**).

## Tecnologias
Java 21 · Spring Boot 3 (Web, Data JPA, Validation) · Lombok · H2 · Swagger (springdoc) · JUnit 5 · Mockito · GitHub Actions

## Arquitetura em camadas
`controller` (HTTP e DTOs) → `service` (regras e transações) → `repository` (Spring Data JPA) → H2

## Modelagem
```
Cliente 1 ──── N OrdemServico N ──── 1 Tecnico
```
- **Cliente**: id, nome, telefone, email (único)
- **Tecnico**: id, nome, especialidade
- **OrdemServico**: id, equipamento, defeito, status, valor, dataAbertura, dataConclusao, cliente (`@ManyToOne`), tecnico (`@ManyToOne`)

## Endpoints
| Ação | Endpoint | Sucesso | Erros |
|---|---|---|---|
| Listar / pesquisar clientes | GET /api/clientes?nome=maria | 200 | |
| Consultar cliente | GET /api/clientes/{id} | 200 | 404 |
| Cadastrar cliente | POST /api/clientes | 201 | 400 |
| Atualizar cliente | PUT /api/clientes/{id} | 200 | 400, 404 |
| Excluir cliente | DELETE /api/clientes/{id} | 204 | 404, 409 |
| CRUD de técnicos | /api/tecnicos | 200 / 201 / 204 | 400, 404, 409 |
| Listar / filtrar OS | GET /api/ordens?status=ABERTA | 200 | |
| Consultar OS | GET /api/ordens/{id} | 200 | 404 |
| Abrir OS | POST /api/ordens | 201 | 400, 404 |
| Atribuir técnico | PATCH /api/ordens/{id}/tecnico/{tecnicoId} | 200 | 404, 409 |
| Iniciar OS | PATCH /api/ordens/{id}/iniciar | 200 | 409 |
| Concluir OS | PATCH /api/ordens/{id}/concluir | 200 | 400, 409 |
| Cancelar OS | PATCH /api/ordens/{id}/cancelar | 200 | 409 |

## Regras de negócio
- A OS só é aberta para cliente existente e começa com status **ABERTA**
- Só inicia se estiver ABERTA **e** com técnico atribuído
- Só conclui se estiver EM_ANDAMENTO e com valor maior que zero
- OS concluída ou cancelada não pode ser alterada
- Não aceita cliente com e-mail duplicado
- Cliente ou técnico com OS vinculada não pode ser excluído

## Validação e tratamento de erros
- DTOs (`record`) com Jakarta Validation: `@NotBlank`, `@Email`, `@Size`, `@NotNull`, `@DecimalMin`
- `@Valid @RequestBody` no POST, PUT e PATCH
- `ApiErrors` (`@RestControllerAdvice`) retorna `ResponseEntity<ProblemDetail>`:
  - **400** entrada inválida (lista os campos) ou valor inválido (`IllegalArgumentException`)
  - **404** recurso não encontrado (`ClienteNaoEncontradoException`, `TecnicoNaoEncontradoException`, `OrdemNaoEncontradaException`)
  - **409** conflito com o estado atual da OS (`IllegalStateException`)

## Como executar
```bash
mvn spring-boot:run
```
- API: `http://localhost:8080/api/ordens`
- H2: `http://localhost:8080/h2-console` · JDBC URL `jdbc:h2:mem:gerenciadoros` · usuário `sa` · senha vazia
- Swagger: `http://localhost:8080/swagger-ui/index.html`

## Demonstração (Postman ou cURL)
```bash
curl -i -X POST http://localhost:8080/api/clientes -H 'Content-Type: application/json' \
  -d '{"nome":"Maria Silva","telefone":"11999990000","email":"maria@email.com"}'

curl -i -X POST http://localhost:8080/api/tecnicos -H 'Content-Type: application/json' \
  -d '{"nome":"Carlos Souza","especialidade":"Notebooks"}'

curl -i -X POST http://localhost:8080/api/ordens -H 'Content-Type: application/json' \
  -d '{"equipamento":"Notebook Dell","defeito":"Não liga","clienteId":1}'

curl -i -X PATCH http://localhost:8080/api/ordens/1/iniciar          # 409: sem técnico
curl -i -X PATCH http://localhost:8080/api/ordens/1/tecnico/1
curl -i -X PATCH http://localhost:8080/api/ordens/1/iniciar          # 200 EM_ANDAMENTO
curl -i -X PATCH http://localhost:8080/api/ordens/1/concluir -H 'Content-Type: application/json' -d '{"valor":150.00}'
curl -i "http://localhost:8080/api/ordens?status=CONCLUIDA"

curl -i -X POST http://localhost:8080/api/clientes -H 'Content-Type: application/json' -d '{"nome":"","email":"x"}'   # 400
```

## Testes (JUnit 5 + Mockito)
`OrdemServicoServiceTest` e `ClienteServiceTest`: `@ExtendWith(MockitoExtension.class)`, `@Mock` nos repositories, `@InjectMocks` no service, `@BeforeEach`, `assertThrows`, `assertAll`, `verify` com `times(1)` e `never()`, `ArgumentCaptor` e `@ParameterizedTest` com `@ValueSource`.
```bash
mvn test
```

## Integração contínua
`.github/workflows/ci.yml`: executa em push na `main` e em Pull Requests para a `main`, usa runner Ubuntu com Java 21, compila com Maven e executa os testes. Falha se a compilação ou algum teste falhar.
