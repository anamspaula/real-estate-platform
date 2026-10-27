# ADR-0001: Banco de dados por tenant, sem banco central

- **Status:** Aceita
- **Data:** 2026-10-20

## Contexto

O sistema atende várias imobiliárias (tenants) numa mesma instalação, e os dados de uma nunca podem ser acessados por outra. Isso inclui pessoas, imóveis, contratos e todo o histórico financeiro, que são dados sensíveis.

O projeto é desenvolvido por uma única pessoa, com objetivo de estudo e portfólio. A estratégia de isolamento precisa, portanto, ser sustentável de operar sozinha, mas também deve servir como exercício de aprendizado sobre multi-tenancy.

## Decisão

Cada imobiliária tem o seu próprio banco de dados PostgreSQL, e não existe um banco central de catálogo.

- O tenant é identificado pelo subdomínio da requisição (`imobiliaria-a.dominio`).
- O nome do banco é derivado do identificador por convenção (`imobiliaria-a` usa o banco `imob_imobiliaria_a`), sem nenhum registro intermediário.
- O identificador é validado por um padrão rígido (letras minúsculas, números e hífen) e uma lista de nomes reservados, porque vem da URL e se transforma em nome de banco.
- Os tenants existentes são descobertos pelo catálogo do próprio PostgreSQL (`pg_database`).
- A conexão padrão da aplicação aponta para o banco de manutenção `postgres`, usado apenas para listar e provisionar tenants. Nenhum dado de negócio fica nele.
- Usuários do sistema também vivem dentro do banco de cada tenant.

## Alternativas consideradas

**Banco compartilhado com coluna `tenant_id`.** É a opção mais simples de operar e a mais comum em SaaS, mas o isolamento depende de nenhuma consulta esquecer o filtro por tenant. Um único erro expõe dados de outra imobiliária.

**Schema por tenant no mesmo banco.** Oferece bom isolamento com um único pool de conexões e foi a alternativa mais próxima. Foi preterida porque o isolamento por banco é mais forte e permite backup, restauração e remoção de cada tenant de forma totalmente independente.

**Banco por tenant com banco central de catálogo.** Facilitaria guardar a lista de tenants, credenciais por banco e um administrador da plataforma. Foi descartada para evitar um ponto central que concentra informação de todos os clientes e que precisaria ser mantido em sincronia com os bancos reais.

## Consequências

**O que fica mais fácil**

- Isolamento forte: um erro de consulta nunca alcança dados de outro tenant, porque a conexão só enxerga o banco daquele tenant.
- Backup e restauração por imobiliária, sem afetar as demais.
- Remoção completa dos dados de um cliente que sai da plataforma, apagando um único banco.
- Os módulos de negócio não precisam conhecer o conceito de tenant.

**O que fica mais difícil**

- As migrations precisam rodar em todos os bancos, e uma falha em um tenant precisa ser tratada sem derrubar os outros.
- Cadastrar uma imobiliária deixa de ser um `INSERT` e vira um processo de provisionamento (criar o banco, aplicar as migrations, criar o primeiro administrador), com desfazimento em caso de falha.
- Cada tenant precisa do próprio pool de conexões, criado sob demanda. O total de conexões cresce com o número de tenants e esbarra no limite do PostgreSQL.
- Consultas e relatórios que atravessam tenants exigem percorrer todos os bancos.
- O login precisa conhecer o tenant antes de autenticar, o que torna o subdomínio obrigatório.
- O mesmo e-mail pode existir em mais de uma imobiliária, como contas independentes.

**Riscos aceitos**

- Por causa da convenção de nomes, todos os bancos ficam no mesmo servidor PostgreSQL.
- Não há onde guardar um administrador da plataforma. Se um painel global for necessário no futuro, será preciso rever a ausência de um banco central.
- Na evolução para microsserviços, cada serviço terá seus próprios bancos por tenant, multiplicando a quantidade de bancos a operar.
