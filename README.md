# 🐾 Sistema de Gestão para Clínica Veterinária (POO)

> **Disciplina:** Programação Orientada a Objetos (POO)  
> **Autores:** Emily Silva & Gabriel Nunes  
> **Tecnologias:** Java 21 LTS, Swing (FlatLaf Modern UI), SQLite JDBC, Maven, JUnit 5  

---

## 📌 1. Visão Geral e Arquitetura do Sistema

O sistema foi modelado e implementado estritamente a partir dos requisitos e diagramas desenvolvidos no **Astah UML** (Diagrama de Classes, Casos de Uso, Diagrama de Estados e Diagramas de Sequência SD01, SD02 e SD03).

A arquitetura adotada é o padrão **BCE (Boundary-Control-Entity)** organizado em camadas:

```
src/main/java/com/clinicaveterinaria/
├── boundary/           # [Boundary / View] Telas gráficas modernas em Java Swing + FlatLaf
│   ├── MainFrame.java
│   ├── TelaDashboardPanel.java
│   ├── TelaAgendamentoPanel.java    # SD01 - Agendar Consulta
│   ├── TelaAtendimentoPanel.java    # SD02 - Realizar Consulta e Prontuário
│   ├── TelaCaixaPanel.java          # SD03 - Registrar Pagamento
│   └── TelaCadastrosPanel.java      # Manutenção de Tutores, Animais e Veterinários
├── control/            # [Control / Application] Orquestração e Regras de Negócio
│   ├── ControladorConsulta.java     # Validação de Conflito de Horário (RN09) e Agendamento
│   ├── ControladorAtendimento.java  # Prontuário Imutável (RN03/RN05/RN08) e Prescrição
│   ├── ControladorFinanceiro.java   # Liquidação de Pagamento e Emissão de Recibo
│   └── ControladorCadastros.java    # Manutenção de Entidades
├── entity/             # [Entity / Domain Model] Entidades puras do negócio (Modelagem Astah)
│   ├── Tutor.java, Animal.java, Veterinario.java
│   ├── Consulta.java, Prontuario.java, Exame.java, Vacina.java, Pagamento.java
│   └── enums/ (StatusConsulta, MetodoPagamento)
├── patterns/           # [Padrões de Projeto do GoF]
│   ├── state/          # Pattern 1: State (Ciclo de Vida da Consulta)
│   ├── strategy/       # Pattern 2: Strategy (Processamento de Pagamento Intercambiável)
│   └── factory/        # Pattern 3: Factory Method (Criação de Estratégias e Estados)
└── repository/         # [Infrastructure / Data Access] Persistência Relacional em SQLite
    ├── DatabaseManager.java, SeedDatabase.java
    ├── TutorRepository.java, AnimalRepository.java, VeterinarioRepository.java
    ├── ConsultaRepository.java, ProntuarioRepository.java, PagamentoRepository.java
    └── ExameRepository.java, VacinaRepository.java
```

---

## 🎯 2. Os 3 Padrões de Projeto (Design Patterns) Implementados

### 1️⃣ Padrão STATE (Comportamental)
* **Onde está:** Pacote `com.clinicaveterinaria.patterns.state` e classe `Consulta`.
* **Classes:** `ConsultaState` (Interface), `AgendadaState`, `EmAndamentoState`, `RealizadaState`, `PagaState`, `CanceladaState`.
* **Por que foi usado:** Materializa diretamente o **Diagrama de Máquina de Estados da Consulta**. Em vez de usar múltiplos `if/else` e flags booleanas para checar o status, cada estado é um objeto que encapsula o comportamento válido. 
* **Regra de Negócio Garantida:** A **RN07** (que impede o pagamento de uma consulta não realizada) é validada diretamente pelo estado: tentar chamar `consulta.pagar()` em uma consulta no estado `AgendadaState` dispara automaticamente uma `IllegalStateException`.

