import com.microsoft.z3.*;
import java.io.*;

public class Util {

    // Shorthand for checking and printing results
    static void solve(Solver s) {
        Status res = s.check();
        System.out.println(res);
        switch (res) {
            case SATISFIABLE: 
                System.out.println(s.getModel());
                break;
            case UNSATISFIABLE: 
                System.out.println(s.getUnsatCore());
                break;
            case UNKNOWN: 
                System.out.println(s.getReasonUnknown());
                break;
        }
    }

    static void solve(Solver s, BoolExpr[] premises) {
        Status res = s.check(premises);
        System.out.println(res);
        switch (res) {
            case SATISFIABLE: 
                System.out.println(s.getModel());
                break;
            case UNSATISFIABLE: 
                System.out.println(s.getUnsatCore());
                break;
            case UNKNOWN: 
                System.out.println(s.getReasonUnknown());
                break;
        }
    }

    static void log(String msg) {
        System.out.println("\n===========================");
        System.out.println(msg);
        System.out.println("===========================");
    }

    // Shorthand for proving given set of formulas, by negating all assertions
    @SuppressWarnings("unchecked")
    static void prove(Context ctx, Solver s) {
        var theorem = ctx.mkBool(true);

        for (var as: s.getAssertions()) {
            theorem = ctx.mkAnd(theorem, as);
        }
        s.reset();
        s.add(ctx.mkNot(theorem));

        solve(s);
    }

    @SuppressWarnings("unchecked")
    static void prove(Context ctx, Solver s, BoolExpr[] premises) {
        var theorem = ctx.mkBool(true);

        for (var as: s.getAssertions()) {
            theorem = ctx.mkAnd(theorem, as);
        }
        s.reset();
        s.add(ctx.mkNot(theorem));

        solve(s, premises);
    }

    static void printSExpr(Solver s) {
        System.out.println("###########");
        for (var as: s.getAssertions()) {
            System.out.println(as.getSExpr());
        }
        System.out.println("###########");
    }

    static void dumpBitBlast(Context ctx, Solver s, String path, boolean simplify) 
        throws IOException {
        var tac = ctx.mkTactic("bit-blast");
        var goal = ctx.mkGoal(false, false, false);
        for (var as: s.getAssertions()) {
            goal.add(as);
        }

        if (simplify)
            goal = goal.simplify();

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));

        var res = tac.apply(goal);
        for (var g: res.getSubgoals()) {
            writer.write(g.toString() + "\n");
        }

        writer.close();
    }
}
