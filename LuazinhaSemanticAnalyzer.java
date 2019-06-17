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
    public static String grupo = "<Coloque os RAs do seu grupo aqui>"; 

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
    public Void visitBloco(LuazinhaParser.BlocoContext ctx) {
          //pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.parent.parent.getChild(1).getText()));
          visitTrecho(ctx.trecho());
          pilhaDeTabelas.desempilhar();
          
          return null;
    }
    
    public Void visitComandoAtribuicao(LuazinhaParser.ComandoAtribuicaoContext ctx) {
        int tam = ctx.listavar().nomes.size();
        for (int nome=0; nome< tam; nome++){
            if (pilhaDeTabelas.existeSimbolo(ctx.listavar().nomes.get(nome))== false)
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.listavar().nomes.get(nome),"variavel");
        }
        return null;
    }
    
    public Void visitNomedafuncao(LuazinhaParser.NomedafuncaoContext ctx){
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.getText()));
        return null;
    }
    
    public Void visitComandoWhile(LuazinhaParser.ComandoWhileContext ctx){
        return null;
    }
    
    public Void visitComandoIf(LuazinhaParser.ComandoIfContext ctx){
        return null;
    }
        
    public Void visitComandoFor1(LuazinhaParser.ComandoFor1Context ctx){
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("for"));
        pilhaDeTabelas.topo().adicionarSimbolo(ctx.NOME().getText(),"variavel");
        
        pilhaDeTabelas.desempilhar();
        return null;
    }
    
    public Void visitComandoLocalAtribuicao(LuazinhaParser.ComandoLocalAtribuicaoContext ctx){
        pilhaDeTabelas.topo().adicionarSimbolo(ctx.listadenomes().getText(),"variavel");
        return null;
    }
    
    public Void visitListadenomes(LuazinhaParser.ListadenomesContext ctx){
        int tam = ctx.nomes.size();
        for (int nome=0; nome< tam; nome++){
            pilhaDeTabelas.topo().adicionarSimbolo(ctx.nomes.get(nome),"parametro");
        }
        return null;
            
    }
    

    

}

    
    