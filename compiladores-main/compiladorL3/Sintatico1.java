package compiladorL3;
/*
 * @author luizcrisanto
 */
public class Sintatico1 {
    private Lexico lexico;
    private Token token;
    
    public Sintatico1(Lexico lexico){
        this.lexico=lexico;
    }
    
    public void S(){
        this.token=this.lexico.nextToken();
        this.E();
        if(this.token.getTipo() == Token.TIPO_FIM_CODIGO){
            System.out.println("Compilação efetuada! @Equipe Devs ");
        }else{
             throw new RuntimeException("Erro! Era para ser um identificador " + "ou numero proximo de "+ this.token.getLexema());
        }
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
            //OLHA AQUI
        }     
    }
    
    private void T(){
        if(this.token.getTipo() == Token.TIPO_IDENTIFICADOR || this.token.getTipo() == Token.TIPO_INTEIRO || this.token.getTipo() == Token.TIPO_REAL){
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
