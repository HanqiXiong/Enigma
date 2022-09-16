package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Hanqi Xiong
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
    }

    void setring(char ring) {
        _ring = alphabet().toInt(ring);
        _hasring = true;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _setting = alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        if (!_hasring) {
            int result = _permutation.permute(wrap(p + _setting, size()));
            if (Main.verbose()) {
                System.err.printf("%c -> ", alphabet().toChar(result));
            }
            return wrap(result - _setting, size());
        } else {
            int temp = wrap(p + _setting - _ring, size());
            int result = _permutation.permute(temp);
            if (Main.verbose()) {
                System.err.printf("%c -> ", alphabet().toChar(result));
            }
            return wrap(result - _setting + _ring, size());

        }
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        if (!_hasring) {
            int result = _permutation.invert(wrap(e + _setting, size()));
            if (Main.verbose()) {
                System.err.printf("%c -> ", alphabet().toChar(result));
            }
            return wrap(result - _setting, size());
        } else {
            int temp = wrap(e + _setting - _ring, size());
            int result = _permutation.invert(temp);
            if (Main.verbose()) {
                System.err.printf("%c -> ", alphabet().toChar(result));
            }
            return wrap(result - _setting + _ring, size());
        }
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return "";
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    final int wrap(int p, int size) {
        int r = p % size;
        if (r < 0) {
            r += size;
        }
        return r;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private final Permutation _permutation;

    /** Common alphabet of my rotors. */
    private int _setting;

    /** The ring of the rotor. */
    private int _ring;

    /** To check if I have a ring. */
    private boolean _hasring;
}
