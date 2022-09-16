package enigma;

import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Hanqi Xiong
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 < PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors.toArray();
        _rotors = new Rotor[_numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    void insertRing(String ring) {
        for (int i = 1; i < numRotors(); i++) {
            getRotor(i).setring(ring.charAt(i - 1));
        }
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        if (k > _numRotors) {
            throw EnigmaException.error("wrong index of rotor");
        }
        return _rotors[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        boolean [] check = new boolean[rotors.length];
        for (int i = 0; i < rotors.length; i++) {
            for (Object allRotor : _allRotors) {
                if ((rotors[i]).equals((((Rotor) allRotor).name()))) {
                    _rotors[i] = (Rotor) allRotor;
                    check[i] = true;
                }
            }
        }
        for (boolean b : check) {
            if (!b) {
                throw error("unmatched rotor");
            }
        }
        if (_rotors.length != rotors.length) {
            throw error("There can't be unnamed rotor");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw error("must be a string of numRotors() - 1");
        }
        for (int i = 1; i < _rotors.length; i++) {
            if (!_alphabet.contains(setting.charAt(i - 1))) {
                throw error("character not in alphabet");
            }
            _rotors[i].set(setting.charAt(i - 1));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean[] advanceRotors = new boolean[_rotors.length];
        advanceRotors[_rotors.length - 1] = true;
        if (_rotors[0] == null) {
            throw error("There must be rotors to advance");
        }
        for (int i = _rotors.length - 1; i > 0; i--) {
            if (_rotors[i].atNotch() && _rotors[i - 1].rotates()) {
                advanceRotors[i] = true;
                advanceRotors[i - 1] = true;
            }
        }
        for (int i = 0; i < _rotors.length; i++) {
            if (advanceRotors[i]) {
                _rotors[i].advance();
            }
        }
    }
    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        if (c < 0 || c >= alphabet().size()) {
            throw error("wrong index number");
        }
        for (int i = _numRotors - 1; i >= 0; i--) {
            c = _rotors[i].convertForward(c);
        }
        for (int i = 1; i < _numRotors; i++) {
            c = _rotors[i].convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replace(" ", "");
        StringBuilder convertedMsg = new StringBuilder();
        for (int i = 0; i < msg.length();) {
            if (msg.charAt(i) == ' ') {
                convertedMsg.append(" ");
            } else {
                int converted = convert(_alphabet.toInt(msg.charAt(i)));
                convertedMsg.append(_alphabet.toChar(converted));
            }
            i++;
        }
        return convertedMsg.toString();
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors this machine has. */
    private final int _numRotors;

    /** Number of pawls this machine has. */
    private final int _pawls;

    /** Present all rotors. */
    private final Object [] _allRotors;

    /** Present the rotors this machine has. */
    private final Rotor [] _rotors;

    /** Permutation of plugboard. */
    private Permutation _plugboard;

}
