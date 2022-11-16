package compiladorL3;

public class CompiladorL3 {

    public static void main(String[] args) {
        // TODO code application logic here
        Lexico lexico = new Lexico("/workspace/compilador/compiladores-main/compiladorL3/codigo.txt");
        Token t = null;
        while((t = lexico.nextToken()) != null){
            System.out.println(t.toString());
        }

    }
    
}
