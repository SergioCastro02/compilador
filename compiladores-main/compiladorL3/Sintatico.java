package compiladorL3;

import java.util.LinkedList;

/*
 * @author aluno
 */
public class Sintatico {
    private final Lexico lexico;
    private Token token;
    private final LinkedList<Token> vetorSemantico = new LinkedList<>();

    public Sintatico(Lexico lexico) {
        this.lexico = lexico;
        this.token = token;
    }

    // Estado Inicial
    public void S() {
        this.token = lexico.nextToken();
        if (!token.getLexema().equals("int")) {
            throw new RuntimeException("Faltou implementar o main");
        }
        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals("main")) {
            throw new RuntimeException("Faltou implementar o main");
        }
        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals("(")) {
            throw new RuntimeException("Esqueceu de abrir ( ");
        }
        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals(")")) {
            throw new RuntimeException("Esqueceu de fechar ) ");
        }
        this.token = this.lexico.nextToken();
        this.B();
        if (this.token.getTipo() == Token.TIPO_FIM_CODIGO) {
            System.out.println("Compilação Feita!");
        } else {
            throw new RuntimeException("Erro próximo do fim do programa.");
        }
    }

    // Bloco/case
    private void B() {
        if (!this.token.getLexema().equals("{")) {
            throw new RuntimeException(
                    "Erro em bloco, era esperado " + "{" + " / próximo de  " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        while (!this.token.getLexema().equals("}") && this.token.getTipo() != Token.TIPO_FIM_CODIGO) {
            if (this.token.getLexema().equals("int")
                    || this.token.getLexema().equals("float")
                    || this.token.getLexema().equals("char"))
                DECLARACAO();
            CS();
        }
        if (!this.token.getLexema().equals("}")) {
            throw new RuntimeException("Faltou fechar " + "}" + " / próximo de " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
    }

    // Comando
    private void CS() {
        if (this.token.getLexema().equals("int")
                || this.token.getLexema().equals("float") || this.token.getLexema().equals("char")) {
            this.DECLARACAO();
            this.CS();
        } else if ((this.token.getTipo() == Token.TIPO_IDENTIFICADOR || this.token.getLexema().equals("{"))) {
            CB();
        } else if (this.token.getLexema().equals("while")) {
            iteração();
        } else if (this.token.getLexema().equals("if")) {
            condicional();
        } else if (this.token.getLexema().equals("}")) {
        } else {
            throw new RuntimeException(
                    "Erro no comando, era esperado algum comando ou identificador , mas achou "
                            + this.token.toString());
        }

    }

    // Comando Basico
    private void CB() {
        if (this.token.getTipo() == Token.TIPO_IDENTIFICADOR) {
            this.ATRIBUICAO();
        } else if (this.token.getLexema().equals("{")) {
            B();
        } else {
            throw new RuntimeException(
                    "Erro no comando basico, era esperado o '{' , mas achou " + this.token.getLexema());
        }
    }

    // Declaraçao
    private void DECLARACAO() {
        int tipo = 0;

        if (this.token.getLexema().equals("int"))
            tipo = Token.TIPO_INTEIRO;
        else if (this.token.getLexema().equals("float"))
            tipo = Token.TIPO_REAL;
        else if (this.token.getLexema().equals("char"))
            tipo = Token.TIPO_CHAR;
        else {
            throw new RuntimeException(
                    "Erro na declaração de variavel. " + " Erro próximo de: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();

        if (this.token.getTipo() != Token.TIPO_IDENTIFICADOR) {
            throw new RuntimeException(
                    "Erro na declaração de variavel. " + " Erro próximo de: " + this.token.getLexema());
        }

        String auxToken = this.token.getLexema();
        boolean isDeclared = false;
        for (Token token : vetorSemantico) {
            if (token.getLexema().equals(auxToken)) {
                isDeclared = true;
                break;
            }
        }
        if (isDeclared) {
            throw new RuntimeException("Identificador '" + auxToken + "' ja declarada");
        }
        vetorSemantico.push(new Token(auxToken, tipo));

        this.token = this.lexico.nextToken();
        if (!this.token.getLexema().equalsIgnoreCase(";")) {
            throw new RuntimeException(
                    "Erro na declaração de variavel. " + " Erro próximo de: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();

    }

    // Atribuição de valores para as variáveis
    private void ATRIBUICAO() {

        if (this.token.getTipo() != Token.TIPO_IDENTIFICADOR) {
            throw new RuntimeException(
                    "Erro de atribuição! Era esperado um identificador  mas  o Lexema gerado foi: "
                            + this.token.toString());
        }

        String auxToken = this.token.getLexema();
        boolean isDeclared = false;
        for (Token token : vetorSemantico) {
            if (token.getLexema().equals(auxToken)) {
                isDeclared = true;
                break;
            }
        }
        if (!isDeclared) {
            throw new RuntimeException("Identificador '" + auxToken + "' não declarada");
        }

        this.token = this.lexico.nextToken();
        if (this.token.getTipo() != Token.TIPO_OPERADOR_ATRIBUICAO) {
            throw new RuntimeException(
                    "Erro de atribuição! Era espearado '='  mas o Lexema gerado foi: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        expressão();
        if (!this.token.getLexema().equals(";")) {
            throw new RuntimeException(
                    "Erro de atribuição! Era espearado ';'  mas o Lexema gerado foi: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
    }

    private void iteração() {
        this.token = lexico.nextToken();
        if (!this.token.getLexema().equals("(")) {
            throw new RuntimeException(
                    "Erro em iteração! Era Esperado o '(' mas o Lexema gerado foi: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        relacional();
        if (!this.token.getLexema().equals(")")) {
            throw new RuntimeException(
                    "Erro em iteração! Era Esperado o ')' mas o Lexema gerado foi: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        B();
    }

    private void relacional() {
        expressão();
        if (token.getTipo() != Token.TIPO_OPERADOR_RELACIONAL) {
            throw new RuntimeException("Esperado 'Operador Relacional' mas foi: " + this.token.toString());
        }
        this.token = lexico.nextToken();
        expressão();
    }

    private void condicional() {
        this.token = lexico.nextToken();
        if (!this.token.getLexema().equals("(")) {
            throw new RuntimeException(
                    "Erro em condicional! Era Esperado o '(' mas o Lexema gerado foi: " + this.token.getLexema());
        }
        this.token = lexico.nextToken();
        relacional();
        if (!this.token.getLexema().equals(")")) {
            throw new RuntimeException(
                    "Erro em condicional! Era Esperado o ')' mas o Lexema gerado foi: " + this.token.getLexema());
        }
        this.token = lexico.nextToken();
        B();
        if (this.token.getLexema().equals("else")) {
            this.token = this.lexico.nextToken();
            B();
            return;
        }

    }

    private void expressão() {
        termo();
        if (token.getLexema().equals("+") || token.getLexema().equals("-")) {
            this.token = lexico.nextToken();
            expressão();
        }
    }

    private void termo() {
        fator();
        if (token.getLexema().equals("/") || token.getLexema().equals("*")) {
            this.token = lexico.nextToken();
            termo();
        }
    }

    private void fator() {
        if (this.token.getLexema().equals("(")) {
            this.token = lexico.nextToken();
            expressão();
            this.token = this.lexico.nextToken();
            if (!this.token.getLexema().equals(")")) {
                throw new RuntimeException("Erro em fator! Era Era Esperado o ')' mas: " + token.getLexema());
            }
            return;
        }
        if (token.getTipo() != Token.TIPO_IDENTIFICADOR &&
                token.getTipo() != Token.TIPO_REAL &&
                token.getTipo() != Token.TIPO_INTEIRO &&
                token.getTipo() != Token.TIPO_CHAR) {
            throw new RuntimeException(
                    "Erro em fator! Era esperado tipo 'CHAR', 'IDENTIFICADOR', 'INTEGER' ou 'REAL' mas foi: "
                            + this.token.toString());
        } else
            this.token = lexico.nextToken();
    }

}