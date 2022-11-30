package compiladorL3;
/*
 * @author aluno
 */
public class Sintatico {
    private Lexico lexico;
    private Token token;
    
    public Sintatico(Lexico lexico){
        this.lexico= lexico;
    }
    
    //Estado Inicial
    public void S(){
        this.token= this.lexico.nextToken();
        if(!token.getLexema().equals("int")){
            throw new RuntimeException("Faltou implementar o main");
        }
        this.token=this.lexico.nextToken();
        if(!token.getLexema().equals("main")){
            throw new RuntimeException("Faltou implementar o main");
        }
        this.token=this.lexico.nextToken();
        if(!token.getLexema().equals("(")){
            throw new RuntimeException("Esqueceu de abrir ( ");
        }
        this.token= this.lexico.nextToken();
        if(!token.getLexema().equals(")")){
            throw new RuntimeException("Esqueceu de fechar ) ");
        }
        this.token= this.lexico.nextToken();
        this.B();
        if(this.token.getTipo() == Token.TIPO_FIM_CODIGO){
            System.out.println("Compilação Feita!");
        }else{
            throw new RuntimeException("Erro próximo do fim do programa.");
        }
    }
    
    //Bloco/case
    private void B(){
        if(!this.token.getLexema().equals("{")){
            throw new RuntimeException("Erro, era esperado "+"{"+" / próximo de  "+this.token.getLexema());
        }
        this.token= this.lexico.nextToken();
        this.CS();
        if(!this.token.getLexema().equals("}")){
            throw new RuntimeException("Faltou fechar "+"}"+ " / próximo de "+this.token.getLexema());
        }
        this.token= this.lexico.nextToken();
    }
    
    //Comando
    private void CS(){
        if((this.token.getTipo() == Token.TIPO_IDENTIFICADOR) || this.token.getLexema().equals("int") || this.token.getLexema().equals("float") || this.token.getLexema().equals("char")){
            this.C();
            this.CS();
        }else{
            
        }
    }
    
    //Comando
    private void C(){
        if(this.token.getTipo() == Token.TIPO_IDENTIFICADOR){
            this.ATRIBUICAO();
        }else if(this.token.getLexema().equals("int") || this.token.getLexema().equals("float") || this.token.getLexema().equals("char")){
            this.DECLARACAO();
        }else{
            throw new RuntimeException("Erro, era esperado declarar um comando próximo de: "+this.token.getLexema());
        }
    }
    
    //Declaraçao
    private void DECLARACAO(){
            if(!(this.token.getLexema().equals("int") || this.token.getLexema().equals("float") || this.token.getLexema().equals("char"))){
                throw new RuntimeException("Erro na declaração de variavel. "+" Erro próximo de: "+this.token.getLexema());
            }
            this.token= this.lexico.nextToken();
            if(this.token.getTipo() != Token.TIPO_IDENTIFICADOR){
                 throw new RuntimeException("Erro na declaração de variavel. "+" Erro próximo de: "+this.token.getLexema());
            }
            this.token = this.lexico.nextToken();
            if(!this.token.getLexema().equalsIgnoreCase(";")){
                 throw new RuntimeException("Erro na declaração de variavel. "+" Erro próximo de: "+this.token.getLexema());
            }
        this.token = this.lexico.nextToken();
    }
    
    //Atribuição de valores para as variáveis
    private void ATRIBUICAO(){
        if(this.token.getTipo() != Token.TIPO_IDENTIFICADOR){
            throw new RuntimeException("Erro de atribuição! Próximo de: "+this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        if(this.token.getTipo() != Token.TIPO_OPERADOR_ATRIBUICAO){
            throw new RuntimeException("Erro de atribuição! Próximo de: "+this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        this.E();
        if(!this.token.getLexema().equals(";")){
            throw new RuntimeException("Erro de atribuição! Próximo de: "+this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
    }
      
    private void E(){
        this.T();
        this.El();
    }
    
    private void El(){
        if(this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO){
            this.OP();
            this.T();
            this.El();
        }else{

        }     
    }
    
    private void T(){
        if(this.token.getTipo() == Token.TIPO_IDENTIFICADOR || this.token.getTipo() == Token.TIPO_INTEIRO || this.token.getTipo() == Token.TIPO_REAL || this.token.getTipo() == Token.TIPO_CHAR){
            this.token=this.lexico.nextToken();
        }else{
            throw new RuntimeException("Erro! Era para ser um identificador " + "ou numero proximo de "+ this.token.getLexema());
        }        
    }
    
    private void OP(){
        if(this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO){
            this.token=this.lexico.nextToken();
        }else{
            throw new RuntimeException("Erro! Era para ser um operador" + " aritmetico(+/-/*/%/) proximo de "+ this.token.getLexema());
        }      
    }
}