import com.microsoft.z3.*;
import java.io.*;

public class BVSign {

    //! Explores how the sign of BV operations affects
    //! bit-blasting.

    static void basicSign(int width) {
        Util.log("BasicSign");
        var ctx = new Context();
        var tac = ctx.mkTactic("bit-blast");
        var goal = ctx.mkGoal(false, false, false);

        var x = ctx.mkBVConst("x", width);
        var y = ctx.mkBVConst("y", width);
        
        goal.add(
            ctx.mkBVSGT(x, y)
        );

        goal = goal.simplify();
        var res = tac.apply(goal);
        for (var subgoal: res.getSubgoals()) {
            System.out.println(subgoal);
        }
        ctx.close();
    }

    static void basicSignInt(int width) {
        Util.log("BasicSignInt");
        var ctx = new Context();
        var tac = ctx.mkTactic("bit-blast");
        var goal = ctx.mkGoal(false, false, false);

        var x = ctx.mkBVConst("x", width);
        var y = ctx.mkBVConst("y", width);
        
        goal.add(
            ctx.mkGt(ctx.mkBV2Int(x, true), ctx.mkBV2Int(y, true))
        );

        goal = goal.simplify();
        var res = tac.apply(goal);
        for (var subgoal: res.getSubgoals()) {
            System.out.println(subgoal);
        }
        ctx.close();
    }

    static void basicUnsign(int width) {
        Util.log("BasicUnsign");
        var ctx = new Context();
        var tac = ctx.mkTactic("bit-blast");
        var goal = ctx.mkGoal(false, false, false);

        var x = ctx.mkBVConst("x", width);
        var y = ctx.mkBVConst("y", width);
        
        goal.add(
            ctx.mkBVUGT(x, y)
        ); 

        goal = goal.simplify();

        var param = ctx.mkParams();
        param.add("blast_full", true);
        var res = tac.apply(goal, param);
        for (var subgoal: res.getSubgoals()) {
            System.out.println(subgoal);
        }
        ctx.close();
    }

    @SuppressWarnings("all")
    static void testSMT(int width) {
        Util.log("TestSMT");

        var ctx = new Context();
        
        var x = ctx.mkBVConst("x", width);
        var y = ctx.mkBVConst("y", width);
        
        var goal = ctx.mkGoal(false, false, false);
        goal.add(ctx.mkBVULT(
            x, y
        ));

        var param = ctx.mkParams();
        param.add("blast_full", true);
        
        var res = ctx.then(
            ctx.mkTactic("simplify"),
            ctx.mkTactic("propagate-values"),
            ctx.mkTactic("solve-eqs"),
            // ctx.mkTactic("elim-uncnstr"),
            ctx.mkTactic("reduce-bv-size"),
            ctx.mkTactic("ackermannize_bv"),
            // ctx.usingParams(ctx.mkTactic("bit-blast"), param),
            ctx.mkTactic("bit-blast"),
            ctx.mkTactic("aig"),
            ctx.mkTactic("propagate-values")
        ).apply(goal);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("smt/test-2.smt2"));
            for (var g: res.getSubgoals()) {
                writer.write(g.AsBoolExpr().getSExpr() + "\n");
            }
            writer.close();
        } catch (IOException e) {}

        // Solver
        // var s = ctx.mkSolver();
        // var s = ctx.mkSolver(ctx.then(
        //     ctx.mkTactic("simplify"),
        //     ctx.mkTactic("propagate-values"),
        //     ctx.mkTactic("solve-eqs"),
        //     ctx.mkTactic("elim-uncnstr"),
        //     ctx.mkTactic("reduce-bv-size"),
        //     ctx.mkTactic("ackermannize_bv"),
        //     ctx.mkTactic("bit-blast"),
        //     ctx.mkTactic("aig"),
        //     ctx.mkTactic("propagate-values"),
        //     ctx.mkTactic("sat")
        // ));
        // s.add(ctx.mkBVULT(x, ctx.mkBV(1<<(width/2), width)));
        // s.add(ctx.mkBVULT(y, ctx.mkBV(1<<(width/2), width)));
        // s.add(ctx.mkNot(ctx.mkBVMulNoOverflow(x, y, false)));
        // Util.solve(s);

        ctx.close();
    }

    public static void main(String[] args) {

        Global.setParameter("verbose", "10");

        testSMT(8);

    }
}
