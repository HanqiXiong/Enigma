package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.NoSuchElementException;


import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Hanqi Xiong
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
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
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
        Machine machine = readConfig();
        while (_input.hasNextLine()) {
            String next = _input.nextLine();
            if (next.isEmpty()) {
                _output.println();
            }
            if (next.startsWith("*")) {
                setUp(machine, next);
            } else {
                printMessageLine(machine.convert(next));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphabet = _config.next();
            _alphabet = new Alphabet(alphabet);
            if (!_config.hasNextInt()) {
                throw error("there must be numbers of rotors");
            }
            int rotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw error("there must be numbers of pawls");
            }
            int pawls = _config.nextInt();
            if (pawls >= rotors) {
                throw error("pawls error");
            }
            ArrayList<Rotor> roTors = new ArrayList<>();
            while (_config.hasNext()) {
                Rotor rotor = readRotor();
                roTors.add(rotor);
            }
            return new Machine(_alphabet, rotors, pawls, roTors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String notch = _config.next();
            String cycle = "";
            while (_config.hasNext("\\(.*\\)")) {
                cycle = cycle.concat((_config.next() + " "));
            }
            Permutation permutation = new Permutation(cycle, _alphabet);
            if (notch.isEmpty()) {
                throw error("there can't be empty notch");
            }
            if (notch.charAt(0) == 'M') {
                return new MovingRotor(name, permutation, notch.substring(1));
            } else if (notch.charAt(0) == 'N') {
                return new FixedRotor(name, permutation);
            } else if (notch.charAt(0) == 'R') {
                return new Reflector(name, permutation);
            } else {
                throw error("must be one of rotor type");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String [] setting = settings.trim().split(" ");
        String [] rotors = new String [M.numRotors()];
        String plugboard = "";
        if (!setting[0].equals("*")) {
            throw error("There must be * at the beginning");
        }
        System.arraycopy(setting, 1, rotors, 0, M.numRotors());
        for (int i = 0; i < rotors.length - 1; i++) {
            for (int j = i + 1; j < rotors.length; j++) {
                if (rotors[i].equals(rotors[j])) {
                    throw error("duplicated rotors");
                }
            }
        }
        M.insertRotors(rotors);
        if (setting.length - 1 < M.numRotors()) {
            throw error("mismatch settings");
        }
        M.setRotors(setting[M.numRotors() + 1]);
        if (!M.getRotor(0).reflecting()) {
            throw error("first rotor must be reflecting");
        }
        if (M.numRotors() + 2 < setting.length) {
            if (!setting[M.numRotors() + 2].contains("(")) {
                M.insertRing(setting[M.numRotors() + 2]);
                for (int i = M.numRotors() + 3; i < setting.length; i++) {
                    plugboard = plugboard.concat(setting[i] + " ");
                }
            } else {
                for (int i = M.numRotors() + 2; i < setting.length; i++) {
                    plugboard = plugboard.concat(setting[i] + " ");
                }
            }
        } else {
            for (int i = M.numRotors() + 2; i < setting.length; i++) {
                plugboard = plugboard.concat(setting[i] + " ");
            }
        }
        M.setPlugboard(new Permutation(plugboard, _alphabet));
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i += 5) {
            if (msg.length() - i <= 5) {
                _output.println(msg.substring(i));
            } else {
                _output.print(msg.substring(i, i + 5) + " ");
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private final Scanner _input;

    /** Source of machine configuration. */
    private final Scanner _config;

    /** File for encoded/decoded messages. */
    private final PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
