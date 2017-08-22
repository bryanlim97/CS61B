package enigma;

import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Bryan Lim
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotorsTemp = allRotors;
        _allRotors = new Rotor[_allRotorsTemp.size()];
        _allRotors = _allRotorsTemp.toArray(_allRotors);
        _myRotors = new Rotor[_numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length - 1; i++) {
            for (int j = i + 1; j < rotors.length; j++) {
                if (rotors[i].equals(rotors[j])) {
                    throw new EnigmaException("Duplicate rotors");
                }
            }
        }
        if (rotors.length != _numRotors) {
            throw new EnigmaException("Wrong number of arguments");
        }
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        for (int i = 0; i < _allRotors.length; i++) {
            if (rotors[0].equals(_allRotors[i].name())) {
                _myRotors[0] = _allRotors[i];
                count1++;
            }
        }
        for (int i = 1; i <= _numRotors - _pawls; i++) {
            for (int j = 0; j < _allRotors.length; j++) {
                if (rotors[i].equals(_allRotors[j].name())) {
                    _myRotors[i] = _allRotors[j];
                    count2++;
                }
            }
        }

        for (int i = 2; i < rotors.length; i++) {
            for (int j = 0; j < _allRotors.length; j++) {
                if (rotors[i].equals(_allRotors[j].name())) {
                    _myRotors[i] = _allRotors[j];
                    count3++;
                }
            }
        }
        if (count1 == 0 || count2 == 0 || count3 == 0) {
            throw new EnigmaException("Bad rotor name");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of four
     *  upper-case letters. The first letter refers to the leftmost
     *  rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() < _numRotors - 1) {
            throw new EnigmaException("Setting too short");
        }
        if (setting.length() > _numRotors - 1) {
            throw new EnigmaException("Setting too long");
        }
        for (int i = 1; i < _numRotors; i++) {
            _myRotors[i].set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        for (int i = _numRotors - 1; i > 0; i--) {
            if (_myRotors[i].atNotch()) {
                _myRotors[i - 1].advance();
                if (i != _numRotors - 1) {
                    _myRotors[i].advance();
                }
            }
        }
        _myRotors[_numRotors - 1].advance();
        c = _plugboard.permute(c);
        for (int i = _numRotors - 1; i > 0; i--) {
            c = _myRotors[i].convertForward(c);
        }
        c = _myRotors[0].convertForward(c);
        for (int j = 1; j < _numRotors; j++) {
            c = _myRotors[j].convertBackward(c);
        }
        c = _plugboard.permute(c);
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String temp = "";
        for (int i = 0; i < msg.length(); i++) {
            temp += _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }
        return temp;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors in this machine. */
    private int _numRotors;

    /** Number of pawls in this machine. */
    private int _pawls;

    /** Plugboard of this machine. */
    private Permutation _plugboard;

    /** Temporary storage of collection of rotors. */
    private Collection<Rotor> _allRotorsTemp;

    /** Array of all rotors for easy access. */
    private Rotor[] _allRotors;

    /** Array of my rotors for easy access. */
    private Rotor[] _myRotors;
}
