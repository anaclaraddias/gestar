# Diagramas e Guia de Implementação do Gestar

Modelagem da fatia implementada: **triagem com classificação de risco + fila priorizada**.
As descrições textuais dos casos de uso estão em `requisitos.md` (Seção 7); aqui ficam
os diagramas e o guia que orienta a codificação.

---

## 1. Diagrama de Casos de Uso

```mermaid
graph LR
    PT([Profissional de Triagem])
    MED([Médico])
    PAC([Paciente])

    UC1(Realizar triagem e classificar risco)
    UC2(Inserir na fila priorizada)
    UC3(Chamar próximo paciente)
    UC4(Emitir alerta clínico)
    UC5(Finalizar atendimento)

    PT --- UC1
    PAC --- UC1
    UC1 -. include .-> UC2
    UC1 -. extend: caso crítico .-> UC4
    MED --- UC3
    MED --- UC5
```

---

## 2. Diagrama de Classes

```mermaid
classDiagram
    class Paciente {
        -String id
        -String nome
        -LocalDate dataNascimento
        +getNome() String
    }
    class SinaisVitais {
        -int frequenciaCardiaca
        -double temperatura
        -int saturacao
        -int escalaDor
    }
    class NivelUrgencia {
        <<enumeration>>
        VERMELHO
        LARANJA
        AMARELO
        VERDE
        -int prioridade
        -int tempoAlvoMinutos
        +getPrioridade() int
    }
    class CategoriaPrioridade {
        <<enumeration>>
        PRIORIDADE_MAXIMA
        PREFERENCIAL
        NORMAL
        -int peso
    }
    class StatusAtendimento {
        <<enumeration>>
        AGUARDANDO_TRIAGEM
        EM_TRIAGEM
        NA_FILA
        EM_ATENDIMENTO
        FINALIZADO
        ENCAMINHADO
    }
    class ProtocolType {
        <<enumeration>>
        MANCHESTER
        SIMPLE
    }
    class Atendimento {
        -String id
        -Paciente paciente
        -SinaisVitais sinaisVitais
        -String queixaPrincipal
        -NivelUrgencia nivel
        -CategoriaPrioridade categoriaPrioridade
        -StatusAtendimento status
        -LocalDateTime dataHoraChegada
        +avancarStatus(StatusAtendimento) void
        +reclassificar(NivelUrgencia) void
        +ehCritico() boolean
    }
    class EstrategiaClassificacao {
        <<interface>>
        +classificar(Atendimento) NivelUrgencia
    }
    class ClassificacaoManchester {
        +classificar(Atendimento) NivelUrgencia
    }
    class ClassificacaoSimples {
        +classificar(Atendimento) NivelUrgencia
    }
    class FabricaEstrategiaClassificacao {
        +criar(TipoProtocolo) EstrategiaClassificacao$
    }
    class RepositorioAtendimento {
        <<interface>>
        +salvar(Atendimento) void
        +obterPorId(String) Atendimento
        +listarTodos() List~Atendimento~
    }
    class RepositorioAtendimentoEmMemoria {
        -Map~String,Atendimento~ dados
        +salvar(Atendimento) void
        +obterPorId(String) Atendimento
        +listarTodos() List~Atendimento~
    }
    class GerenciadorFila {
        -PriorityQueue~Atendimento~ fila
        +adicionar(Atendimento) void
        +proximo() Atendimento
        +tamanho() int
    }
    class ObservadorAlerta {
        <<interface>>
        +notificar(Atendimento) void
    }
    class PainelMedico {
        +notificar(Atendimento) void
    }
    class NotificadorClinico {
        -List~ObservadorAlerta~ observadores
        +registrar(ObservadorAlerta) void
        +dispararAlerta(Atendimento) void
    }
    class ServicoTriagem {
        -EstrategiaClassificacao estrategia
        -RepositorioAtendimento repositorio
        -GerenciadorFila fila
        -NotificadorClinico notificador
        +realizarTriagem(Paciente, SinaisVitais, String) Atendimento
        +encaminhar(Atendimento, String, String) void
        +reclassificar(Atendimento, SinaisVitais) Atendimento
        +chamarProximo() Atendimento
        +finalizar(Atendimento) void
    }

    Atendimento --> Paciente
    Atendimento --> NivelUrgencia
    Atendimento --> CategoriaPrioridade
    Atendimento --> StatusAtendimento
    Atendimento *-- SinaisVitais
    ClassificacaoManchester ..|> EstrategiaClassificacao
    ClassificacaoSimples ..|> EstrategiaClassificacao
    FabricaEstrategiaClassificacao ..> EstrategiaClassificacao
    FabricaEstrategiaClassificacao ..> TipoProtocolo
    RepositorioAtendimentoEmMemoria ..|> RepositorioAtendimento
    PainelMedico ..|> ObservadorAlerta
    NotificadorClinico o-- ObservadorAlerta
    ServicoTriagem --> EstrategiaClassificacao
    ServicoTriagem --> RepositorioAtendimento
    ServicoTriagem --> GerenciadorFila
    ServicoTriagem --> NotificadorClinico
```

