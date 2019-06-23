/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho2;

/**
 *
 * @author daniellucredio
 */
public class LuazinhaSemanticAnalyzer extends LuazinhaBaseVisitor<Void> {

    // Não esqueça de colocar os RAs do seu grupo na variável a seguir    
    public static String grupo = "726549, 619884, 619680";

    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();

    @Override
    public Void visitPrograma(LuazinhaParser.ProgramaContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));

        // A chamada a seguir invoca o comportamento padrão,
        // que é o de visitar todos os filhos
        super.visitPrograma(ctx);
        // Também poderia substituir por uma chamada específica a
        // outro visitante, como a seguir:
        //visitTrecho(ctx.trecho());
        // Neste caso, é preciso especificar o contexto específico
        // do visitante (trecho)

        // Cuidado para lembrar de inserir corretamente as chamadas
        // dos visitantes a seguir, pois no padrão Visitor do ANTLR,
        // a visitação deve ser explicitamente controlada pelo programador.
        pilhaDeTabelas.desempilhar();

        // Deve haver "return null" no final de cada método, devido
        // à verificação de tipos genéricos do Java. Como não estamos
        // utilizando tipo de retorno, não é necessário.
        return null;
    }

    // Não é necessário sobrescrever um determinado método visitante
    // se a única coisa que ele faz é visitar os filhos, sem nenhuma
    // ação adicional. O exemplo a seguir serve apenas para ilustrar
    // esse efeito.
//    @Override
    public Void visitComandoAtribuicao(LuazinhaParser.ComandoAtribuicaoContext ctx) {
        visitChildren(ctx.listaexp());
        int tam = ctx.listavar().nomes.size();
        for (int nome = 0; nome < tam; nome++) { // loop para cada nome
            if (pilhaDeTabelas.existeSimbolo(ctx.listavar().nomes.get(nome)) == false) { // se o simbolo não existir ainda
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.listavar().nomes.get(nome), "variavel"); // adicionar simbolo novo na tabela
            }
        }
        return null;
    }

    public Void visitNomedafuncao(LuazinhaParser.NomedafuncaoContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.getText())); // cria uma nova tabela de simbolos para a função que vamos entrar
        visitChildren(ctx);
        return null;
    }

    public Void visitCorpodafuncao(LuazinhaParser.CorpodafuncaoContext ctx) {
        visitChildren(ctx);
        pilhaDeTabelas.desempilhar(); // depois de visitar a função, removemos a tabela de simbolos
        return null;
    }

    public Void visitComandoWhile(LuazinhaParser.ComandoWhileContext ctx) {
        return visitChildren(ctx);
    }

    public Void visitComandoIf(LuazinhaParser.ComandoIfContext ctx) {
        return visitChildren(ctx);
    }

    public Void visitComandoFor1(LuazinhaParser.ComandoFor1Context ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("for")); // nova tabela de simbolos para o for que entramos
        pilhaDeTabelas.topo().adicionarSimbolo(ctx.NOME().getText(), "variavel"); // adiciona a variavel definida dentro do for (contador por exemplo)
        visitChildren(ctx); // visita o conteudo do for
        pilhaDeTabelas.desempilhar(); // desempilha o for
        return null;
    }

    public Void visitComandoFor2(LuazinhaParser.ComandoFor2Context ctx) {
        visitChildren(ctx.listaexp()); // visita as expressões para ver se não estamos usando uma variavel à direita antes de defini-la

        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("for")); // nova tabela de simbolos para o for que entramos
        for (String elemento : ctx.listadenomes().nomes) { // para cara variavel definida dentro dos parenteses do for, adicionar para lista
            pilhaDeTabelas.topo().adicionarSimbolo(elemento, "variavel");

        }
        visitChildren(ctx.bloco()); // visita conteudo do for

        pilhaDeTabelas.desempilhar(); // desempilha tabela do for
        return null;
    }

    public Void visitComandoLocalAtribuicao(LuazinhaParser.ComandoLocalAtribuicaoContext ctx) {
        visitChildren(ctx); // visita as expressões para ver se não estamos usando uma variavel à direita antes de defini-la 
        for (String nome : ctx.listadenomes().nomes) { // para cada variavel à esquerda, adiciona na tabela
            pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel");
        }

        return null;

    }

    public Void visitComandoRepeat(LuazinhaParser.ComandoRepeatContext ctx) {
        // pilhaDeTabelas.empilhar(new TabelaDeSimbolos("repeat"));
        visitChildren(ctx);
        //pilhaDeTabelas.desempilhar();
        return null;
    }

    public Void visitExpPrefixo2Var(LuazinhaParser.ExpPrefixo2VarContext ctx) {
        if (!pilhaDeTabelas.existeSimbolo(ctx.var().nome)) { // verifica se a variavel à direita de uma atribuição existe ou nao
            Mensagens.erroVariavelNaoExiste(ctx.var().linha, ctx.var().coluna, ctx.var().nome); // caso não exista, da erro

        }

        return visitChildren(ctx);
    }

    public Void visitListavar(LuazinhaParser.ListavarContext ctx) {
        for (String nome : ctx.nomes) {
            if (!pilhaDeTabelas.existeSimbolo(nome)) { // adiciona variaveis à esquerda de uma atribuição em lista para a tabela (caso ja nao esteja)
                pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel");
            }
        }
        return null;

    }

    public Void visitListaParListaDeNomes(LuazinhaParser.ListaParListaDeNomesContext ctx) {
        for (String nome : ctx.listadenomes().nomes) {
            pilhaDeTabelas.topo().adicionarSimbolo(nome, "parametro"); // adiciona todos simbolos de uma lista de parametros na pilha
        }
        return visitChildren(ctx);
    }

    public Void visitComandoFunction(LuazinhaParser.ComandoFunctionContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.nomedafuncao().nome)); // nova pilha para a funcao
        if (ctx.nomedafuncao().metodo) { // se for funcao/metodo
            pilhaDeTabelas.topo().adicionarSimbolo("self", "parametro"); // adiciona self como parametro
        }
        visitChildren(ctx.corpodafuncao()); // visita conteudo da funcao
        pilhaDeTabelas.desempilhar(); // desempilha tabela da funcao
        return null;
    }
}
