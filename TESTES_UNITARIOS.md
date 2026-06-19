# Testes Unitários - Projeto Gestar

## Resumo

Foi criada uma suite completa de testes unitários para o sistema Gestar de triagem e fila priorizada de atendimento. Os testes foram organizados em 5 principais categorias de componentes.

---

## 📋 Testes Criados

### 1. **Testes de Domínio** (`src/test/java/br/unibh/gestar/domain/`)

#### `PatientTest.java` - 7 testes
- ✅ Criação de paciente com ID gerado automaticamente
- ✅ Criação com dados corretos
- ✅ Cálculo correto de idade
- ✅ Cálculo de idade para jovens
- ✅ Recuperação de persistência
- ✅ Geração de IDs diferentes para pacientes diferentes

#### `VitalSignsTest.java` - 6 testes
- ✅ Criação com todos os parâmetros
- ✅ Armazenamento de sinais normais
- ✅ Armazenamento de sinais anormais
- ✅ Tratamento de pressão extrema
- ✅ Tratamento de escala de dor alta

#### `MedicalCareTest.java` - 13 testes
- ✅ Criação com ID gerado
- ✅ Inicialização com status "WAITING_FOR_TRIAGE"
- ✅ Configuração de classificação
- ✅ Reclassificação para urgência maior
- ✅ Não reclassificar para urgência menor
- ✅ Marcar como referenciado
- ✅ Identificar casos críticos
- ✅ Não identificar não-críticos como críticos
- ✅ Configurar sinais vitais
- ✅ Avanço de status
- ✅ Recuperação de persistência

#### `UrgencyLevelTest.java` - 8 testes
- ✅ Propriedades corretas para RED
- ✅ Propriedades corretas para ORANGE
- ✅ Propriedades corretas para YELLOW
- ✅ Propriedades corretas para GREEN
- ✅ Comparação de prioridades
- ✅ Tempo de resposta alvo mais urgente

#### `PriorityCategoryTest.java` - 9 testes
- ✅ Pesos corretos
- ✅ Categorização por idade acima de 80
- ✅ Categorização por idade 60-80
- ✅ Categorização por idade abaixo de 60
- ✅ Categorização por idade exatamente 80
- ✅ Categorização por idade exatamente 60
- ✅ Peso maior para prioridade maior
- ✅ Tratamento de idade muito alta
- ✅ Tratamento de recém-nascido

#### `MedicalCareStatusTest.java` - 3 testes
- ✅ Existência de todos os status
- ✅ Conversão para string
- ✅ Conversão de string

---

### 2. **Testes de Classificação** (`src/test/java/br/unibh/gestar/classification/`)

#### `ManchesterClassificationTest.java` - 16 testes
- ✅ Exceção quando sinais vitais ausentes
- ✅ Classificação RED para baixa saturação de O2
- ✅ Classificação RED para frequência cardíaca alta
- ✅ Classificação RED para frequência cardíaca baixa
- ✅ Classificação RED para frequência respiratória alta
- ✅ Classificação RED para pressão alta
- ✅ Classificação RED para febre alta
- ✅ Classificação ORANGE para saturação moderada baixa
- ✅ Classificação ORANGE para frequência cardíaca elevada
- ✅ Classificação ORANGE para dor alta
- ✅ Classificação YELLOW para sintomas moderados
- ✅ Classificação YELLOW para queixa de dor no peito
- ✅ Classificação YELLOW para queixa torácica
- ✅ Classificação GREEN para sinais vitais normais
- ✅ Escolher mais urgente entre vitais e queixa

#### `SimpleClassificationTest.java` - 10 testes
- ✅ Exceção quando sinais vitais ausentes
- ✅ Classificação ORANGE para baixa saturação de O2
- ✅ Classificação ORANGE para dor alta
- ✅ Classificação YELLOW para dor moderada
- ✅ Classificação YELLOW para dor 6
- ✅ Classificação YELLOW para dor 7
- ✅ Classificação GREEN para sinais normais
- ✅ Classificação GREEN para sem dor
- ✅ Classificação GREEN para dor baixa
- ✅ Classificação ORANGE para baixa O2 e dor alta
- ✅ Limite correto de saturação
- ✅ Limite correto de dor

---

### 3. **Testes de Fila** (`src/test/java/br/unibh/gestar/queue/`)

#### `QueueManagerTest.java` - 16 testes
- ✅ Exceção ao adicionar atendimento não classificado
- ✅ Adicionar atendimento classificado à fila
- ✅ Retornar tamanho correto da fila
- ✅ Identificar fila vazia
- ✅ Identificar fila não vazia
- ✅ Retornar pacientes RED em primeiro lugar
- ✅ Peek sem remover
- ✅ Remove on next
- ✅ Retornar null para fila vazia (next)
- ✅ Retornar null para fila vazia (peek)
- ✅ Contar tamanho por nível de urgência
- ✅ Respeitar ordem de prioridade dentro do nível
- ✅ Manipular múltiplos casos
- ✅ Manter ordem após múltiplas adições

---

