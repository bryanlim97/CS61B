package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Bryan Lim
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine M = readConfig();
        String tempMSG = "";
        int count = 0;
        while (_input.hasNext()) {
            if (_input.next().equals("*")) {
                setUp(M, _input.nextLine());
                count++;
            }
            while (!_input.hasNext("\\*") && _input.hasNextLine()) {
                tempMSG += _input.nextLine().toUpperCase();
                tempMSG = tempMSG.replaceAll("\\s", "");
                printMessageLine(M.convert(tempMSG));
                tempMSG = "";
                if (_input.hasNextLine()) {
                    _output.println();
                }
            }
        }
        if (count == 0) {
            throw new EnigmaException("No configuration!");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String nothing = _config.next();
            int howManyRotors = _config.nextInt();
            int howManyPawls = _config.nextInt();
            _alphabet = new UpperCaseAlphabet();
            Collection<Rotor> everyRotor = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                String tempName = _config.next().toUpperCase();
                String tempDesig = _config.next();
                String tempNotches = "";
                String tempPerm = "";
                while (_config.hasNext("\\(.*")) {
                    tempPerm += _config.next();
                    tempPerm += " ";
                }
                if (tempDesig.charAt(0) == 'M') {
                    for (int i = 1; i < tempDesig.length(); i++) {
                        tempNotches += tempDesig.charAt(i);
                    }
                    Rotor thisRotor = new MovingRotor(tempName,
                        new Permutation(tempPerm, _alphabet), tempNotches);
                    everyRotor.add(thisRotor);
                } else if (tempDesig.charAt(0) == 'N') {
                    Rotor thisRotor = new FixedRotor(tempName,
                        new Permutation(tempPerm, _alphabet));
                    everyRotor.add(thisRotor);
                } else if (tempDesig.charAt(0) == 'R') {
                    Rotor thisRotor = new Reflector(tempName,
                        new Permutation(tempPerm, _alphabet));
                    everyRotor.add(thisRotor);
                }
            }
            return new Machine(_alphabet, howManyRotors,
                howManyPawls, everyRotor);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            return null;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner settingsScan = new Scanner(settings);
        String [] insert = new String[M.numRotors()];
        String rotorset = "";
        String cycling = "";
        int count = 0;
        for (int i = 0; i < M.numRotors(); i++) {
            insert[i] = settingsScan.next();
        }
        rotorset = settingsScan.next();
        while (settingsScan.hasNext() && settingsScan.hasNext("\\(.*")) {
            cycling += settingsScan.next();
            cycling += " ";
        }
        M.setPlugboard(new Permutation(cycling, new UpperCaseAlphabet()));
        M.insertRotors(insert);
        M.setRotors(rotorset);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String printMSG = "";
        int msgLength = msg.length();
        int groups = msgLength / 5;
        int leftover = msgLength - (groups * 5);
        for (int i = 0; i < groups; i++) {
            printMSG += msg.substring(i * 5, (i * 5) + 5);
            printMSG += " ";
        }
        if (leftover > 0) {
            printMSG += msg.substring(msgLength - leftover);
        }
        _output.print(printMSG);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
