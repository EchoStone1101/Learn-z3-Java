import com.microsoft.z3.*;

// java -cp ../z3/build/com.microsoft.z3.jar Basic.java

class Basic {

    @SuppressWarnings("unchecked")
    public static void main(String args[]) {
        com.microsoft.z3.Global.ToggleWarningMessages(true);

        Context ctx = new Context();
        System.out.println("BasicTest");

        // Constraints
        IntExpr x = ctx.mkIntConst("x");
        IntExpr y = ctx.mkIntConst("y");
        
        Solver s = ctx.mkSolver();
        s.add(ctx.mkEq(ctx.mkAdd(x, y), ctx.mkInt(10)),
            ctx.mkEq(ctx.mkAdd(x, ctx.mkMul(y, ctx.mkInt(2))), ctx.mkInt(15))
        );

        // Check
        System.out.println(s.check());
        System.out.println(s.getModel());

        // For gc
        ctx.close();
    } 
}