---

## 3. Guia de Implementação

| Classe | Responsabilidade | Padrão / SOLID |
|--------|------------------|----------------|
| `Paciente`, `SinaisVitais` | Dados do paciente e da aferição | Entidade / Value Object (SRP) |
| `Atendimento` | Representa um atendimento e seu estado | Entidade (SRP) |
| `NivelUrgencia` | Níveis de risco com prioridade e tempo-alvo | Enum |
| `StatusAtendimento` | Estados do ciclo de vida do atendimento | Enum |
| `EstrategiaClassificacao` | Contrato para classificar o risco | **Strategy** (OCP, LSP, ISP) |
| `ClassificacaoManchester` / `ClassificacaoSimples` | Regras concretas de classificação | **Strategy** |
| `FabricaEstrategiaClassificacao` | Cria a estratégia de acordo com o protocolo | **Factory Method** (criacional) |
| `RepositorioAtendimento` | Contrato de persistência | **Repository** (DIP) |
| `RepositorioAtendimentoEmMemoria` | Persistência em memória (sem banco) | **Repository** |
| `GerenciadorFila` | Ordena por prioridade e, em caso de empate, por chegada | Lógica testável |
| `ObservadorAlerta` / `PainelMedico` | Reagem a casos críticos | **Observer** (comportamental) |
| `NotificadorClinico` | Dispara alertas aos observadores | **Observer** (Subject) |
| `ServicoTriagem` | Orquestra triagem → fila → alerta | Depende de interfaces (DIP, SRP) |

**Coração testável:** `GerenciadorFila`. Use `PriorityQueue` com um `Comparator` que
ordena **(1)** por `NivelUrgencia.prioridade` (Vermelho mais urgente), **(2)** por
`CategoriaPrioridade` (idoso 80+ > PCD/idoso 60+ > normal) e **(3)** por
`dataHoraChegada` (mais antigo primeiro). A unidade usa 4 cores (sem Azul).

## 4. Estrutura de pacotes sugerida (Maven)

```
src/main/java/br/unibh/gestar/
├── domain/         Patient, MedicalCare, VitalSigns, UrgencyLevel, MedicalCareStatus
├── classification/ ClassificationStrategy, ManchesterClassification, SimpleClassification,
│                   ProtocolType, ClassificationStrategyFactory
├── queue/          QueueManager
├── repository/     MedicalCareRepository, MedicalCareRepositoryInMemory
├── alert/          AlertObserver, MedicalPanel, ClinicalNotifier
├── service/        TriageService
└── Main.java       (demonstração do fluxo)
src/test/java/br/unibh/gestar/
├── queue/          QueueManagerTest
├── classification/ ManchesterClassificationTest
└── service/        TriageServiceTest
```

## 5. Mapa dos padrões (para a documentação e a defesa)

| Categoria | Padrão | Onde | Justificativa |
|-----------|--------|------|---------------|
| Criacional | Factory Method | `FabricaEstrategiaClassificacao` | Cria a estratégia certa sem acoplar o serviço às classes concretas |
| Estrutural | Repository | `RepositorioAtendimento` | Isola a persistência; permite trocar memória por banco sem afetar a regra |
| Comportamental | Strategy | `EstrategiaClassificacao` | Troca o protocolo de classificação sem reescrever a fila |
| Comportamental | Observer | `NotificadorClinico` / `ObservadorAlerta` | Notifica o corpo clínico em casos críticos |

> **Evolução futura:** mantivemos `StatusAtendimento` como enum por simplicidade. O ciclo
> de vida do atendimento poderia ser refatorado para o padrão **State** (GoF) numa próxima
> iteração. Não é necessário neste escopo, já que os quatro padrões adotados cobrem as
> categorias criacional, estrutural e comportamental.
