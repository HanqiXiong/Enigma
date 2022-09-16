package enigma;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Hanqi Xiong
 *
 */
class Permutation {
    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    private final String [] _cycles;
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String cycle = cycles.trim();
        cycle = cycle.replace("(", "");
        cycle = cycle.replace(")", " ");
        _cycles = cycle.split(" ");
    }

    /** Return the value of P modulo the size of this permutation.
     * @param size size of the mod
     * @param p parameter of the mod
     * */
    final int wrap(int p, int size) {
        int r = p % size;
        if (r < 0) {
            r += size;
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
        for (String cycle : _cycles) {
            for (int j = 0; j < cycle.length(); j++) {
                if (cycle.charAt(j) == _alphabet.toChar(wrap(p, size()))) {
                    int permute = wrap(j + 1, cycle.length());
                    return _alphabet.toInt(cycle.charAt(permute));
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        for (String cycle : _cycles) {
            for (int j = 0; j < cycle.length(); j++) {
                if (cycle.charAt(j) == _alphabet.toChar(wrap(c, size()))) {
                    int invert = wrap(j - 1, cycle.length());
                    return _alphabet.toInt(cycle.charAt(invert));
                }
            }
        }
        return c;
    }


    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (String cycle : _cycles) {
            if (cycle.length() == 1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private final Alphabet _alphabet;
}
