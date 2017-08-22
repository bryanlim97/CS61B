package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;
import java.util.Collection;
import java.util.ArrayList;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Bryan Lim
 */
public class MachineTest {
    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    private void checkMachine(Alphabet alpha, int numRotors, int pawls,
        Collection<Rotor> allRotors) {
        Machine tested = new Machine(alpha, numRotors, pawls, allRotors);
        String [] myRotor = new String[5];
        myRotor[0] = "B";
        myRotor[1] = "Beta";
        myRotor[2] = "III";
        myRotor[3] = "IV";
        myRotor[4] = "I";
        tested.setPlugboard(_plug);
        tested.insertRotors(myRotor);
        tested.setRotors("AXLE");
        String first;
        first = "QVPQSOKOILPUBKJZPISFXDWBHCNSCXNUOAATZXSRCFYDGUFLPNXGXIXTYJU";
        String second;
        second = "FROMHISSHOULDERHIAWATHATOOKTHECAMERAOFROSEWOODMADEOFSLIDING";
        assertEquals(first, tested.convert(second));
    }

    @Test
    public void simpleMessage() {
        _alpha = UPPER;
        _plug = new Permutation("(HQ) (EX) (IP) (TR) (BY)", _alpha);
        _pawls = 4;
        _numRotors = 5;
        rotorSet();
        checkMachine(_alpha, _numRotors, _pawls, _allRotors);
    }

    public void rotorSet() {
        Rotor r1 = new MovingRotor("I",
            new Permutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)",
            UPPER), "Q");
        Rotor r2 = new MovingRotor("II",
            new Permutation("(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)",
            UPPER), "E");
        Rotor r3 = new MovingRotor("III",
            new Permutation("(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", UPPER), "V");
        Rotor r4 = new MovingRotor("IV",
            new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", UPPER), "J");
        Rotor r5 = new MovingRotor("V",
            new Permutation("(AVOLDRWFIUQ)(BZKSMNHYC) (EGTJPX)", UPPER), "Z");
        Rotor r6 = new MovingRotor("VI",
            new Permutation("(AJQDVLEOZWIYTS) (CGMNHFUX) (BPRK) ",
            UPPER), "ZM");
        Rotor r7 = new MovingRotor("VII",
            new Permutation("(ANOUPFRIMBZTLWKSVEGCJYDHXQ) ", UPPER), "ZM");
        Rotor r8 = new MovingRotor("VIII",
            new Permutation("(AFLSETWUNDHOZVICQ) (BKJ) (GXY) (MPR)",
            UPPER), "ZM");
        Rotor rbet = new FixedRotor("Beta",
            new Permutation("(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", UPPER));
        Rotor rg = new FixedRotor("Gamma",
            new Permutation("(AFNIRLBSQWVXGUZDKMTPCOYJHE)", UPPER));
        Rotor rb = new Reflector("B",
                new Permutation("(AE) (BN) (CK) (DQ) (FU) (GY) (HW) (IJ) (LO) "
                + "(MP) (RX) (SZ) (TV)", UPPER));
        Rotor rc = new Reflector("C",
                new Permutation("(AR) (BD) (CO) (EJ) (FN) (GT) (HK) (IV) (LM) "
                + "(PW) (QZ) (SX) (UY)", UPPER));
        _allRotors = new ArrayList<Rotor>();
        _allRotors.add(r1);
        _allRotors.add(r2);
        _allRotors.add(r3);
        _allRotors.add(r4);
        _allRotors.add(r5);
        _allRotors.add(r6);
        _allRotors.add(r7);
        _allRotors.add(r8);
        _allRotors.add(rbet);
        _allRotors.add(rg);
        _allRotors.add(rb);
        _allRotors.add(rc);
    }

    private Alphabet _alpha;
    private int _numRotors;
    private int _pawls;
    private Permutation _plug;
    private Collection<Rotor> _allRotors;
}
