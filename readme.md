# Quantum Simulator

A small quantum circuit simulator: define circuits in a simple text format,
run them, and see the probability histogram of the measurement outcomes.

Two ways to use it:

- **JAR file only** (end user) — see [Usage with the JAR](#usage-with-the-jar)
- **Full codebase** (developer) — see [Developer section](#developer-section)

---

## Usage with the JAR

### Requirements

- A Java runtime (JDK/JRE 17+; built with Kotlin/JVM toolchain 23, runs on any modern JRE)
- The single file `quantum-simulator-1.0-SNAPSHOT.jar` (self-contained, ~3.6 MB, no other files needed)

### Running

From a terminal in the folder containing the jar:

```bash
java -jar quantum-simulator-1.0-SNAPSHOT.jar                # interactive picker
java -jar quantum-simulator-1.0-SNAPSHOT.jar mycircuit.qcirc # run a file directly
java -jar quantum-simulator-1.0-SNAPSHOT.jar mycircuit.qcirc 1000   # ... with 1000 runs
```

- **No arguments** → lists all `.qcirc` files in the current folder, you pick one by
  number, then you are asked how many runs to simulate (empty = 1 run).
- **With arguments** → `args[0]` = circuit file (relative to the current folder or
  absolute), `args[1]` = number of runs (optional, default 1).

Note: double-clicking the jar does not work reliably on Windows (no `.jar` file
association, and `javaw` opens no console). Use the terminal, or create a
`run.bat` next to the jar containing `@java -jar "%~dp0quantum-simulator-1.0-SNAPSHOT.jar" %*`.

### Output

After the runs, a histogram of outcome probabilities is printed, one line per
possible bitstring (zero-probability outcomes are listed too), with the bar
scaled so that probability 1.0 = 20 blocks:

```
Results for interesting.qcirc over 4000 runs:
000 | █████                0.252 (1006/4000)
001 | ████                 0.249 (996/4000)
010 |                      0.000 (0/4000)
...
111 | █████                0.254 (1017/4000)
```

### The `.qcirc` circuit format

- The **first non-comment line** is a single integer: the number of qubits (≥ 1).
- Blank lines and lines starting with `#` are ignored, anywhere in the file.
- Each other line is a command followed by space-separated arguments.
- Qubit indices are 0-based; **qubit 0 is the leftmost bit** in outcome strings.

| Command | Args | Meaning |
|---|---|---|
| `I` `X` `Z` `H` `S` `T` | `qubit` | single-qubit gate on that qubit |
| `P` | `theta qubit` | phase gate with angle θ (radians) |
| `CNOT` / `CX` | `control target` | controlled-X |
| `CZ` `CH` `CS` `CT` | `control target` | controlled-Z/H/S/T |
| `CP` | `theta control target` | controlled-phase |
| `MEASURE` | (none) | measure all qubits (full collapse) |
| `MEASURE` | `qubit` | measure one qubit |
| `INITSTATE BASIS \|bits>` | e.g. `\|001>` | set start state to a basis state |
| `INITSTATE FILE name` | e.g. `myState.txt` | set start state from a file |

Notes:

- `INITSTATE` may appear anywhere and multiple times; the **last one wins**.
- The two qubits of a two-qubit gate must be different and in range.
- All errors are reported with the line number, e.g.
  `[3]: Command H expects a number (qubit) between 0 and 1 (numQubits) as argument number 1, but got 5`.

#### `INITSTATE FILE` state-file format

One line per basis state |i⟩ (i = 0 … 2ⁿ−1), in order, each line:

```
<real> <imag>
```

Example for 2 qubits (`(|00⟩ + |11⟩)/√2`, the Bell state b00):

```
0.7071067811865476 0
0 0
0 0
0.7071067811865476 0
```

The file must have exactly 2ⁿ lines and describe a state with norm ≈ 1
(tolerance 1e-2, so 3 decimals like `0.707` are fine).

### Example

`interesting.qcirc` — Bell pair on qubits 0,1 plus an independent H on qubit 2:

```
3
H 0
CNOT 0 1
H 2
MEASURE
```

Only outcomes `000 001 110 111` can occur (~25% each) — the Bell correlation
means qubits 0 and 1 are always equal.

---

## Developer section

### Build

```bash
./gradlew clean jar     # self-contained runnable jar -> build/libs/quantum-simulator-1.0-SNAPSHOT.jar
./gradlew test          # run the full test suite
./gradlew run           # run the CLI from source (interactive picker)
./gradlew run --args="path/to/circuit.qcirc 1000"
./gradlew installDist   # alternative: distribution with launcher scripts in build/install/
```

The default `jar` task is configured as a **fat jar**: it bundles the runtime
dependencies (EJML) and sets `Main-Class: dk.maxkandersen.MainKt`, so the single
jar runs with plain `java -jar`. `JavaExec` tasks get `standardInput = System.in`
so the interactive picker works under `gradlew run`.

### Project layout

```
src/main/kotlin/
  Main.kt                     CLI UI: arg or interactive picker, prints histogram
  qubit/QState.kt             quantum state vector (2^n x 1 complex), validation, probabilities()
  action/Action.kt            interface: apply(QState): QState
  action/gate/                Gate interface + I/X/Z/H/S/T/P, CtrlGate (+CX/CZ/CH/CS/CT/CP),
                              SwapGate, MultiGate, expansion helpers (expand/expandWithID)
  action/measure/             Measurement, SingleQubitMeasurement, FullMeasurement (arbitrary basis)
  circuit/QCircuit.kt         ordered Action list, run(), start state, dimension checks
  parsing/QParser.kt          .qcirc parser (commands, INITSTATE, comments, line-numbered errors)
  parsing/paths/              PathStrategy + RelativePathStrategy(prefix) / ResourcePathStrategy(prefix) / NoPathJustTextStrategy
  parsing/exception/          CircuitParseException hierarchy (line, command, args as vals)
  simulator/Simulator.kt      backend: loads via RelativePathStrategy only, run(file, runs), listCircuitFiles()
  simulator/SimulationResult.kt  histogram data + pretty rendering (20-block bars)
  random/                     RandomStrategy, StandardRandomStrategy (injectable for tests)
  util/                       kron, mmul, ediv, put, expand, getProjMatrix, ... (all require-checked)
src/test/...                  mirrors main; test resources in src/test/resources/
```

### Key design points

- **State**: `QState` wraps a `ZMatrixRMaj` column vector; constructor enforces
  column shape, 2ⁿ rows, and norm 1 (within `QState.NORM_TOLERANCE = 1e-2`, so
  3-decimal state files are accepted). Qubit 0 is the most significant bit.
- **Purity**: gates and measurements return new `QState`s; `QCircuit.run()` is
  idempotent, so running a circuit N times is safe.
- **Validation**: `require` checks everywhere (dimensions, qubit ranges, square
  matrices, division by zero, ...), each with a descriptive message; parser
  errors use the `CircuitParseException` hierarchy with line numbers.
- **Probabilities**: `QState.probabilities()` returns |amplitude|² for **all**
  2ⁿ bitstrings (0.0 included); `Simulator` averages these over N runs.
- **Randomness**: `RandomStrategy` is injectable (parser → measurements;
  `FixedRandomStrategy` in tests makes runs deterministic).
- **Path strategies**: `RelativePathStrategy(prefix)` reads files under a
  filesystem prefix (separator auto-appended); `ResourcePathStrategy(prefix)`
  reads classpath resources; `NoPathJustTextStrategy` treats the "filename" as
  literal text (handy for parser tests). `Simulator` is locked to
  `RelativePathStrategy`.

### Extending

- **New gate**: implement `SingleQubitGate` (or `DoubleQubitGate` for two-qubit
  gates) and add a command branch in `QParser.doParse`.
- **New command**: add a branch to the `when` in `doParse`; throw
  `NumArgsException`/`InvalidArgException`/`UnknownCommandException` for bad input.
- **New path source**: implement `PathStrategy` and pass it to `QParser`.
- **Tests**: mirror the package under `src/test/kotlin`, use
  `NoPathJustTextStrategy` for parser tests and temp dirs/resources for file tests.

---

## Disclaimer

This application was produced with the assistance of **Mistral Vibe** — the Mistral AI coding agent that reads your codebase, writes and tests code, and runs your tools, all from the terminal.

- Website: <https://mistral.ai/vibe>
- Source: <https://github.com/mistralai/mistral-vibe>

If you're building something yourself and want an agent that actually does the work — give Vibe a try.

(This section was written by Mistral Vibe)
