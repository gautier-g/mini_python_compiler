
import java.util.LinkedList;
import java.util.List;
import java.nio.channels.MulticastChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import ANALYSE.AnalyseException;
import ANALYSE.ConvertToken;
import AST.*;
import AST.SimpleStmt.Affect;
import AST.SimpleStmt.Print;
import AST.SimpleStmt.Return;
import AST.SimpleStmt.SimpleStmt;
import AST.SimpleStmt.Expr.AddBinop;
import AST.SimpleStmt.Expr.AddExpr;
import AST.SimpleStmt.Expr.AndExpr;
import AST.SimpleStmt.Expr.CompBinop;
import AST.SimpleStmt.Expr.CompExpr;
import AST.SimpleStmt.Expr.Expr;
import AST.SimpleStmt.Expr.MutBinop;
import AST.SimpleStmt.Expr.MutExpr;
import AST.SimpleStmt.Expr.NotExpr;
import AST.SimpleStmt.Expr.OrExpr;
import AST.SimpleStmt.Expr.TermExpr.Ident;
import AST.SimpleStmt.Expr.TermExpr.IdentP;
import AST.SimpleStmt.Expr.TermExpr.ListType;
import AST.SimpleStmt.Expr.TermExpr.Parenthese;
import AST.SimpleStmt.Expr.TermExpr.TermExpr;
import AST.SimpleStmt.Expr.TermExpr.Const.BoolType;
import AST.SimpleStmt.Expr.TermExpr.Const.Const;
import AST.SimpleStmt.Expr.TermExpr.Const.IntegerType;
import AST.SimpleStmt.Expr.TermExpr.Const.StringType;
import AST.Stmt.For;
import AST.Stmt.If;
import AST.Stmt.IfElse;
import AST.Stmt.Stmt;

public class ParserV2 {
    private Queue<Token> tokenQueue = new LinkedList<Token>();

    public ParserV2(Queue<Token> tokenQueue){
        this.tokenQueue = tokenQueue;
    }

    public void startAnalyse(){
        AnalyseFile();
    }
    
    private File AnalyseFile(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("NEWLINE", "def", "ident", "(", "return", "print", "[", "for", "in", "if", "not", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }

        Token currentToken = tokenQueue.peek();
        
        if (validetoken.contains(currentToken.getSymbole())){
            

            AnalyseOptNewline();
            
            File file = new File( AnalyseDefEtoile() , AnalyseStmtPlus() );

            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals("EOF")){
                System.out.println("MOT RECONNUE | AUCUN PROBLEME");
                return file;
            }

            
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
        return null;
    }

