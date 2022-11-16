package compiladorL3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tarci
 */
public class Lexico {
    private char[] conteudo;
    private int indiceConteudo;

    public Lexico(String caminhoCodigoFonte) {
        try {
            String conteudoStr;
            conteudoStr = new String(Files.readAllBytes(Paths.get(caminhoCodigoFonte)));
            this.conteudo = conteudoStr.toCharArray();
            this.indiceConteudo = 0;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Retorna próximo char
    private char nextChar() {
        return this.conteudo[this.indiceConteudo++];
    }

    // Verifica existe próximo char ou chegou ao final do código fonte
    private boolean hasNextChar() {
        return indiceConteudo < this.conteudo.length;
    }

    // Retrocede o índice que aponta para o "char da vez" em uma unidade
    private void back() {
        this.indiceConteudo--;
    }

    // Identificar se char é letra minúscula
    private boolean isLetra(char c) {
        return (c >= 'a') && (c <= 'z');
    }

    // Identificar se char é dígito
    private boolean isDigito(char c) {
        return (c >= '0') && (c <= '9');
    }

    private boolean operadorRelacional(char c) {
        return (c == '>' || c == '<');
    }

    public boolean operadorAtribuicao(char c) {
        return (c == '=');
    }

    public boolean operadorAritmetico(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c == '%');
    }

    // Método retorna próximo token válido ou retorna mensagem de erro.
    public Token nextToken() {
        Token token = null;
        char c;
        int estado = 0;
        StringBuffer lexema = new StringBuffer();
        while (this.hasNextChar()) {
            c = this.nextChar();
            switch (estado) {

                case 0:
                    if (c == ' ' || c == '\t' || c == '\n' || c == '\r') { // caracteres de espaço em branco ASCII
                                                                           // tradicionais
                        estado = 0;
                    } else if (this.isLetra(c) || c == '_') {
                        lexema.append(c);
                        estado = 1;
                    } else if (this.isDigito(c)) {
                        lexema.append(c);
                        estado = 2;
                    } else if (c == '\'' || c == '\"') {
                        this.back();
                        estado = 10;

                    } else if (c == ')' ||
                            c == '(' ||
                            c == '{' ||
                            c == '}' ||
                            c == ',' ||
                            c == ';') {
                        lexema.append(c);
                        estado = 5;

                    } else if (c == '<') {
                        lexema.append(c);
                        estado = 14;

                    } else if(c == '>'){
                        lexema.append(c);
                        estado = 17;
                    
                    }else if (c == '=') {
                        lexema.append(c);
                        estado = 7;

                    } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
                        lexema.append(c);
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_ARITMETICO);

                    } else if (c == '$') {
                        lexema.append(c);
                        estado = 99;
                        this.back();
                    } else {
                        lexema.append(c);
                        throw new RuntimeException("Erro: token inválido \"" + lexema.toString() + "\"");
                    }
                    break;
                case 1:
                    if (this.isLetra(c) || this.isDigito(c) || c == '_') {
                        lexema.append(c);
                        estado = 13;
                    } else {
                        if ("if".contentEquals(lexema.toString()) ||
                                "main".contentEquals(lexema.toString()) ||
                                "else".contentEquals(lexema.toString()) ||
                                "while".contentEquals(lexema.toString()) ||
                                "do".contentEquals(lexema.toString()) ||
                                "for".contentEquals(lexema.toString()) ||
                                "int".contentEquals(lexema.toString()) ||
                                "float".contentEquals(lexema.toString()) ||
                                "char".contentEquals(lexema.toString())) {
                            this.back();
                            return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
                        }

                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
                    }
                    break;

                case 2:
                    if (this.isDigito(c)) {
                        lexema.append(c);
                        estado = 2;
                    } else if (c == '.') {
                        lexema.append(c);
                        estado = 3;
                    } else {
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_INTEIRO);
                    }
                    break;
                case 3:
                    if (this.isDigito(c)) {
                        lexema.append(c);
                        estado = 4;
                    } else {
                        throw new RuntimeException("Erro: número float inválido \"" + lexema.toString() + "\"");
                    }
                    break;
                case 4:
                    if (this.isDigito(c)) {
                        lexema.append(c);
                        estado = 4;
                    } else {
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_REAL);
                    }
                    break;
                case 5:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_CARACTER_ESPECIAL);

                case 6:
                    if (this.operadorRelacional(c)) {
                        lexema.append(c);
                        estado = 6;
                    } else {
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                    }
                    break;

                case 7:
                    if (this.operadorAtribuicao(c)) {
                        lexema.append(c);
                        estado = 6;
                    } else {
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_ATRIBUICAO);
                    }
                    break;

                case 9:
                    if (this.operadorAritmetico(c)) {
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_ARITMETICO);
                    }
                    break;

                case 10:
                    if (c == '\'' || c == '\"') {
                        lexema.append(c);
                        estado = 11;
                    } else {
                        throw new RuntimeException(
                                "Erro: sequência de caractere inválido \"" + lexema.toString() + "\"");
                    }
                    break;

                case 11:
                    if (this.isDigito(c) || this.isLetra(c)) {
                        lexema.append(c);
                        estado = 12;
                    } else {
                        throw new RuntimeException("Erro: sequência de Char inválida \"" + lexema.toString() + "\"");
                    }
                    break;
                case 12:
                    if (c == '\'' || c == '\"') {
                        lexema.append(c);
                        return new Token(lexema.toString(), Token.TIPO_CHAR);
                    } else {
                        throw new RuntimeException("Erro: sequência de Char inválida \"" + lexema.toString() + "\"");
                    }
                case 13:
                    if (this.isLetra(c) || this.isDigito(c) || c == '_') {
                        lexema.append(c);
                        estado = 13;
                    } else {
                        if ("if".contentEquals(lexema.toString()) ||
                                "main".contentEquals(lexema.toString()) ||
                                "else".contentEquals(lexema.toString()) ||
                                "while".contentEquals(lexema.toString()) ||
                                "do".contentEquals(lexema.toString()) ||
                                "for".contentEquals(lexema.toString()) ||
                                "int".contentEquals(lexema.toString()) ||
                                "float".contentEquals(lexema.toString()) ||
                                "char".contentEquals(lexema.toString())) {
                            this.back();
                            return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
                        }

                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
                    }
                    break;

                case 14:
                    if (c == '=') {
                        lexema.append(c);
                        estado = 15;
                    } else if (c == '>') {
                        lexema.append(c);
                        estado = 16;

                    } else {
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                    }
                    break;
                case 15:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 16:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 17:
                    if (c == '=') {
                        lexema.append(c);
                        estado = 18;
                    } else {
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                    }
                    break;
                case 18:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 99:
                    return new Token(lexema.toString(), Token.TIPO_FIM_CODIGO);
                    
            }
        }
        return token;
    }
}
