package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Bryan Lim
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters not
     *  included in any cycle map to themselves. Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        numArrays = 0;
        for (int i = 0; i < cycles.length(); i++) {
            if (cycles.charAt(i) == '(') {
                numArrays++;
            }
        }
        if (numArrays > 0) {
            _cycleArray = new String[numArrays];
            temp = Character.toString(cycles.charAt(1));
            int x = 0;
            for (int j = 2; j < cycles.length(); j++) {
                if (cycles.charAt(j) == ')' && (j == cycles.length() - 1
                    || j == cycles.length() - 2)) {
                    _cycleArray[x] = temp;
                    x++;
                } else if (cycles.charAt(j) == ')') {
                    _cycleArray[x] = temp;
                    temp = Character.toString(cycles.charAt(j + 3));
                    x++;
                } else if (cycles.charAt(j) == ' ') {
                    j += 2;
                    continue;
                } else {
                    temp += cycles.charAt(j);
                }
            }
        } else {
            _cycleArray = new String[0];
        }
    }


    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        tempArray = new String[numArrays + 1];
        for (int i = 0; i < numArrays; i++) {
            tempArray[i] = _cycleArray[i];
        }
        tempArray[temp.length() - 1] = cycle;
        _cycleArray = tempArray;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(p);
        char myChar = _alphabet.toChar(p);
        for (int i = 0; i < numArrays; i++) {
            if (_cycleArray[i].contains("" + myChar)) {
                if (_cycleArray[i].indexOf(myChar)
                    == _cycleArray[i].length() - 1) {
                    return _alphabet.toInt(_cycleArray[i].charAt(0));
                } else {
                    return _alphabet.toInt(_cycleArray[i].
                    charAt(_cycleArray[i].indexOf(myChar) + 1));
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);
        char myChar = _alphabet.toChar(c);
        for (int i = 0; i < numArrays; i++) {
            if (_cycleArray[i].contains("" + myChar)) {
                if (_cycleArray[i].indexOf(myChar) == 0) {
                    return _alphabet.toInt(
                    _cycleArray[i].charAt(_cycleArray[i].length() - 1));
                } else {
                    return _alphabet.toInt(_cycleArray[i].
                    charAt(_cycleArray[i].indexOf(myChar) - 1));
                }
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int initVal = _alphabet.toInt(p);
        int newVal = permute(initVal);
        return _alphabet.toChar(newVal);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int initVal = _alphabet.toInt(c);
        int newVal = invert(initVal);
        return _alphabet.toChar(newVal);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < numArrays; i++) {
            if (_cycleArray[i].length() == 1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** String array of cycles. */
    private String [] _cycleArray;

    /** Temp variable for storing strings. */
    private String temp;

    /** Temp array for adding new cycles. */
    private String [] tempArray;

    /** Number of arrays/groups of cycles. */
    private int numArrays;

    /** Cycles in the form of "(ccc) (cc) ... ". */
    private String _cycles;
}