### 2️⃣ Padrão STRATEGY (Comportamental)
* **Onde está:** Pacote `com.clinicaveterinaria.patterns.strategy` e classe `Pagamento`.
* **Classes:** `PagamentoStrategy` (Interface), `PixPaymentStrategy`, `CreditCardPaymentStrategy`, `DebitCardPaymentStrategy`, `CashPaymentStrategy`.
* **Por que foi usado:** Permite que diferentes algoritmos e gateways de pagamento (PIX com chave dinâmica, Cartão com NSU/adquirente, Dinheiro com conferência de caixa) sejam executados de forma intercambiável sem acoplar o `ControladorFinanceiro` às especificidades de cada operadora.

### 3️⃣ Padrão FACTORY METHOD (Criacional)
* **Onde está:** Pacote `com.clinicaveterinaria.patterns.factory`.
* **Classes:** `PagamentoStrategyFactory`, `ConsultaStateFactory`.
* **Por que foi usado:** Desacopla a camada visual da instanciação concreta das estratégias e dos estados, centralizando a lógica de criação a partir dos enums correspondentes.

---

## 💎 3. Princípios SOLID Aplicados

* **S (Single Responsibility Principle):** Cada classe possui uma única responsabilidade. O `ControladorFinanceiro` não grava diretamente no SQLite (delega ao `PagamentoRepository`), e as entidades não possuem lógica de interface com usuário.
* **O (Open/Closed Principle):** O sistema está aberto para extensão e fechado para modificação. Para adicionar um novo método de pagamento (ex: Convênio ou Boleto), basta criar uma nova classe que implementa `PagamentoStrategy` sem alterar o código existente do caixa.
* **L (Liskov Substitution Principle):** Qualquer implementação de `PagamentoStrategy` ou de `ConsultaState` pode substituir sua interface base sem quebrar o comportamento do sistema.
* **I (Interface Segregation Principle):** As interfaces (`ConsultaState`, `PagamentoStrategy`) são coesas e específicas, contendo apenas os métodos essenciais ao seu contexto.
* **D (Dependency Inversion Principle):** Os controladores e serviços dependem de abstrações (interfaces e contratos de repositório), facilitando testes unitários com mocks.

---

## 🚀 4. Como Executar a Aplicação

### Pré-requisitos
* Java 21 LTS (OpenJDK)
* Maven 3.8+ (ou o script automático incluído)

### Opção 1: Via Script Rápido (Recomendado)
```bash
./run.sh
```

### Opção 2: Via Maven Direto
```bash
mvn clean compile exec:java
```

### Opção 3: Executar o Fat JAR Gerado
```bash
java -jar target/clinica-veterinaria-1.0.0.jar
```

---

## 🧪 5. Executando os Testes Automatizados (JUnit 5)

Para rodar a suíte completa de testes automatizados que validam os padrões e regras de negócio:
```bash
mvn test
```

---

## 🎤 6. Roteiro Sugerido para Apresentação em Aula

1. **Abertura:** Mostrar o sistema rodando com a interface moderna Dark/Light FlatLaf.
2. **Aba 1 (Agendamento - SD01):**
   * Agendar uma consulta para um animal.
   * Tentar agendar outra consulta para o mesmo veterinário em horário próximo para demonstrar o bloqueio de conflito de agenda (**RN09**).
3. **Aba 2 (Consultório & Prontuário - SD02):**
   * Selecionar a consulta e clicar em *"Iniciar Atendimento"*.
   * Mostrar o prontuário eletrônico com histórico prévio carregado.
   * Solicitar um Exame, prescrever uma Vacina e inserir o diagnóstico médico.
   * Clicar em *"Finalizar Atendimento"* e mostrar que o status mudou para *"Realizada"*.
4. **Aba 3 (Caixa & Pagamentos - SD03):**
   * Mostrar a consulta finalizada liberada no caixa (**RN07**).
   * Selecionar a forma de pagamento (ex: **PIX** ou **Cartão de Crédito**).
   * Processar o pagamento e exibir o recibo autenticado emitido na tela.
5. **Apresentação do Código e Padrões:**
   * Abrir o pacote `patterns.state` e explicar o padrão **State**.
   * Abrir o pacote `patterns.strategy` e explicar o padrão **Strategy**.
   * Abrir o pacote `patterns.factory` e explicar o padrão **Factory Method**.
   * Destacar a aderência aos diagramas do Astah e aos princípios **SOLID**.
