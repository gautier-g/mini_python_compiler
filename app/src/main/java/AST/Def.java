package AST;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import AST.SimpleStmt.Expr.TermExpr.Ident;

public class Def implements Node {
    private Ident ident;
    private List<Ident> idents;
    private Suite suite;

    public Def() {
        this.idents = new ArrayList<>();
    }

    public Ident getIdent() {
        return ident;
    }

    public void setIdent(Ident ident) {
        this.ident = ident;
    }

    public List<Ident> getIdents() {
        return idents;
    }

    public void setIdents(List<Ident> idents) {
        this.idents = idents;
    }

    public Suite getSuite() {
        return suite;
    }

    public void setSuite(Suite suite) {
        this.suite = suite;
    }

    public void vizualisation(BufferedWriter writer, String nodeName) throws IOException {
        writer.write("  " + nodeName + " [label=\"DEF\"];\n");

        
        if (this.ident != null) {
            String identNodeName = nodeName + "_ident"; 
            writer.write("  " + nodeName + " -- " + identNodeName + ";\n");
            ident.vizualisation(writer, identNodeName);
        }


        if (idents != null && !idents.isEmpty()){
            String paramNodeName = nodeName + "_PARAM";
            writer.write("  " + nodeName + " -- " + paramNodeName + ";\n");
            writer.write("  " + paramNodeName + " [label=\"PARAM\"];\n");
            for (Ident ident_ : idents){
                String childNodeName = paramNodeName + "_" + ident_.hashCode(); 
                writer.write("  " + paramNodeName + " -- " + childNodeName + ";\n");
                ident_.vizualisation(writer,childNodeName); 
            }
        }

        if (this.suite != null) {
            String suiteNodeName = nodeName + "_suite"; 
            writer.write("  " + nodeName + " -- " + suiteNodeName + ";\n");
            suite.vizualisation(writer, suiteNodeName); 
        }
    }

    public Def simplify(){
        suite = suite.simplify();
        idents = idents.stream()
                    .map(elt -> elt.simplify()) 
                    .collect(Collectors.toList());  
        return this;    
    }
}