    private void AnalyseOptNewline(){
        LinkedList<String> validetoken = new LinkedList<>(Arrays.asList("def", "ident", "(", "return", "print", "[", "for","if", "not", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }

        Token currentToken = tokenQueue.peek();
        if (currentToken.getSymbole().equals("NEWLINE")){
            tokenQueue.poll();
        }
        else if (validetoken.contains(currentToken.getSymbole())){

        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private Def AnalyseDeft(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.poll();

        if (currentToken.getSymbole().equals("def")){
            Def def = new Def();

            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals("ident")){
                Ident ident = new Ident(currentToken.getValue()); // A revoir 

                currentToken = tokenQueue.poll();
                if(currentToken.getSymbole().equals("(")){


                    LinkedList<Ident> idents = AnalyseIdentEtoileVirgule();

                    currentToken = tokenQueue.poll();
                    if(currentToken.getSymbole().equals(")")){
                        
                        currentToken = tokenQueue.poll();
                        if(currentToken.getSymbole().equals(":")){
                            def.setIdent(ident);
                            def.setIdents(idents);
                            def.setSuite( AnalyseSuite() );

                            return def;
                        }
                        else{
                            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
                        }

                    }
                    else{
                        throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
                    }
                }
                else{
                    throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
                }
            }
            else{
                throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
            

        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private LinkedList<Def> AnalyseDefEtoile(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList( "ident", "(", "return", "print", "[", "for","if", "not", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("def")){
            Def def = AnalyseDeft();
            LinkedList<Def> defs = AnalyseDefEtoile();

            if (defs == null) {
                LinkedList<Def> newDefs = new LinkedList<Def>();
                newDefs.add(def);
                
                return newDefs;
            }
            else {
                defs.add(def);
                return defs;
            }


        }
        else if (validetoken.contains(currentToken.getSymbole())){
            return null;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
        
    }

    private LinkedList<Stmt> AnalyseStmtPlus(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList( "ident", "(", "return", "print", "[", "for","if", "not", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (validetoken.contains(currentToken.getSymbole())){
            Stmt stmt = AnalyseStmt();
            LinkedList<Stmt> stmts = AnalyseStmtPlusRest();

            if (stmts == null){
                LinkedList<Stmt> newStmts = new LinkedList<Stmt>();
                newStmts.add(stmt);
                return newStmts;
            }
            else {
                stmts.add(stmt);
                return stmts;
            }
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private LinkedList<Stmt> AnalyseStmtPlusRest(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList( "ident", "(", "return", "print", "[", "for","if", "not", "integer", "string", "True", "False", "None")) ;
        final LinkedList<String> validetoken1 = new LinkedList<>(Arrays.asList( "EOF","END")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();
        
        if (validetoken.contains(currentToken.getSymbole())){
            return AnalyseStmtPlus();
        }
        else if (validetoken1.contains(currentToken.getSymbole())){
            return null;
        }   
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private LinkedList<Ident> AnalyseIdentEtoileVirgule(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("ident")){
            return AnalyseIdentPlusVirgule();
        }
        else if (currentToken.getSymbole().equals(")")){
            return null;
        }
        else {
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }

    }

    private LinkedList<Ident> AnalyseIdentPlusVirgule(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("ident")){
            tokenQueue.poll();
            Ident ident = new Ident();
            LinkedList<Ident> idents = AnalyseIdentPlusVirguleRest();

            if (idents == null){
                LinkedList<Ident> newIdents = new LinkedList<>();
                newIdents.add(ident);
                return newIdents;
            }
            else {
                idents.add(ident);
                return idents;
            }
        }
        else {
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private LinkedList<Ident> AnalyseIdentPlusVirguleRest(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals(")")){
            return null;
        }
        else if (currentToken.getSymbole().equals(",")){
            tokenQueue.poll();
            return AnalyseIdentPlusVirgule();
        }
        else {
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private Suite AnalyseSuite(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("ident", "(", "return", "print", "[", "not", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();
        
        if (currentToken.getSymbole().equals("NEWLINE")){
            tokenQueue.poll();
            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals("BEGIN")){

                Suite suite = new Suite( AnalyseStmtPlus() ); 

                currentToken = tokenQueue.poll();
                if(currentToken.getSymbole().equals("END")){
                    return suite;
                }
                else{
                    throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
                }

            }
            else{
                throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
        }

        else if (validetoken.contains(currentToken.getSymbole())){


            SimpleStmt simpleStmt = AnalyseSimpleStmt();


            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals("NEWLINE")){
                return new Suite( Arrays.asList(simpleStmt) );
            }
            else{
                throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }

    }

    private SimpleStmt AnalyseSimpleStmt(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList( "ident", "(", "[", "not", "integer", "string", "True", "False", "None")) ;

        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("ident")){
            tokenQueue.poll();
            
            Ident ident = new Ident(currentToken.getValue());

            currentToken = tokenQueue.poll();
            if (currentToken.getSymbole().equals("=")){
                return new Affect( ident , AnalyseExpr() );
            }
            else {
                throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
        }
        else if(validetoken.contains(currentToken.getSymbole())){

            Expr expr = AnalyseOrExpr();
            AnalyseSimpleStmtFact();
            
        }
        else if(currentToken.getSymbole().equals("return")){
            
            tokenQueue.poll();
            return new Return(AnalyseExpr());
            
        }
        else if(currentToken.getSymbole().equals("print")){
            tokenQueue.poll();
            currentToken = tokenQueue.poll();

            if(currentToken.getSymbole().equals("(")){
                
                Print print = new Print(AnalyseExpr());

                currentToken = tokenQueue.poll();
                if(currentToken.getSymbole().equals(")")){
                    return print;
                }
                else{
                    throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
                }
            }
            else{
                throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
            
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }

    }

    private void AnalyseSimpleStmtFact(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList( "NEWLINE","=")) ;

        Token currentToken = tokenQueue.poll();

        if(validetoken.contains(currentToken.getSymbole())){
            AnalyseSimpleStmtFactFact();
        }
        else if(currentToken.getSymbole().equals("[")){
            tokenQueue.poll();


            AnalyseExpr();



            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals("]")){
                AnalyseExprCrochetEtoile();
            }
            else{
                throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
            
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private void AnalyseSimpleStmtFactFact(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("=")){
            tokenQueue.poll();

            AnalyseExpr();
        }
        else if (currentToken.getSymbole().equals("NEWLINE")){
            return ;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private Stmt AnalyseStmt(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList( "ident", "(","return","print", "[", "not", "integer", "string", "True", "False", "None")) ;
    
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (validetoken.contains(currentToken.getSymbole())){

            SimpleStmt simpleStmt = AnalyseSimpleStmt();


            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals("NEWLINE")){
                return simpleStmt;
            }
            else{
                throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
            
        }
        else if (currentToken.getSymbole().equals("for")){
            tokenQueue.poll();

            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals("ident")){

                Ident ident = new Ident(currentToken.getValue());
                currentToken = tokenQueue.poll();
                if(currentToken.getSymbole().equals("in")){
                    
                    Expr expr = AnalyseExpr();

                    currentToken = tokenQueue.poll();
                    if(currentToken.getSymbole().equals(":")){
                        
                        return new For( ident,expr, AnalyseSuite());

                    }
                    else{
                        throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
                    }
                }
                else{
                    throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
                }
                
            }
            else{
                 throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
        }
        else if (currentToken.getSymbole().equals("if")){
            tokenQueue.poll();

            Expr expr = AnalyseExpr();

            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals(":")){
                
                Suite suite = AnalyseSuite();
                Suite elseSuite = AnalyseStmtElse();

                if (elseSuite == null){
                    return new If(expr,suite);
                }
                else return new IfElse(expr,suite,elseSuite);
                
            }
            else{
                 throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
        }

        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private Suite AnalyseStmtElse(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList( "EOF","ident", "(","END", "return", "print", "[", "for","if", "not", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("else")){
            tokenQueue.poll();

            currentToken = tokenQueue.poll();

            if (currentToken.getSymbole().equals(":")){
                
                return AnalyseSuite();
            }
            else{
                throw new AnalyseException("Erreur non reconnue, ligne t: " + currentToken.getLine());
            }

        }
        else if (validetoken.contains(currentToken.getSymbole())){
            return null;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private Expr AnalyseExpr(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList( "ident", "(", "[",  "not", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (validetoken.contains(currentToken.getSymbole())){
            OrExpr orExpr = AnalyseOrExpr();
            LinkedList<Expr> exprs = AnalyseExprCrochetEtoile();

            if (exprs == null){
                return orExpr;
            }
            else {
                return null; // PB -> NOUVELLE CLASSE POUR L'AST ?????????????????
            }


        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private LinkedList<Expr> AnalyseExprCrochetEtoile(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("NEWLINE",")",":",",", "ident", "(", "]")) ;
        
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("[")){
            tokenQueue.poll();
            Expr expr = AnalyseExpr();

            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals("]")){
                LinkedList<Expr> exprs = AnalyseExprCrochetEtoile();
                if (exprs == null) {
                    LinkedList<Expr> newExprs = new LinkedList<>();
                    newExprs.addFirst(expr);
                    return newExprs;
                }
                else {
                    exprs.addFirst(expr);
                    return exprs;
                }
            }
            else{
                throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
        }
        else if (validetoken.contains(currentToken.getSymbole())){
            return null;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private OrExpr AnalyseOrExpr(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("ident", "(", "[", "not", "integer", "string", "True", "False", "None")) ;
        
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (validetoken.contains(currentToken.getSymbole())){
            AndExpr andExpr = AnalyseAndExpr();
            OrExpr orExpr = AnalyseOrExprRest();

            if (orExpr == null){
                return andExpr;
            }
            else {
                orExpr.addOrExpr(andExpr);
                return orExpr;
            }
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private OrExpr AnalyseOrExprRest(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("NEWLINE",")",":",",","=","[","]")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("or")){
            AnalyseBinopOr();
            return AnalyseOrExpr();
        }
        else if(validetoken.contains(currentToken.getSymbole())){
            return null;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private AndExpr AnalyseAndExpr(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("ident", "(", "[", "not", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (validetoken.contains(currentToken.getSymbole())){
            NotExpr notExpr = AnalyseNotExpr();
            AndExpr andExpr = AnalyseAndExprRest();

            if (andExpr == null){
                return notExpr;
            }
            else {
                andExpr.addOrExpr(notExpr);
                return andExpr;
            }
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private AndExpr AnalyseAndExprRest(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("or","NEWLINE",")",":",",","=","[","]")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("and")){
            AnalyseBinopAnd();
            return AnalyseAndExpr();
        }
        else if(validetoken.contains(currentToken.getSymbole())){
            return null;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private NotExpr AnalyseNotExpr(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("ident", "(", "[", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (validetoken.contains(currentToken.getSymbole())){
            return AnalyseCompExpr();
        }
        else if(currentToken.getSymbole().equals("not")){
            tokenQueue.poll();
            return new NotExpr(AnalyseNotExpr());
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private CompExpr AnalyseCompExpr(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("ident", "(", "[", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (validetoken.contains(currentToken.getSymbole())){
            AddExpr addExpr = AnalyseAddExpr();
            CompExpr compExpr = AnalyseCompExprRest();

            if (compExpr == null){
                return addExpr;
            }
            else {
                compExpr.setLeft(addExpr);
                return compExpr;
            }
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private CompExpr AnalyseCompExprRest(){
        final LinkedList<String> validetoken1 = new LinkedList<>(Arrays.asList("and","or","NEWLINE",")",":",",","=","[","]")) ;
        final LinkedList<String> validetoken2 = new LinkedList<>(Arrays.asList("<=",">=",">","<","!=","==")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (validetoken2.contains(currentToken.getSymbole())){
            return new CompExpr( AnalyseBinopComp() , AnalyseAddExpr());
        }
        else if(validetoken1.contains(currentToken.getSymbole())){
            return null;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private AddExpr AnalyseAddExpr(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("ident", "(", "[", "integer", "string", "True", "False", "None")) ;
        
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();
        
        if (validetoken.contains(currentToken.getSymbole())){
            MutExpr mutExpr = AnalyseMutExpr();
            AddExpr addExpr = AnalyseAddExprRest();

            if (addExpr == null){
                return mutExpr;
            }
            else {
                addExpr.setLeft(mutExpr);
                return addExpr;
            }

        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }

    }

    private AddExpr AnalyseAddExprRest(){
        final LinkedList<String> validetoken1 = new LinkedList<>(Arrays.asList("and","or","NEWLINE",")",":",",","=","[","]","<=",">=",">","<","!=","==")) ;
        final LinkedList<String> validetoken2 = new LinkedList<>(Arrays.asList("-","+")) ;
        
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();
        
        if (validetoken2.contains(currentToken.getSymbole())){
            return new AddExpr( AnalyseBinopAdd(), AnalyseAddExpr());
        }
        else if(validetoken1.contains(currentToken.getSymbole())){
            return null;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private MutExpr AnalyseMutExpr(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("ident", "(", "[", "integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();
        
        if (validetoken.contains(currentToken.getSymbole())){
            TermExpr termExpr = AnalyseTerminalExpr();
            MutExpr mutExpr = AnalyseMutExprRest();

            if (mutExpr == null){
                return termExpr;
            }
            else {
                mutExpr.setLeft(termExpr);
                return mutExpr;
            }
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private MutExpr AnalyseMutExprRest(){
        final LinkedList<String> validetoken1 = new LinkedList<>(Arrays.asList("and","or","NEWLINE",")",":",",","=","[","]","<=",">=",">","<","!=","==","+","-")) ;
        final LinkedList<String> validetoken2 = new LinkedList<>(Arrays.asList("*","//","%")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (validetoken2.contains(currentToken.getSymbole())){
            return new MutExpr( AnalyseBinopMut(),AnalyseMutExpr());
        }
        else if(validetoken1.contains(currentToken.getSymbole())){
            return null;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private TermExpr AnalyseTerminalExpr(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("integer", "string", "True", "False", "None")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("ident")){
            tokenQueue.poll();
            Ident ident = new Ident(currentToken.getValue());
            LinkedList<Expr> exprs = AnalyseExprRestIdent();

            if (exprs == null){
                return ident;
            }
            else return new IdentP(ident, exprs);
        }
        else if(currentToken.getSymbole().equals("(")){
            tokenQueue.poll();

            Expr expr = AnalyseExpr();

            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals(")")){
                return new Parenthese(expr);
            }
            else{
                throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
        }
        else if(currentToken.getSymbole().equals("[")){
            tokenQueue.poll();
            
            LinkedList<Expr> exprs = AnalyseExprEtoileVirgule();

            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals("]")){
                return new ListType(exprs);
            }
            else{
                throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
        }
        else if(validetoken.contains(currentToken.getSymbole())){
            return AnalyseConst();
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " +  currentToken.getSymbole());
        }
    }

    private LinkedList<Expr> AnalyseExprRestIdent(){
        final LinkedList<String> validetoken1 = new LinkedList<>(Arrays.asList("and","or","NEWLINE",")",":",",","=","[","]","<=",">=",">","<","!=","==","+","-","*","//","%")) ;

        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("(")){
            tokenQueue.poll();
            LinkedList<Expr> exprs = AnalyseExprEtoileVirgule();


            currentToken = tokenQueue.poll();
            if(currentToken.getSymbole().equals(")")){
                return exprs;
            }
            else{
                 throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
            }
        }
        else if(validetoken1.contains(currentToken.getSymbole())){
            return null;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private LinkedList<Expr> AnalyseExprEtoileVirgule(){
        
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals("ident")){
            return AnalyseExprPlusVirgule();
        }
        else if (currentToken.getSymbole().equals(")")){
            return null;
        } 
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private LinkedList<Expr> AnalyseExprPlusVirgule(){
        final ArrayList<String> validetoken = new ArrayList<>(Arrays.asList("ident","(","[","not", "integer", "string", "True", "False", "None")) ;
        
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.peek();

        if (validetoken.contains(currentToken.getSymbole())){
            Expr expr = AnalyseExpr();
            LinkedList<Expr> exprs = AnalyseExprPlusVirguleRest();

            if (exprs == null){
                LinkedList<Expr> newExprs = new LinkedList<>();
                newExprs.add(expr);
                return newExprs;
            }
            else {
                exprs.addFirst(expr);
                return exprs;
            }
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private LinkedList<Expr> AnalyseExprPlusVirguleRest(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }

        Token currentToken = tokenQueue.peek();

        if (currentToken.getSymbole().equals(",")){
            tokenQueue.poll();
            return AnalyseExprPlusVirgule();
        }
        else if(currentToken.getSymbole().equals(")")){
            return null;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private AddBinop AnalyseBinopAdd(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.poll();

        if (currentToken.getSymbole().equals("+")){
            return AddBinop.ADD;
        }
        else if (currentToken.getSymbole().equals("-")){
            return AddBinop.SUB;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private MutBinop AnalyseBinopMut(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.poll();

        if (currentToken.getSymbole().equals("*")){
            return MutBinop.MULT;
        }
        else if (currentToken.getSymbole().equals("//")){
            return MutBinop.DIV;
        }
        else if (currentToken.getSymbole().equals("%")){
            return MutBinop.MOD;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private CompBinop AnalyseBinopComp(){
        final LinkedList<String> validetoken = new LinkedList<>(Arrays.asList("<=",">=",">","<","!=","==")) ;
        
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.poll();

        if (validetoken.contains(currentToken.getSymbole())){
            switch (currentToken.getSymbole()) {
                case "<" :
                    return CompBinop.LESS_THAN;
                case "<=" :
                    return CompBinop.LESS_EQUAL;
                case ">" :
                    return CompBinop.GREATER_THAN;
                case ">=" :
                    return CompBinop.GREATER_EQUAL;
                case "==" :
                    return CompBinop.EQUAL;
                case "!=" :
                    return CompBinop.NOT_EQUAL;
                default:
                    return null;
            }
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }
    }

    private void AnalyseBinopAnd(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.poll();

        if (currentToken.getSymbole().equals("and")){
            return;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }

    }

    private void AnalyseBinopOr(){
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.poll();

        if (currentToken.getSymbole().equals("or")){
            return;
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }

    }

    private Const AnalyseConst(){
        final LinkedList<String> validetoken = new LinkedList<>(Arrays.asList("integer", "string", "True", "False", "None")) ;
        
        if (tokenQueue.isEmpty()) {
            throw new AnalyseException("Erreur : pile de tokens vide !");
        }
        
        Token currentToken = tokenQueue.poll();
        
    
        if (validetoken.contains(currentToken.getSymbole())){
            switch (currentToken.getSymbole()) {
                case "integer" :
                    return new IntegerType(Integer.valueOf(currentToken.getValue()));
                case "string" :
                    return new StringType(currentToken.getValue());
                case "True" :
                    return new BoolType(true);
                case "False" :
                    return new BoolType(false);
                /*case "None" :
                    return new NoneType();*/
                default:
                    return null;
            }
        }
        else{
            throw new AnalyseException("Erreur non reconnue, ligne : " + currentToken.getLine());
        }

    }
}