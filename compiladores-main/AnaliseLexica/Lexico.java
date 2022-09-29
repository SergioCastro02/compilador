package AnaliseLexica;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Lexico {
    private char[] conteudo;
    private int indiceConteudo;
    
    public Lexico(String caminhoCodigoFonte){
        try {
            String conteudoStr;
            conteudoStr = new String(Files.readAllBytes(Paths.get(caminhoCodigoFonte)));
            this.conteudo = conteudoStr.toCharArray();
            this.indiceConteudo = 0;                        
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }
    
    //Retorna próximo char
    private char nextChar(){
        return this.conteudo[this.indiceConteudo++];
    }
    
    //Verifica existe próximo char ou chegou ao final do código fonte
    private boolean hasNextChar(){
        return indiceConteudo < this.conteudo.length;
    }
    
    //Retrocede o índice que aponta para o "char da vez" em uma unidade
    private void back(){
        this.indiceConteudo--;
    }
    
    //Identificar se char é letra minúscula    
    private boolean isLetra(char c){
        return (c >= 'a') && (c <= 'z');
    }
    
    //Identificar se char é dígito
    private boolean isDigito(char c){
        return (c >= '0') && (c <= '9');
    }
    
    //Método retorna próximo token válido ou retorna mensagem de erro.
    public Token nextToken(){
        Token token = null;
        char c;
        int estado = 0;
        
        StringBuffer lexema = new StringBuffer();
        while(this.hasNextChar()){
            c = this.nextChar();            
            switch(estado){
                case 0:
                    if(c == ' ' || c == '\t' || c == '\n'){ //caracteres de espaço em branco ASCII tradicionais 
                        estado = 0;
                    }
                    else if(this.isDigito(c)){
                        lexema.append(c);
                        estado = 1;
                    }
                    else if(c == '\"' ||  c == '\''){
                        lexema.append(c);
                        estado = 4;
                    }
                    else if(c == '<'){
                        lexema.append(c);
                        estado = 7;
                    }
                    else if(c == '>'){
                        lexema.append(c);
                        estado = 10;
                    }
                    else if(c == '='){
                        lexema.append(c);
                        estado = 12;
                    }
                    else if(c == ')' || 
                            c == '(' ||
                            c == '{' ||
                            c == '}' ||
                            c == ',' ||
                            c == ';'){
                        lexema.append(c);
                        estado = 14;
                    }
                    else if("int".contentEquals(lexema.toString()) ||
                            "float".contentEquals(lexema.toString()) ||
                            "char".contentEquals(lexema.toString()) ||
                            "while".contentEquals(lexema.toString()) ||
                            "if".contentEquals(lexema.toString()) ||
                            "else".contentEquals(lexema.toString()) ||
                            "main".contentEquals(lexema.toString())){
                        lexema.append(c);
                        estado = 15;        
                    }
                    else if(c == '+' || c == '-' || c == '*' || c == '/' || c == '%'){
                        lexema.append(c);
                        estado = 16;
                    }
                    else if(this.isLetra(c)){
                        lexema.append(c);
                        estado = 17;
                    }
                    else if(c == '$'){
                        lexema.append(c);
                        estado = 99;
                        this.back();
                    }else{
                        lexema.append(c);
                        throw new RuntimeException("Erro: token inválido \"" + lexema.toString() + "\"");
                    }
                    break;
                case 1:
                    if(this.isDigito(c)){
                        lexema.append(c);
                        estado = 1;                        
                    }else if (c == '.'){
                        lexema.append(c);
                        estado = 2;                        
                    }else{
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_INTEIRO);
                    }
                    break;
                case 2:
                    if(this.isDigito(c)){
                        lexema.append(c);
                        estado = 3;
                    }else{
                        throw new RuntimeException("Erro: número float inválido \"" + lexema.toString() + "\"");
                    }
                    break;
                case 3:
                    if(this.isDigito(c)){
                        lexema.append(c);
                        estado = 3;
                    }else{
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_REAL);
                    }
                    break;
                    case 4://char
                    if(this.isDigito(c) || this.isLetra(c)){
                        lexema.append(c);
                        estado = 5;
                    }
                    else{
                        throw new RuntimeException("Erro: char mal formatado \"" + lexema.toString() + "\"");
                    }
                    break;
                case 5:
                    if(c == '\"' || c == '\''){
                        lexema.append(c);
                        estado = 6;
                    }else{
                        lexema.append(c);
                        throw new RuntimeException("Erro: char mal formatado \"" + lexema.toString() + "\"");
                    }
                case 6:             
                    return new Token(lexema.toString(), Token.TIPO_CHAR); 
                case 7:
                    if(c == '='){
                        lexema.append(c);
                        estado = 8;
                    }
                    else if(c == '>'){
                        lexema.append(c);
                        estado = 9;
            
                    }else{
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                    }
                    break;
                case 8:
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 9:
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 10:
                    if(c == '='){
                        lexema.append(c);
                        estado = 11;
                    }
                    else{
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                    }
                    break; 
                case 11:
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 12:
                    if(c == '='){
                        lexema.append(c);
                        estado = 13; // É 13
                    }else{
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_ATRIBUICAO);
                    }
                case 13: // É 13!
                       this.back();
                       return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 14:
                       this.back();
                       return new Token(lexema.toString(), Token.TIPO_CARACTER_ESPECIAL);
                case 15://palavra reservada
                       this.back();
                       return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
                case 16:
                       this.back();
                       return new Token(lexema.toString(), Token.TIPO_OPERADOR_ARITMETICO);
                    
                case 17://identificador
                    if(this.isLetra(c) || this.isDigito(c)){
                        lexema.append(c);
                        estado = 17;
                    }else{
                       this.back();
                       return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
                    }
                    break;
                case 99:
                    return new Token(lexema.toString(), Token.TIPO_FIM_CODIGO); 
            }
        }                
        return token;
    }   
}