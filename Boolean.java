import com.microsoft.z3.*;

/**
 * #2 Boolean Logic
 */

public class Boolean {

    @SuppressWarnings("unchecked")
    static void deMorgan() {
        // Verify the deMorgan theorem

        Util.log("DeMorgan");
        Context ctx = new Context();

        var a = ctx.mkBoolConst("a");
        var b = ctx.mkBoolConst("b");

        Solver s = ctx.mkSolver();

        var exp = ctx.mkEq(
            ctx.mkAnd(a, b),
            ctx.mkNot(
                ctx.mkOr(
                    ctx.mkNot(a),
                    ctx.mkNot(b)
                )
            )
        );
        s.add(ctx.mkNot(exp));

        Util.solve(s);
        ctx.close();
    }

    public static void main(String[] args) {
        deMorgan();
    }
}
