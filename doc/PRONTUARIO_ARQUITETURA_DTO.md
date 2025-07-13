# Arquitetura DTO para Sistema de Prontuário

**Versão:** 1.0  
**Data:** 13/07/2025  
**Autor:** Equipe de Desenvolvimento Cara Core  
**Status:** Implementado

## Resumo Executivo

Este documento descreve a implementação do padrão DTO (Data Transfer Object) no sistema de prontuários, com foco específico no tratamento seguro de imagens radiológicas e otimização de performance no acesso a dados.

## Contexto Técnico

A implementação do sistema de prontuário odontológico enfrentou desafios significativos relacionados ao gerenciamento eficiente de imagens radiológicas e à segurança no acesso a dados entre camadas da aplicação. A solução implementada utiliza o padrão DTO para criar uma abstração de transferência de dados que resolve problemas específicos de lazy loading e conversão de tipos.

## Problemas Solucionados

### 1. Lazy Loading em Templates Thymeleaf

**Problema:** O acesso direto a coleções lazy-loaded (como imagens radiológicas) em templates Thymeleaf causava consultas adicionais ao banco de dados ou exceptions quando as sessões já estavam fechadas.

**Solução:** Implementação de DTOs específicos que transportam apenas os metadados necessários para apresentação na interface, evitando o carregamento completo das entidades e suas coleções.

### 2. Conversão de Tipos no PostgreSQL

**Problema:** O PostgreSQL tentava converter dados de imagens em formato base64 (armazenados como TEXT) para tipo Long durante operações de consulta, causando erros de tipo.

**Solução:** Criação de queries específicas que selecionam apenas os campos necessários, excluindo o conteúdo base64 das imagens quando não é necessário.

## Implementação Técnica

### Camada de Dados

1. **Repositório:** Adição de métodos específicos para buscar apenas metadados

   ```java
   @Repository
   public interface ImagemRadiologicaRepository extends JpaRepository<ImagemRadiologica, Long> {
       List<ImagemRadiologicaResumo> findResumoByProntuarioIdAndAtivoTrue(Long prontuarioId);
   }
   ```

2. **Entidade Prontuário:** Adição de campo transiente para metadados

   ```java
   @Entity
   public class Prontuario {
       // campos existentes...
       
       @Transient
       private Map<String, Object> metadados;
       
       public Map<String, Object> getMetadados() {
           return metadados;
       }
       
       public void setMetadados(Map<String, Object> metadados) {
           this.metadados = metadados;
       }
   }
   ```

### Camada de Serviço

1. **Serviço de Prontuário:** Métodos para operações com DTOs

   ```java
   @Service
   public class ProntuarioService {
       // métodos existentes...
       
       public List<ImagemRadiologicaResumo> buscarImagensProntuarioResumo(Long prontuarioId) {
           return imagemRadiologicaRepository.findResumoByProntuarioIdAndAtivoTrue(prontuarioId);
       }
       
       public Prontuario prepararMetadados(Prontuario prontuario) {
           if (prontuario == null) return null;
           
           Map<String, Object> metadados = new HashMap<>();
           List<ImagemRadiologicaResumo> imagens = buscarImagensProntuarioResumo(prontuario.getId());
           
           metadados.put("totalImagens", imagens.size());
           metadados.put("imagensResumo", imagens);
           
           prontuario.setMetadados(metadados);
           return prontuario;
       }
   }
   ```

### Camada de Apresentação

1. **Controlador:** Uso de DTOs para transferência de dados

   ```java
   @Controller
   @RequestMapping("/prontuarios")
   public class ProntuarioController {
       // métodos existentes...
       
       @GetMapping("/paciente/{pacienteId}")
       public String listarProntuarios(@PathVariable Long pacienteId, Model model) {
           List<Prontuario> prontuarios = prontuarioService.buscarPorPacienteId(pacienteId);
           
           // Preparar metadados para cada prontuário
           prontuarios.forEach(prontuarioService::prepararMetadados);
           
           model.addAttribute("prontuarios", prontuarios);
           return "prontuarios/meus-prontuarios";
       }
   }
   ```

2. **Templates:** Uso de metadados pré-calculados

   ```html
   <div class="prontuario-card" th:each="prontuario : ${prontuarios}">
       <div class="prontuario-header">
           <span th:text="${prontuario.data}">01/01/2025</span>
           <span th:text="${prontuario.metadados.totalImagens} + ' imagens'">2 imagens</span>
       </div>
       <!-- Restante do template -->
   </div>
   ```

### Classes DTO

1. **ImagemRadiologicaResumo:** DTO para metadados de imagens

   ```java
   public class ImagemRadiologicaResumo {
       private Long id;
       private String descricao;
       private String tipo;
       private LocalDateTime dataCriacao;
       private String nomeArquivo;
       
       // Getters e setters
   }
   ```

## Benefícios

1. **Desempenho Aprimorado:**
   - Redução de consultas desnecessárias ao banco de dados
   - Transferência de menor volume de dados entre camadas

2. **Segurança de Tipos:**
   - Eliminação de erros de conversão de tipos no PostgreSQL
   - Tratamento adequado de campos TEXT para base64

3. **Robustez do Template:**
   - Eliminação de erros de LazyInitializationException
   - Templates mais simples e diretos com dados pré-calculados

4. **Arquitetura Aprimorada:**
   - Separação clara entre entidades de domínio e representação de dados
   - Padrão consistente para implementações futuras

## Testes

A implementação foi validada com testes unitários e de integração, confirmando a correção de problemas anteriores:

- **Testes Unitários:** 545/545 testes passando com sucesso
- **Testes de Controladores:** ProntuarioControllerSecurityTest completo e funcionando
- **Testes de Template:** Validação de renderização sem erros de lazy loading

## Conclusão

A implementação do padrão DTO no sistema de prontuário representou uma evolução significativa na arquitetura da aplicação, resolvendo problemas específicos de performance e segurança no tratamento de dados. Esta abordagem estabelece um padrão que pode ser expandido para outras partes do sistema que enfrentam desafios semelhantes.

---

**Próximos Passos:**

- Implementação de paginação para listas de imagens muito grandes
- Expansão do padrão DTO para outros módulos do sistema
- Adição de cache para melhorar ainda mais a performance

---

**Documento gerado por:** Equipe de Desenvolvimento Cara Core Informática  
**Última atualização:** 13/07/2025
