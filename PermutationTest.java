package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Hanqi Xiong
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private Permutation perm1;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void checkPermutation() {
        perm = new Permutation("(BACD) (EF) (GH)", UPPER);
        assertSame('A', perm.permute('B'));
        assertSame('C', perm.permute('A'));
        assertSame('F', perm.permute('E'));

    }

    @Test
    public void checkInvert() {
        perm = new Permutation("(BACD) (EF) (GH)", UPPER);
        assertSame('B', perm.invert('A'));
        assertSame('A', perm.invert('C'));
        assertSame('E', perm.invert('F'));
    }
    @Test
    public void checkDerangement() {
        perm = new Permutation("(BACD) (EF) (GH)", UPPER);
        perm1 = new Permutation("(ABCD) (E)", UPPER);
        assertTrue(perm.derangement());
        assertFalse(perm1.derangement());
    }
}