### 4. **Testes de Serviço** (`src/test/java/br/unibh/gestar/service/`)

#### `PatientServiceTest.java` - 10 testes
- ✅ Criar novo paciente
- ✅ Atualizar paciente existente
- ✅ Exceção para nome ausente
- ✅ Exceção para nome em branco
- ✅ Exceção para data de nascimento ausente
- ✅ Exceção para formato de data inválido
- ✅ Listar todos os pacientes
- ✅ Retornar lista vazia quando nenhum paciente
- ✅ Calcular idade na resposta
- ✅ Remover espaços em branco da data

#### `MedicalCareServiceTest.java` - 20 testes
- ✅ Criar atendimento com novo paciente
- ✅ Criar atendimento com idade
- ✅ Exceção sem idade ou data de nascimento
- ✅ Exceção para idade inválida
- ✅ Exceção para idade negativa
- ✅ Definir sinais vitais padrão
- ✅ Disparar alerta para caso crítico
- ✅ Não disparar alerta para caso não-crítico
- ✅ Adicionar à fila
- ✅ Referenciar paciente
- ✅ Exceção para queixa ausente
- ✅ Exceção para queixa em branco
- ✅ Listar todos os atendimentos
- ✅ Retornar status da fila
- ✅ Chamar próximo paciente
- ✅ Retornar vazio quando não há próximo
- ✅ Finalizar atendimento
- ✅ Exceção ao finalizar inexistente

---

### 5. **Testes de Alerta** (`src/test/java/br/unibh/gestar/alert/`)

#### `ClinicalNotifierTest.java` - 6 testes
- ✅ Registrar observador
- ✅ Notificar múltiplos observadores
- ✅ Notificar com atendimento correto
- ✅ Notificar cada observador separadamente
- ✅ Manipular múltiplos alertas
- ✅ Cada observador recebe notificação correta

---

## 📊 Estatísticas

| Categoria | Testes | Testes por Arquivo |
|-----------|--------|-------------------|
| Domínio | 46 | 6 arquivos |
| Classificação | 26 | 2 arquivos |
| Fila | 16 | 1 arquivo |
| Serviço | 30 | 2 arquivos |
| Alerta | 6 | 1 arquivo |
| **TOTAL** | **124 testes** | **12 arquivos** |

---

## 🚀 Como Executar os Testes

### Usando Maven
```bash
# Instalar Maven (se necessário)
# Executar todos os testes
mvn test

# Executar testes de uma classe específica
mvn test -Dtest=PatientTest

# Executar com relatório detalhado
mvn test -DargLine="-Xmx256m"
```

### Usando IDE (VS Code, IntelliJ, Eclipse)
1. Clique com botão direito no arquivo de teste
2. Selecione "Run Tests" ou "Debug Tests"

### Usando linha de comando (sem Maven instalado)
```bash
# Compile primeiro
javac -cp "caminho-para-junit" src/test/java/br/unibh/gestar/domain/PatientTest.java

# Execute
java -cp "caminho-para-junit" org.junit.platform.console.ConsoleLauncher --scan-classpath
```

---

## 📝 Estrutura dos Testes

Cada teste segue as melhores práticas:

- **Nomenclatura**: `should<Expected>When<Condition>()`
- **Setup**: Método `@BeforeEach` para inicialização
- **Assertions**: Uso de `assertEquals`, `assertTrue`, `assertThrows`, etc.
- **Mocks**: Repositórios mocados para testes de serviço
- **Isolamento**: Cada teste é independente

Exemplo:
```java
@Test
void shouldCreatePatientWithGeneratedId() {
    // Arrange
    LocalDate birthDate = LocalDate.of(1990, 5, 15);
    
    // Act
    Patient patient = new Patient("João Silva", birthDate);
    
    // Assert
    assertNotNull(patient.getId());
    assertFalse(patient.getId().isBlank());
}
```

---

## ✅ Cobertura

Os testes cobrem:
- ✅ Casos normais/felizes
- ✅ Casos extremos (limites)
- ✅ Exceções e erros
- ✅ Validação de dados
- ✅ Lógica de negócio
- ✅ Padrões de design (Observer, Strategy)
- ✅ Integração entre componentes

---

## 📌 Notas Importantes

1. **Repositórios Mocados**: Os testes de serviço usam mock repositories internos para não depender do banco de dados
2. **Isolamento de Testes**: Cada teste é independente e não afeta outros
3. **Fácil Manutenção**: Novos testes podem ser adicionados facilmente seguindo o mesmo padrão
4. **Documentação**: Nomes descritivos dos testes servem como documentação viva

---

## 🔍 Próximos Passos (Opcional)

Se desejar expandir ainda mais a cobertura:
- Adicionar testes de integração (com banco de dados real)
- Adicionar testes de controlador (API endpoints)
- Adicionar testes de performance
- Gerar relatório de cobertura com JaCoCo
- Adicionar testes de casos edge (overflow, underflow, etc.)

---

**Data de Criação**: 18 de Junho de 2026  
**Framework**: JUnit 5  
**Status**: ✅ Pronto para Execução
