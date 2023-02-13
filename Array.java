import com.microsoft.z3.*;

/**
 * #4 Array theory
 */

public class Array {

    @SuppressWarnings("unchecked")
    static void basicArray() {
        Util.log("BasicArray");
        var ctx = new Context();

        var s = ctx.mkSolver();
        var a = ctx.mkArrayConst("a", ctx.mkIntSort(), ctx.mkIntSort());
        var x = ctx.mkIntConst("x");
        var y = ctx.mkIntConst("y");

        // Prove: a[y-1] = 1; a[x] = y-x
        s.add(ctx.mkEq(
            ctx.mkSelect(
                ctx.mkStore(a, ctx.mkSub(y, ctx.mkInt(1)), ctx.mkInt(1)),
                x
            ),
            ctx.mkSub(y, x)
        ));

        // Given: y = x + 1
        Util.prove(ctx, s,
            new BoolExpr[]{ctx.mkEq(y, ctx.mkAdd(x, ctx.mkInt(1)))}
        );

        ctx.close();
    }

    @SuppressWarnings("unchecked")
    static void arrayInduction() {
        Util.log("ArrayInduction");
        var ctx = new Context();
        var s = ctx.mkSolver();

        final int LEN = 10000;
        final int INDEX = 10;
        var a = ctx.mkArrayConst("array", ctx.mkIntSort(), ctx.mkIntSort());
        // First, a[0] >= 0
        s.add(ctx.mkGe(
            ctx.mkSelect(a, ctx.mkInt(0)),
            ctx.mkInt(0)
        ));

        for(int i = 0; i < LEN; i++) {
            s.add(ctx.mkLt(
                ctx.mkSelect(a, ctx.mkInt(i)),
                ctx.mkSelect(a, ctx.mkAdd(ctx.mkInt(i), ctx.mkInt(1)))
            ));
        }

        // Prove that a[INDEX] >= INDEX
        s.add(ctx.mkNot(ctx.mkGe(ctx.mkSelect(a, ctx.mkInt(INDEX)), ctx.mkInt(INDEX))));

        // z3 is incapable of applying inductive facts, making this process slow
        // for large `INDEX`
        Util.solve(s);
        ctx.close();
    }

    public static void main(String[] args) {
        basicArray();

        arrayInduction();
    }
}
