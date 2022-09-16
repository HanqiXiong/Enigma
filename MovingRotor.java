package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Hanqi Xiong
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initially in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    boolean atNotch() {
        for (int i = 0; i < notches().length(); i++) {
            if (alphabet().toInt(notches().charAt(i)) == setting()) {
                return true;
            }
        }
        return false;
    }
    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        set(wrap(setting() + 1, size()));
    }

    @Override
    String notches() {
        return _notches;
    }

    /** Notches of the rotor. */
    private final String _notches;

}
