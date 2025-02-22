package AST.SimpleStmt.Expr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import AST.SimpleStmt.Expr.TermExpr.Const.BoolType;

public class AndExpr extends OrExpr {
    private List<NotExpr> exprs;

    public AndExpr() {
        this.exprs = new ArrayList<NotExpr>();
    }

    public List<NotExpr> getAndExprs() {
        return exprs;
    }

    public void addAndExpr(NotExpr expr) {
        this.exprs.add(expr);
    }

    public void vizualisation(BufferedWriter writer, String nodeName) throws IOException {
        writer.write("  " + nodeName + " [label=\"AND\"];\n");

        for (NotExpr child : exprs){
            String childNodeName = nodeName + "_" + child.hashCode(); 
            writer.write("  " + nodeName + " -- " + childNodeName + ";\n");
            child.vizualisation(writer,childNodeName); 
        }
    }

    public AndExpr simplify(){
        exprs = exprs.stream()
                     .map(elt -> elt.simplify()) 
                     .collect(Collectors.toList());  

        exprs.removeIf(expr -> expr instanceof BoolType && ((BoolType)expr).getValue());
        if (exprs.isEmpty()) return new BoolType(true); 
        if (exprs.size() == 1) return exprs.get(0);

        for (Expr expr : exprs) {
            if (expr instanceof BoolType && !((BoolType)expr).getValue() ){
                return new BoolType(false); 
            }
        }

        return this;
    }
}
