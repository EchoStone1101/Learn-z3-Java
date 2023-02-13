import com.microsoft.z3.*;

/**
 * #1 Basic examples.
 */

public class GettingStarted {

    @SuppressWarnings("unchecked")
    static void basicSolve() {

        // x = Int('x')
        // y = Int('y')
        // solve(x > 2, y < 10, x + 2*y == 7)

        Util.log("BasicSolve");
        Context ctx = new Context();
        IntExpr x = ctx.mkIntConst("x");
        IntExpr y = ctx.mkIntConst("y");

        Solver s = ctx.mkSolver();
        s.add(ctx.mkGt(x, ctx.mkInt(2)));
        s.add(ctx.mkLt(y, ctx.mkInt(10)));
        s.add(ctx.mkEq(ctx.mkAdd(x, ctx.mkMul(y, ctx.mkInt(2))), ctx.mkInt(7)));

        Util.solve(s);
        ctx.close();
    }

    @SuppressWarnings("unchecked")
    static void basicSimplify() {

        // x = Int('x')
        // y = Int('y')
        // print simplify(x + y + 2*x + 3)
        // print simplify(x < y + x + 2)
        // print simplify(And(x + 1 >= 3, x**2 + x**2 + y**2 + 2 >= 5))

        Util.log("BasicSimplify");

        Context ctx = new Context();
        IntExpr x = ctx.mkIntConst("x");
        IntExpr y = ctx.mkIntConst("y");

        System.out.println(
            ctx.mkAdd(
                x,
                y,
                ctx.mkMul(
                    ctx.mkInt(2),
                    x
                ),
                ctx.mkInt(3)
            ).simplify()
        );

        System.out.println(
            ctx.mkLe(
                x,
                ctx.mkAdd(
                    y,
                    x,
                    ctx.mkInt(2)
            )).simplify()
        );

        System.out.println(
            ctx.mkAnd(
                ctx.mkGe(
                    ctx.mkAdd(
                        x,
                        ctx.mkInt(1)
                    ),
                    ctx.mkInt(3)
                ),
                ctx.mkGe(
                    ctx.mkAdd(
                        ctx.mkPower(x, ctx.mkInt(2)),
                        ctx.mkPower(x, ctx.mkInt(2)),
                        ctx.mkPower(y, ctx.mkInt(2)),
                        ctx.mkInt(2)
                    ),
                    ctx.mkInt(5)
                )
            ).simplify()
        );

        ctx.close();
    }

    @SuppressWarnings("unchecked")
    static void nonLinear() {

        // x = Real('x')
        // y = Real('y')
        // solve(x**2 + y**2 == 3, x**3 == 2)

        Util.log("NonLinear");
        Context ctx = new Context();
        RealExpr x = ctx.mkRealConst("x");
        RealExpr y = ctx.mkRealConst("y");
        
        Solver s = ctx.mkSolver();
        s.add(
            ctx.mkEq(
                ctx.mkAdd(
                    ctx.mkPower(x, ctx.mkInt(2)),
                    ctx.mkPower(y, ctx.mkInt(2))
                ),
                ctx.mkReal(3)
            )
        );
        s.add(
            ctx.mkEq(
                ctx.mkPower(x, ctx.mkInt(3)),
                ctx.mkInt(2)
            )
        );

        Util.solve(s);
        ctx.close();
    }

    @SuppressWarnings("unchecked")
    static void unknownSolve() {
        Util.log("UnknownSolve");
        var ctx = new Context();
        var x = ctx.mkRealConst("x");

        Solver s = ctx.mkSolver();
        var exp = ctx.mkEq(
            ctx.mkPower(ctx.mkInt(2), x),
            ctx.mkReal(4)
        );
        s.add(exp);
        System.out.println(exp.getSExpr());

        Util.solve(s);
        ctx.close();
    }

    public static void main(String[] args) {
        basicSolve();
        basicSimplify();
        nonLinear();
        unknownSolve();
    }
}
